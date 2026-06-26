package io.github.khawajaabdullah.scheduler;

import io.github.khawajaabdullah.client.UsgsClient;
import io.github.khawajaabdullah.dto.usgs.FeatureCollection;
import io.github.khawajaabdullah.service.EarthquakeService;
import io.github.khawajaabdullah.util.Constant;
import io.quarkus.scheduler.Scheduled;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.logging.Logger;

@ApplicationScoped
public class EarthquakePoller {

  private static final Logger LOGGER = Logger.getLogger(EarthquakePoller.class);

  @RestClient
  UsgsClient usgsClient;
  @Inject
  EarthquakeService earthquakeService;

  @Transactional
  @Scheduled(every = "1m")
  void pollAndPersistLastHourEarthquakes() {
    LOGGER.info("Polling earthquake data...");
    FeatureCollection featureCollection = usgsClient.getEarthquakesFeedSummary(Constant.EARTHQUAKE_FEED_TYPE_ALL_HOUR);
    LOGGER.infof("Polled %d earthquake events in last hour", featureCollection.metadata().count());
    earthquakeService.persist(featureCollection);
    LOGGER.info("Persisted earthquake data!");
  }

}
