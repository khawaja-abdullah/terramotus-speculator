package io.github.khawajaabdullah.service;

import io.github.khawajaabdullah.dto.response.EarthquakeRecord;
import io.github.khawajaabdullah.util.Constant;
import io.quarkus.scheduler.Scheduled;
import jakarta.enterprise.context.ApplicationScoped;
import org.jboss.logging.Logger;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;

@ApplicationScoped
public class EarthquakeHistoricalPoller {

  private static final Logger LOGGER = Logger.getLogger(EarthquakeHistoricalPoller.class);

  private final SeismicPortalService seismicPortalService;
  private final EarthquakeService earthquakeService;

  public EarthquakeHistoricalPoller(SeismicPortalService seismicPortalService, EarthquakeService earthquakeService) {
    this.seismicPortalService = seismicPortalService;
    this.earthquakeService = earthquakeService;
  }

  @Scheduled(every = "4h")
  void syncRecentEvents() {
    LOGGER.info("Polling earthquake events...");
    LocalDateTime now = LocalDateTime.now(ZoneOffset.UTC);
    List<EarthquakeRecord> earthquakeRecords = seismicPortalService.getHistoricalEvents(
        now.minusHours(5).format(Constant.ISO_ZULU_LOCAL_DATE_TIME),
        now.format(Constant.ISO_ZULU_LOCAL_DATE_TIME)
    );
    LOGGER.infof("Polled %d earthquake events", earthquakeRecords.size());
    earthquakeService.upsertMultiple(earthquakeRecords);
  }

}
