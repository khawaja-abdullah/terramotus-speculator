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
  private final ApplicationProperties applicationProperties;
  private volatile boolean shuttingDown = false;

  public SeismicPortalWebSocketConnectionManager(WebSocketConnector<SeismicPortalWebSocketClient> seismicPortalWebSocketClientWebSocketConnector,
                                                 EarthquakeService earthquakeService,
                                                 Vertx vertx,
                                                 ApplicationProperties applicationProperties) {
    this.seismicPortalWebSocketClientWebSocketConnector = seismicPortalWebSocketClientWebSocketConnector;
    this.earthquakeService = earthquakeService;
    this.vertx = vertx;
    this.applicationProperties = applicationProperties;
  }

  void onStartup(@Observes StartupEvent startupEvent) {
    backfillAndConnect();
  }

  void onShutdown(@Observes ShutdownEvent shutdownEvent) {
    shuttingDown = true;
  }

  public void backfillAndConnect() {
    if (shuttingDown) return;
    vertx.executeBlocking(earthquakeService::backfillGap)
        .onSuccess(this::onBackfillSuccess)
        .onFailure(this::onBackfillFailure);
  }

  private void onBackfillSuccess(int count) {
    LOGGER.infof("Backfilled %d events", count);
    connect();
  }

  private void onBackfillFailure(Throwable throwable) {
    LOGGER.errorf("Backfill failed with error=%s, retrying...", throwable.getMessage());
    vertx.setTimer(Long.parseLong(applicationProperties.webSocket().seismicPortal().retryDelay()), ignored -> backfillAndConnect());
  }

  private void connect() {
    if (shuttingDown) return;
    seismicPortalWebSocketClientWebSocketConnector
        .baseUri(URI.create("wss://%s".formatted(applicationProperties.client().seismicPortal().domain())))
        .connect()
        .subscribe()
        .with(this::onConnectSuccess, this::onConnectFailure);
  }

  private void onConnectSuccess(WebSocketClientConnection webSocketClientConnection) {
    LOGGER.infof("Connection to web socket successful! connectionId: %s", webSocketClientConnection.id());
  }

  private void onConnectFailure(Throwable throwable) {
    LOGGER.errorf("Connection to web socket failed with error=%s, retrying...", throwable.getMessage());
    vertx.setTimer(Long.parseLong(applicationProperties.webSocket().seismicPortal().retryDelay()), ignored -> connect());
  }

}
