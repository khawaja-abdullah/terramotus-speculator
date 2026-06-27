package io.github.khawajaabdullah.scheduler;

import io.github.khawajaabdullah.client.seismicportal.SeismicPortalClient;
import io.github.khawajaabdullah.dto.seismicportal.FeatureCollection;
import io.github.khawajaabdullah.service.EarthquakeService;
import io.quarkus.scheduler.Scheduled;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.logging.Logger;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

@ApplicationScoped
public class EarthquakePoller {

  private static final Logger LOGGER = Logger.getLogger(EarthquakePoller.class);

  @RestClient
  SeismicPortalClient seismicPortalClient;
  @Inject
  EarthquakeService earthquakeService;

  @Transactional
  @Scheduled(every = "1m")
  void pollAndPersistEarthquakeEventsInLastHour() {
    LOGGER.info("Polling earthquake events...");
    LocalDateTime now = LocalDateTime.now(ZoneOffset.UTC);
    FeatureCollection featureCollection = seismicPortalClient.getHistoricalEvents("json", now.minusHours(1).toString(), now.toString(), null, null, null, null, null, null, null);
    LOGGER.infof("Polled %d earthquake events in last hour", featureCollection.metadata().count());
    earthquakeService.persist(featureCollection);
    LOGGER.info("Persisted earthquake events!");
  }

}
