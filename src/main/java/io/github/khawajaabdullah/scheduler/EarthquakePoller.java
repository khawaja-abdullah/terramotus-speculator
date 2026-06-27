package io.github.khawajaabdullah.scheduler;

import io.github.khawajaabdullah.dto.response.EarthquakeRecord;
import io.github.khawajaabdullah.service.EarthquakeService;
import io.github.khawajaabdullah.service.SeismicPortalService;
import io.quarkus.scheduler.Scheduled;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import org.jboss.logging.Logger;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;

@ApplicationScoped
public class EarthquakePoller {

  private static final Logger LOGGER = Logger.getLogger(EarthquakePoller.class);

  private final SeismicPortalService seismicPortalService;
  private final EarthquakeService earthquakeService;

  public EarthquakePoller(SeismicPortalService seismicPortalService, EarthquakeService earthquakeService) {
    this.seismicPortalService = seismicPortalService;
    this.earthquakeService = earthquakeService;
  }

  @Transactional
  @Scheduled(every = "1m")
  void pollAndPersistEarthquakeEventsInLastHour() {
    LOGGER.info("Polling earthquake events...");
    LocalDateTime now = LocalDateTime.now(ZoneOffset.UTC);
    List<EarthquakeRecord> earthquakeRecords = seismicPortalService.getHistoricalEvents(now.minusHours(1).toString(), now.toString());
    LOGGER.infof("Polled %d earthquake events in last hour", earthquakeRecords.size());
    earthquakeService.persistBatch(earthquakeRecords);
    LOGGER.info("Persisted earthquake events!");
  }

}
