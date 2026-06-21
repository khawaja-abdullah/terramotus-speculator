package io.github.khawajaabdullah.scheduler;

import io.github.khawajaabdullah.client.UsgsClient;
import io.github.khawajaabdullah.dto.FeatureCollection;
import io.github.khawajaabdullah.util.Constant;
import io.quarkus.scheduler.Scheduled;
import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.logging.Logger;

@ApplicationScoped
public class EarthquakePoller {

  private static final Logger LOGGER = Logger.getLogger(EarthquakePoller.class);

  @RestClient
  UsgsClient usgsClient;

  @Scheduled(every = "1m")
  void pollLastHourEarthquakes() {
    LOGGER.info("Polling earthquake data...");
    FeatureCollection featureCollection = usgsClient.getEarthquakesFeedSummary(Constant.EARTHQUAKE_FEED_TYPE_ALL_HOUR);
    LOGGER.infof("Count of earthquakes in last hour: %d", featureCollection.metadata().count());
    featureCollection.features().forEach(
        feature -> LOGGER.infof("Earthquake ID: %s, Magnitude: %s, Place: %s, Time: %s",
            feature.id(),
            feature.properties().mag(),
            feature.properties().place(),
            feature.properties().time()
        )
    );
  }

}
