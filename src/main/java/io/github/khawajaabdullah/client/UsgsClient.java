package io.github.khawajaabdullah.client;

import io.github.khawajaabdullah.dto.FeatureCollection;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

@RegisterRestClient(configKey = "usgs-api")
public interface UsgsClient {

  @GET
  @Path("/earthquakes/feed/v1.0/summary/{feedType}")
  FeatureCollection getEarthquakesFeedSummary(@PathParam("feedType") String feedType);

}
