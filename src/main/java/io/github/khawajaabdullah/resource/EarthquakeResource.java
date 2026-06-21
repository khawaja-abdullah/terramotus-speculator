package io.github.khawajaabdullah.resource;

import io.github.khawajaabdullah.client.UsgsClient;
import io.github.khawajaabdullah.dto.FeatureCollection;
import io.github.khawajaabdullah.util.Constant;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.rest.client.inject.RestClient;

@Path("/earthquakes")
public class EarthquakeResource {

  @RestClient
  UsgsClient usgsClient;

  @GET
  @Path("/hour/all")
  @Produces(MediaType.APPLICATION_JSON)
  public FeatureCollection getAllHourData() {
    return usgsClient.getEarthquakesFeedSummary(Constant.EARTHQUAKE_FEED_TYPE_ALL_HOUR);
  }

}
