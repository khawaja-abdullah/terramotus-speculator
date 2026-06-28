package io.github.khawajaabdullah.service;

import io.github.khawajaabdullah.dto.response.EarthquakeRecord;
import io.github.khawajaabdullah.dto.seismicportal.FeatureMessage;
import io.github.khawajaabdullah.mapper.EarthquakeMapper;
import io.github.khawajaabdullah.util.Constant;
import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.context.ManagedExecutor;

@ApplicationScoped
public class LiveEarthquakeEventsHandler {

  private final EarthquakeMapper earthquakeMapper;
  private final ManagedExecutor managedExecutor;
  private final EarthquakeService earthquakeService;

  public LiveEarthquakeEventsHandler(EarthquakeMapper earthquakeMapper, ManagedExecutor managedExecutor, EarthquakeService earthquakeService) {
    this.earthquakeMapper = earthquakeMapper;
    this.managedExecutor = managedExecutor;
    this.earthquakeService = earthquakeService;
  }

  public void handle(FeatureMessage featureMessage) {
    EarthquakeRecord earthquakeRecord = earthquakeMapper.mapFeatureToEarthquakeDto(featureMessage.feature());
    if (Constant.FEATURE_MESSAGE_ACTION_CREATE.equals(featureMessage.action())) {
      earthquakeService.broadcastLiveEvent(earthquakeRecord);
    }
    managedExecutor.runAsync(() -> earthquakeService.upsert(earthquakeRecord));
  }

}
