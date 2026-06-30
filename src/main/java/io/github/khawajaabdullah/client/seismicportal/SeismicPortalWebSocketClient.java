package io.github.khawajaabdullah.client.seismicportal;

import io.github.khawajaabdullah.dto.EarthquakeRecord;
import io.github.khawajaabdullah.dto.seismicportal.FeatureMessage;
import io.github.khawajaabdullah.mapper.EarthquakeMapper;
import io.github.khawajaabdullah.service.EarthquakeService;
import io.github.khawajaabdullah.service.SeismicPortalWebSocketConnectionManager;
import io.github.khawajaabdullah.util.Constant;
import io.quarkus.websockets.next.*;
import org.jboss.logging.Logger;

@WebSocketClient(path = "/standing_order/websocket")
public class SeismicPortalWebSocketClient {

  private static final Logger LOGGER = Logger.getLogger(SeismicPortalWebSocketClient.class);

  private final EarthquakeMapper earthquakeMapper;
  private final EarthquakeService earthquakeService;
  private final SeismicPortalWebSocketConnectionManager seismicPortalWebSocketConnectionManager;

  public SeismicPortalWebSocketClient(EarthquakeMapper earthquakeMapper,
                                      EarthquakeService earthquakeService,
                                      SeismicPortalWebSocketConnectionManager seismicPortalWebSocketConnectionManager) {
    this.earthquakeMapper = earthquakeMapper;
    this.earthquakeService = earthquakeService;
    this.seismicPortalWebSocketConnectionManager = seismicPortalWebSocketConnectionManager;
  }

  @OnOpen
  void onOpen(WebSocketClientConnection webSocketClientConnection) {
    LOGGER.infof("Connected to web socket! connectionId: %s", webSocketClientConnection.id());
  }

  @OnTextMessage
  void onMessage(FeatureMessage featureMessage) {
    LOGGER.infof("Message received from web socket: %s", featureMessage);
    EarthquakeRecord earthquakeRecord = earthquakeMapper.mapFeatureToEarthquakeDto(featureMessage.feature());
    earthquakeService.upsert(earthquakeRecord);
    // TODO: replace with pub/sub stream
    if (Constant.FEATURE_MESSAGE_ACTION_CREATE.equals(featureMessage.action())) {
      earthquakeService.broadcast(earthquakeRecord);
    }
  }

  @OnError
  void onError(WebSocketClientConnection webSocketClientConnection, Throwable throwable) {
    LOGGER.errorf("Error encountered with web socket connection! connectionId=%s, error=%s", webSocketClientConnection.id(), throwable.getMessage());
    seismicPortalWebSocketConnectionManager.backfillAndConnect();
  }

  @OnClose
  void onClose(WebSocketClientConnection webSocketClientConnection, CloseReason reason) {
    LOGGER.warnf("Connection to web socket closed! connectionId=%s, reason=%s", webSocketClientConnection.id(), reason);
    seismicPortalWebSocketConnectionManager.backfillAndConnect();
  }

}
