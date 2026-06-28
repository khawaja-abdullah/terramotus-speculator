package io.github.khawajaabdullah.service;

import io.github.khawajaabdullah.dto.response.EarthquakeRecord;
import io.github.khawajaabdullah.entity.EarthquakeEntity;
import io.github.khawajaabdullah.mapper.EarthquakeMapper;
import io.github.khawajaabdullah.repository.EarthquakeRepository;
import io.quarkus.panache.common.Page;
import io.quarkus.panache.common.Sort;
import jakarta.enterprise.context.ApplicationScoped;
import org.jboss.logging.Logger;

import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
public class EarthquakeService {

  private static final Logger LOGGER = Logger.getLogger(EarthquakeService.class);

  private final EarthquakeMapper earthquakeMapper;
  private final EarthquakeRepository earthquakeRepository;

  public EarthquakeService(EarthquakeMapper earthquakeMapper, EarthquakeRepository earthquakeRepository) {
    this.earthquakeMapper = earthquakeMapper;
    this.earthquakeRepository = earthquakeRepository;
  }

  public void upsert(EarthquakeRecord earthquakeRecord) {
    EarthquakeEntity earthquakeEntity = earthquakeMapper.mapEarthquakeDtoToEntity(earthquakeRecord);
    earthquakeRepository.upsert(earthquakeEntity);
    LOGGER.infof("Upserted earthquake with id: %s", earthquakeEntity.getId());
  }

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

  public List<EarthquakeRecord> findAll(Integer pageNumber, Integer pageSize) {
    if (pageNumber == null && pageSize == null) {
      return findAll();
    } else if (pageNumber == null) {
      return findAll(0, pageSize.intValue());
    } else if (pageSize == null) {
      return findAll(pageNumber.intValue(), 10);
    } else {
      return findAll(pageNumber.intValue(), pageSize.intValue());
    }
  }

  private List<EarthquakeRecord> findAll() {
    return earthquakeRepository.findAll(Sort.descending("time"))
        .stream()
        .map(earthquakeMapper::mapEarthquakeEntityToDto)
        .toList();
  }

  private List<EarthquakeRecord> findAll(int pageNumber, int pageSize) {
    return earthquakeRepository.findAll(Sort.descending("time"))
        .page(Page.of(pageNumber, pageSize))
        .stream()
        .map(earthquakeMapper::mapEarthquakeEntityToDto)
        .toList();
  }

}
