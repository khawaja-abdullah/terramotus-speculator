package io.github.khawajaabdullah.client.seismicportal;

import io.quarkus.runtime.StartupEvent;
import io.quarkus.websockets.next.*;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import org.jboss.logging.Logger;

import java.net.URI;

@ApplicationScoped
@WebSocketClient(path = "/standing_order/websocket")
public class SeismicPortalWebSocketClient {

  private static final Logger LOGGER = Logger.getLogger(SeismicPortalWebSocketClient.class);

  private final WebSocketConnector<SeismicPortalWebSocketClient> seismicPortalWebSocketClientWebSocketConnector;

  public SeismicPortalWebSocketClient(WebSocketConnector<SeismicPortalWebSocketClient> seismicPortalWebSocketClientWebSocketConnector) {
    this.seismicPortalWebSocketClientWebSocketConnector = seismicPortalWebSocketClientWebSocketConnector;
  }

  void onStart(@Observes StartupEvent startupEvent) {
    connect();
  }

  @OnOpen
  void onOpen() {
    LOGGER.info("Successfully opened connection to Seismic Portal live stream!");
  }

  @OnTextMessage
  void onTextMessage(String message) {
    LOGGER.infof("Message received from Seismic Portal live stream: %s", message);
  }

  @OnError
  void onError(Throwable throwable) {
    LOGGER.errorf("Connection to Seismic Portal live stream failed: %s", throwable.getMessage());
    connect();
  }

  @OnClose
  void onClose(CloseReason closeReason) {
    LOGGER.warnf("Connection to Seismic Portal live stream closed: %s", closeReason.getMessage());
    connect();
  }

  private void connect() {
    seismicPortalWebSocketClientWebSocketConnector.baseUri(URI.create("wss://www.seismicportal.eu"))
        .connect()
        .subscribe()
        .with(
            webSocketClientConnection -> LOGGER.info("Successfully connected to Seismic Portal live stream!"),
            throwable -> LOGGER.errorf("Connection to Seismic Portal live stream failed: %s", throwable.getMessage())
        );
  }

}
