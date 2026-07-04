package io.github.khawajaabdullah.service;

import io.github.khawajaabdullah.client.seismicportal.SeismicPortalWebSocketClient;
import io.github.khawajaabdullah.util.ApplicationProperties;
import io.quarkus.runtime.ShutdownEvent;
import io.quarkus.runtime.StartupEvent;
import io.quarkus.websockets.next.WebSocketClientConnection;
import io.quarkus.websockets.next.WebSocketConnector;
import io.vertx.core.Vertx;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import org.jboss.logging.Logger;

import java.net.URI;

@ApplicationScoped
public class SeismicPortalWebSocketConnectionManager {

  private static final Logger LOGGER = Logger.getLogger(SeismicPortalWebSocketConnectionManager.class);

  private final WebSocketConnector<SeismicPortalWebSocketClient> seismicPortalWebSocketClientWebSocketConnector;
  private final EarthquakeService earthquakeService;
  private final Vertx vertx;

  private final String seismicPortalDomain;
  private final long retryDelay;

  private volatile WebSocketClientConnection currentWebSocketClientConnection;
  private volatile boolean shuttingDown;

  public SeismicPortalWebSocketConnectionManager(WebSocketConnector<SeismicPortalWebSocketClient> seismicPortalWebSocketClientWebSocketConnector,
                                                 EarthquakeService earthquakeService,
                                                 Vertx vertx,
                                                 ApplicationProperties applicationProperties) {
    this.seismicPortalWebSocketClientWebSocketConnector = seismicPortalWebSocketClientWebSocketConnector;
    this.earthquakeService = earthquakeService;
    this.vertx = vertx;
    this.seismicPortalDomain = applicationProperties.client().seismicPortal().domain();
    this.retryDelay = applicationProperties.webSocket().seismicPortal().retryDelay();
  }

  void onStartup(@Observes StartupEvent startupEvent) {
    backfillAndConnect();
  }

  void onShutdown(@Observes ShutdownEvent shutdownEvent) {
    shuttingDown = true;
    if (currentWebSocketClientConnection != null && currentWebSocketClientConnection.isOpen()) {
      currentWebSocketClientConnection.closeAndAwait();
      currentWebSocketClientConnection = null;
    }
  }

  public void backfillAndConnect() {
    if (shuttingDown) return;
    vertx.executeBlocking(earthquakeService::backfillGap)
        .onSuccess(this::handleBackfillSuccess)
        .onFailure(this::handleBackfillFailure);
  }

  private void handleBackfillSuccess(int count) {
    LOGGER.infof("Backfilled %d events", count);
    connect();
  }

  private void handleBackfillFailure(Throwable throwable) {
    LOGGER.errorf("Backfill failed with error=%s, retrying...", throwable.getMessage());
    vertx.setTimer(retryDelay, ignored -> backfillAndConnect());
  }

  private void connect() {
    if (shuttingDown) return;
    seismicPortalWebSocketClientWebSocketConnector
        .baseUri(URI.create("wss://%s".formatted(seismicPortalDomain)))
        .connect()
        .subscribe()
        .with(this::handleConnectSuccess, this::handleConnectFailure);
  }

  private void handleConnectSuccess(WebSocketClientConnection webSocketClientConnection) {
    LOGGER.infof("Connection to web socket successful! connectionId: %s", webSocketClientConnection.id());
    currentWebSocketClientConnection = webSocketClientConnection;
  }

  private void handleConnectFailure(Throwable throwable) {
    LOGGER.errorf("Connection to web socket failed with error=%s, retrying...", throwable.getMessage());
    currentWebSocketClientConnection = null;
    vertx.setTimer(retryDelay, ignored -> backfillAndConnect());
  }

}
