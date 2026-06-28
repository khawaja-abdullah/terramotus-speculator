package io.github.khawajaabdullah.service;

import io.github.khawajaabdullah.client.seismicportal.SeismicPortalWebSocketClient;
import io.github.khawajaabdullah.util.ApplicationConfiguration;
import io.quarkus.runtime.StartupEvent;
import io.quarkus.websockets.next.Closed;
import io.quarkus.websockets.next.WebSocketClientConnection;
import io.quarkus.websockets.next.WebSocketConnector;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.enterprise.event.ObservesAsync;
import jakarta.enterprise.inject.Instance;
import org.jboss.logging.Logger;

import java.net.URI;

@ApplicationScoped
public class SeismicPortalWebSocketAdaptor {

  private static final Logger LOGGER = Logger.getLogger(SeismicPortalWebSocketAdaptor.class);

  private final Instance<WebSocketConnector<SeismicPortalWebSocketClient>> seismicPortalWebSocketClientConnectorInstance;
  private final ApplicationConfiguration applicationConfiguration;

  public SeismicPortalWebSocketAdaptor(Instance<WebSocketConnector<SeismicPortalWebSocketClient>> seismicPortalWebSocketClientConnectorInstance,
                                       ApplicationConfiguration applicationConfiguration) {
    this.seismicPortalWebSocketClientConnectorInstance = seismicPortalWebSocketClientConnectorInstance;
    this.applicationConfiguration = applicationConfiguration;
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
