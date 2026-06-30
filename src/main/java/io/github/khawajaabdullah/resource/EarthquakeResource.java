package io.github.khawajaabdullah.resource;

import io.github.khawajaabdullah.dto.response.EarthquakeRecord;
import io.github.khawajaabdullah.service.EarthquakeService;
import io.github.khawajaabdullah.util.Constant;
import io.smallrye.mutiny.Multi;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;

import java.time.LocalDateTime;
import java.util.List;

@Path("/v1/earthquakes")
public class EarthquakeResource {

  private final EarthquakeService earthquakeService;

  public EarthquakeResource(EarthquakeService earthquakeService) {
    this.earthquakeService = earthquakeService;
  }

  @GET
  @Path("/historical")
  @Produces(MediaType.APPLICATION_JSON)
  public List<EarthquakeRecord> getHistoricalEvents(@QueryParam("format")
                                                    @DefaultValue(Constant.SEISMIC_PORTAL_API_RESPONSE_FORMAT_JSON) String format,
                                                    @QueryParam("start") String start,
                                                    @QueryParam("end") String end,
                                                    @QueryParam("minmag") Double minMag,
                                                    @QueryParam("maxmag") Double maxMag,
                                                    @QueryParam("limit") Integer limit,
                                                    @QueryParam("eventid") String eventId,
                                                    @QueryParam("lat") Double lat,
                                                    @QueryParam("lon") Double lon,
                                                    @QueryParam("maxradius") Double maxRadius) {
    return earthquakeService.getHistoricalEvents(format, start, end, minMag, maxMag, limit, eventId, lat, lon, maxRadius);
  }

  @GET
  @Produces(MediaType.APPLICATION_JSON)
  public List<EarthquakeRecord> getAll(@QueryParam("startTime") LocalDateTime startTime,
                                       @QueryParam("endTime") LocalDateTime endTime,
                                       @QueryParam("pageNumber") Integer pageNumber,
                                       @QueryParam("pageSize") Integer pageSize) {
    return earthquakeService.getAll(startTime, endTime, pageNumber, pageSize);
  }

  @GET
  @Path("/live")
  @Produces(MediaType.SERVER_SENT_EVENTS)
  public Multi<EarthquakeRecord> getLiveEvents() {
    return earthquakeService.getLiveEvents();
  }

}
