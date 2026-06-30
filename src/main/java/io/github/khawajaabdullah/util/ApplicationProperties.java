package io.github.khawajaabdullah.util;

import io.smallrye.config.ConfigMapping;

@ConfigMapping(prefix = "application")
public interface ApplicationProperties {

  Client client();
  WebSocket webSocket();

  interface Client {

    SeismicPortal seismicPortal();

    interface SeismicPortal {

      String domain();

    }

  }

  interface WebSocket {

    SeismicPortal seismicPortal();

    interface SeismicPortal {

      String retryDelay();

    }

  }

}
