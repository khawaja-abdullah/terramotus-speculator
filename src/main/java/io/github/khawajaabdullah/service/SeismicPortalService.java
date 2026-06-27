package io.github.khawajaabdullah.service;

import io.github.khawajaabdullah.client.seismicportal.SeismicPortalClient;
import io.github.khawajaabdullah.dto.response.EarthquakeRecord;
import io.github.khawajaabdullah.dto.seismicportal.FeatureCollection;
import io.github.khawajaabdullah.mapper.EarthquakeMapper;
import io.github.khawajaabdullah.util.Constant;
import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import java.util.List;

@ApplicationScoped
public class SeismicPortalService {

  private final SeismicPortalClient seismicPortalClient;
  private final EarthquakeMapper earthquakeMapper;

  public SeismicPortalService(@RestClient SeismicPortalClient seismicPortalClient, EarthquakeMapper earthquakeMapper) {
    this.seismicPortalClient = seismicPortalClient;
    this.earthquakeMapper = earthquakeMapper;
  }

  public List<EarthquakeRecord> getHistoricalEvents(String start, String end) {
    return getHistoricalEvents(Constant.SEISMIC_PORTAL_API_RESPONSE_FORMAT_JSON, start, end, null, null, null, null, null, null, null);
  }

  public List<EarthquakeRecord> getHistoricalEvents(String format, String start, String end, Double minMag, Double maxMag, Integer limit,
                                                    String eventId, Double lat, Double lon, Double maxRadius) {
    FeatureCollection featureCollection =
        seismicPortalClient.getHistoricalEvents(format, start, end, minMag, maxMag, limit, eventId, lat, lon, maxRadius);
    return featureCollection.features()
        .stream()
        .map(earthquakeMapper::mapFeatureToEarthquakeDto)
        .toList();
  }

}
