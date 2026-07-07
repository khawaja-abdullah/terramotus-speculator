package io.github.khawajaabdullah.service;

import io.github.khawajaabdullah.client.seismicportal.SeismicPortalRestClient;
import io.github.khawajaabdullah.dto.EarthquakeRecord;
import io.github.khawajaabdullah.dto.seismicportal.FeatureCollection;
import io.github.khawajaabdullah.entity.EarthquakeEntity;
import io.github.khawajaabdullah.mapper.EarthquakeMapper;
import io.github.khawajaabdullah.repository.EarthquakeRepository;
import io.github.khawajaabdullah.util.ApplicationProperties;
import io.github.khawajaabdullah.util.Constant;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.panache.common.Page;
import io.quarkus.panache.common.Sort;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.operators.multi.processors.BroadcastProcessor;
import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.logging.Logger;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
public class EarthquakeServiceImpl implements EarthquakeService {

  private static final Logger LOGGER = Logger.getLogger(EarthquakeServiceImpl.class);

  private final EarthquakeMapper earthquakeMapper;
  private final EarthquakeRepository earthquakeRepository;
  private final SeismicPortalRestClient seismicPortalRestClient;
  private final BroadcastProcessor<EarthquakeRecord> earthquakeRecordBroadcastProcessor = BroadcastProcessor.create();
  private final ApplicationProperties applicationProperties;

  public EarthquakeServiceImpl(EarthquakeMapper earthquakeMapper, EarthquakeRepository earthquakeRepository,
                               @RestClient SeismicPortalRestClient seismicPortalRestClient, ApplicationProperties applicationProperties) {
    this.earthquakeMapper = earthquakeMapper;
    this.earthquakeRepository = earthquakeRepository;
    this.seismicPortalRestClient = seismicPortalRestClient;
    this.applicationProperties = applicationProperties;
  }

  @Override
  public void upsert(EarthquakeRecord earthquakeRecord) {
    EarthquakeEntity earthquakeEntity = earthquakeMapper.mapEarthquakeDtoToEntity(earthquakeRecord);
    earthquakeRepository.upsert(earthquakeEntity);
    LOGGER.infof("Upserted earthquake with id: %s", earthquakeEntity.getId());
  }

  @Override
  public void upsertMultiple(List<EarthquakeRecord> earthquakeRecords) {
    List<EarthquakeEntity> earthquakeEntities = earthquakeRecords.stream()
        .map(earthquakeMapper::mapEarthquakeDtoToEntity)
        .toList();
    earthquakeRepository.upsertMultiple(earthquakeEntities);
    String upsertedIds = earthquakeEntities.stream()
        .map(EarthquakeEntity::getId)
        .collect(Collectors.joining(","));
    LOGGER.infof("Upserted %s earthquakes with idx: %s", earthquakeEntities.size(), upsertedIds);
  }

  @Override
  public List<EarthquakeRecord> getAll(LocalDateTime startTime, LocalDateTime endTime, Integer pageNumber, Integer pageSize) {
    PanacheQuery<EarthquakeEntity> earthquakeEntityPanacheQuery;
    if (startTime != null && endTime != null) {
      earthquakeEntityPanacheQuery = earthquakeRepository.find("time BETWEEN ?1 AND ?2", Sort.descending("time"), startTime, endTime);
    } else if (startTime != null) {
      earthquakeEntityPanacheQuery = earthquakeRepository.find("time >= ?1", Sort.descending("time"), startTime);
    } else if (endTime != null) {
      earthquakeEntityPanacheQuery = earthquakeRepository.find("time <= ?1", Sort.descending("time"), endTime);
    } else {
      earthquakeEntityPanacheQuery = earthquakeRepository.findAll(Sort.descending("time"));
    }
    if (pageNumber != null && pageSize != null) {
      earthquakeEntityPanacheQuery = earthquakeEntityPanacheQuery.page(Page.of(pageNumber, pageSize));
    } else if (pageNumber != null) {
      earthquakeEntityPanacheQuery = earthquakeEntityPanacheQuery.page(Page.of(pageNumber, 10));
    } else if (pageSize != null) {
      earthquakeEntityPanacheQuery = earthquakeEntityPanacheQuery.page(Page.of(0, pageSize));
    }
    return earthquakeEntityPanacheQuery.list()
        .stream()
        .map(earthquakeMapper::mapEarthquakeEntityToDto)
        .toList();
  }

  public int backfillGap() {
    LocalDateTime from = earthquakeRepository.getMaxTime();
    LocalDateTime to = LocalDateTime.now(ZoneOffset.UTC);
    if (from == null) {
      from = to.minusHours(applicationProperties.webSocket().seismicPortal().backfillHours());
    }
    List<EarthquakeRecord> earthquakeRecords = getHistoricalEvents(
        from.format(Constant.ISO_ZULU_LOCAL_DATE_TIME),
        to.format(Constant.ISO_ZULU_LOCAL_DATE_TIME)
    );
    upsertMultiple(earthquakeRecords);
    return earthquakeRecords.size();
  }


  @Override
  public List<EarthquakeRecord> getHistoricalEvents(String start, String end) {
    return getHistoricalEvents(Constant.SEISMIC_PORTAL_API_RESPONSE_FORMAT_JSON, start, end, null, null, null, null, null, null, null);
  }

  @Override
  public List<EarthquakeRecord> getHistoricalEvents(String format, String start, String end, Double minMag, Double maxMag, Integer limit,
                                                    String eventId, Double lat, Double lon, Double maxRadius) {
    FeatureCollection featureCollection =
        seismicPortalRestClient.getHistoricalEvents(format, start, end, minMag, maxMag, limit, eventId, lat, lon, maxRadius);
    return featureCollection.features()
        .stream()
        .map(earthquakeMapper::mapFeatureToEarthquakeDto)
        .toList();
  }


  @Override
  public void broadcast(EarthquakeRecord earthquakeRecord) {
    earthquakeRecordBroadcastProcessor.onNext(earthquakeRecord);
  }

  @Override
  public Multi<EarthquakeRecord> getStream() {
    return earthquakeRecordBroadcastProcessor;
  }

}
