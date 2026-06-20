package io.github.khawajaabdullah.resource;

import io.github.khawajaabdullah.client.UsgsClient;
import io.github.khawajaabdullah.dto.FeatureCollection;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.rest.client.inject.RestClient;

@Path("/earthquakes")
public class EarthquakeResource {

  @ConfigProperty(name = "usgs-api.earthquakes.feed-type.all-hour")
  String allHourFeed;

  @RestClient
  UsgsClient usgsClient;

  @GET
  @Path("/hour/all")
  @Produces(MediaType.APPLICATION_JSON)
  public FeatureCollection getAllHourData() {
    return usgsClient.getEarthquakesFeedSummary(allHourFeed);
  }

}
