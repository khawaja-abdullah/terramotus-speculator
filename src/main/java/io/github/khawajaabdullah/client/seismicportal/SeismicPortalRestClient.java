package io.github.khawajaabdullah.client.seismicportal;

import io.github.khawajaabdullah.dto.seismicportal.FeatureCollection;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.QueryParam;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

@RegisterRestClient(configKey = "seismic-portal-api")
public interface SeismicPortalRestClient {

  @GET
  @Path("/fdsnws/event/1/query")
  FeatureCollection getHistoricalEvents(@QueryParam("format") String format,
                                        @QueryParam("start") String start,
                                        @QueryParam("end") String end,
                                        @QueryParam("minmag") Double minMag,
                                        @QueryParam("maxmag") Double maxMag,
                                        @QueryParam("limit") Integer limit,
                                        @QueryParam("eventid") String eventId,
                                        @QueryParam("lat") Double lat,
                                        @QueryParam("lon") Double lon,
                                        @QueryParam("maxradius") Double maxRadius
  );

}
