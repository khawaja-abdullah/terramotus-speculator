package io.github.khawajaabdullah.util;

import io.smallrye.config.ConfigMapping;

@ConfigMapping(prefix = "application")
public interface ApplicationConfiguration {

  Client client();

  interface Client {

    SeismicPortal seismicPortal();

    interface SeismicPortal {

      String domain();

    }

  }

}
