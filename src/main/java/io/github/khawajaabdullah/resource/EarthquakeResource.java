package io.github.khawajaabdullah.resource;

import io.github.khawajaabdullah.client.UsgsClient;
import io.github.khawajaabdullah.dto.FeatureCollection;
import io.github.khawajaabdullah.entity.EarthquakeEntity;
import io.github.khawajaabdullah.repository.EarthquakeRepository;
import io.github.khawajaabdullah.util.Constant;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import java.util.List;

@Path("/earthquakes")
public class EarthquakeResource {

  @RestClient
  UsgsClient usgsClient;

  @Inject
  EarthquakeRepository earthquakeRepository;

  @GET
  @Path("/{feedType}")
  @Produces(MediaType.APPLICATION_JSON)
  public FeatureCollection listByFeedType(@PathParam("feedType") String feedType) {
    return usgsClient.getEarthquakesFeedSummary(feedType);
  }

  @GET
  @Produces(MediaType.APPLICATION_JSON)
  public List<EarthquakeEntity> list() {
    return earthquakeRepository.listAll();
  }

}
