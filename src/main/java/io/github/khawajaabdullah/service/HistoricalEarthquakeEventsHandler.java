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
public class HistoricalEarthquakeEventsHandler {

  private static final Logger LOGGER = Logger.getLogger(HistoricalEarthquakeEventsHandler.class);

  private final EarthquakeService earthquakeService;

  public HistoricalEarthquakeEventsHandler(EarthquakeService earthquakeService) {
    this.earthquakeService = earthquakeService;
  }

  // TODO: implement last successful sync timestamp watermark + configurable delta window
  @Scheduled(every = "4h")
  void syncRecentEvents() {
    LOGGER.info("Polling earthquake events...");
    LocalDateTime now = LocalDateTime.now(ZoneOffset.UTC);
    List<EarthquakeRecord> earthquakeRecords = earthquakeService.getHistoricalEvents(
        now.minusHours(5).format(Constant.ISO_ZULU_LOCAL_DATE_TIME),
        now.format(Constant.ISO_ZULU_LOCAL_DATE_TIME)
    );
    LOGGER.infof("Polled %d earthquake events", earthquakeRecords.size());
    earthquakeService.upsertMultiple(earthquakeRecords);
  }

}
