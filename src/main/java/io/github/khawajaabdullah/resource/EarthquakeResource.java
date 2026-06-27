package io.github.khawajaabdullah.resource;

import io.github.khawajaabdullah.client.seismicportal.SeismicPortalClient;
import io.github.khawajaabdullah.dto.response.EarthquakeResponse;
import io.github.khawajaabdullah.dto.seismicportal.FeatureCollection;
import io.github.khawajaabdullah.service.EarthquakeService;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import java.util.List;

@Path("/earthquakes")
public class EarthquakeResource {

  @RestClient
  SeismicPortalClient seismicPortalClient;
  @Inject
  EarthquakeService earthquakeService;

  @GET
  @Path("/historical")
  @Produces(MediaType.APPLICATION_JSON)
  public FeatureCollection getHistoricalEvents(@QueryParam("format") @DefaultValue("json") String format,
                                               @QueryParam("start") String start,
                                               @QueryParam("end") String end,
                                               @QueryParam("minmag") Double minMag,
                                               @QueryParam("maxmag") Double maxMag,
                                               @QueryParam("limit") Integer limit,
                                               @QueryParam("eventid") String eventId,
                                               @QueryParam("lat") Double lat,
                                               @QueryParam("lon") Double lon,
                                               @QueryParam("maxradius") Double maxRadius) {
    return seismicPortalClient.getHistoricalEvents(format, start, end, minMag, maxMag, limit, eventId, lat, lon, maxRadius);
  }

  @GET
  @Produces(MediaType.APPLICATION_JSON)
  public List<EarthquakeResponse> findAll(@QueryParam("pageNumber") Integer pageNumber, @QueryParam("pageSize") Integer pageSize) {
    return earthquakeService.findAll(pageNumber, pageSize);
  }

}
