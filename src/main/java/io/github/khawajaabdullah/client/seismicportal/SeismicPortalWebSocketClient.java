package io.github.khawajaabdullah.client.seismicportal;

import io.github.khawajaabdullah.dto.seismicportal.FeatureMessage;
import io.github.khawajaabdullah.service.LiveEarthquakeEventsHandler;
import io.quarkus.websockets.next.OnTextMessage;
import io.quarkus.websockets.next.WebSocketClient;
import org.jboss.logging.Logger;

@WebSocketClient(path = "/standing_order/websocket")
public class SeismicPortalWebSocketClient {

  private static final Logger LOGGER = Logger.getLogger(SeismicPortalWebSocketClient.class);

  private final LiveEarthquakeEventsHandler liveEarthquakeEventsHandler;

  public SeismicPortalWebSocketClient(LiveEarthquakeEventsHandler liveEarthquakeEventsHandler) {
    this.liveEarthquakeEventsHandler = liveEarthquakeEventsHandler;
  }

  @OnTextMessage
  void onTextMessage(FeatureMessage featureMessage) {
    LOGGER.infof("Message received from Seismic Portal socket: %s", featureMessage);
    liveEarthquakeEventsHandler.handle(featureMessage);
  }

}
