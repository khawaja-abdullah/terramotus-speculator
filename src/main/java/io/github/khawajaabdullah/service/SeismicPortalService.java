package io.github.khawajaabdullah.service;

import io.github.khawajaabdullah.util.ApplicationConfiguration;
import io.github.khawajaabdullah.client.seismicportal.SeismicPortalClient;
import io.github.khawajaabdullah.client.seismicportal.SeismicPortalWebSocketClient;
import io.github.khawajaabdullah.dto.response.EarthquakeRecord;
import io.github.khawajaabdullah.dto.seismicportal.FeatureCollection;
import io.github.khawajaabdullah.mapper.EarthquakeMapper;
import io.github.khawajaabdullah.util.Constant;
import io.quarkus.runtime.StartupEvent;
import io.quarkus.websockets.next.Closed;
import io.quarkus.websockets.next.WebSocketClientConnection;
import io.quarkus.websockets.next.WebSocketConnector;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.enterprise.event.ObservesAsync;
import jakarta.enterprise.inject.Instance;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.logging.Logger;

import java.net.URI;
import java.util.List;

@ApplicationScoped
public class SeismicPortalService {

  private static final Logger LOGGER = Logger.getLogger(SeismicPortalService.class);

  private final SeismicPortalClient seismicPortalClient;
  private final Instance<WebSocketConnector<SeismicPortalWebSocketClient>> seismicPortalWebSocketClientConnectorInstance;
  private final EarthquakeMapper earthquakeMapper;
  private final ApplicationConfiguration applicationConfiguration;

  public SeismicPortalService(@RestClient SeismicPortalClient seismicPortalClient,
                              Instance<WebSocketConnector<SeismicPortalWebSocketClient>> seismicPortalWebSocketClientConnectorInstance,
                              EarthquakeMapper earthquakeMapper, ApplicationConfiguration applicationConfiguration) {
    this.seismicPortalClient = seismicPortalClient;
    this.seismicPortalWebSocketClientConnectorInstance = seismicPortalWebSocketClientConnectorInstance;
    this.earthquakeMapper = earthquakeMapper;
    this.applicationConfiguration = applicationConfiguration;
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

  void onStart(@Observes StartupEvent startupEvent) {
    connect();
  }

  void onClose(@ObservesAsync @Closed WebSocketClientConnection connection) {
    LOGGER.warnf("Connection [id= %s] to Seismic Portal live stream closed. Reconnecting...", connection.id());
    connect();
  }

  private void connect() {
    seismicPortalWebSocketClientConnectorInstance.get()
        .baseUri(URI.create("wss://%s".formatted(applicationConfiguration.client().seismicPortal().domain())))
        .connect()
        .subscribe()
        .with(
            webSocketClientConnection -> LOGGER.infof("Connected to Seismic Portal live stream! [id= %s]", webSocketClientConnection.id()),
            throwable -> LOGGER.errorf("Connection to Seismic Portal live stream failed with message: %s", throwable.getMessage())
        );
  }

}
