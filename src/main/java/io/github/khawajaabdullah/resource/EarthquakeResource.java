package io.github.khawajaabdullah.resource;

import io.github.khawajaabdullah.client.UsgsClient;
import io.github.khawajaabdullah.dto.response.EarthquakeResponse;
import io.github.khawajaabdullah.dto.usgs.FeatureCollection;
import io.github.khawajaabdullah.service.EarthquakeService;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import java.util.List;

@Path("/earthquakes")
public class EarthquakeResource {

  @RestClient
  UsgsClient usgsClient;
  @Inject
  EarthquakeService earthquakeService;

  @GET
  @Path("/{feedType}")
  @Produces(MediaType.APPLICATION_JSON)
  public FeatureCollection listByFeedType(@PathParam("feedType") String feedType) {
    return usgsClient.getEarthquakesFeedSummary(feedType);
  }

  @GET
  @Produces(MediaType.APPLICATION_JSON)
  public List<EarthquakeResponse> listAll(@QueryParam("pageNumber") Integer pageNumber, @QueryParam("pageSize") Integer pageSize) {
    return earthquakeService.findAll(pageNumber, pageSize);
  }

}
