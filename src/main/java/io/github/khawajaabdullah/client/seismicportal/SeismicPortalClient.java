package io.github.khawajaabdullah.client.seismicportal;

import io.github.khawajaabdullah.dto.seismicportal.FeatureCollection;
import io.github.khawajaabdullah.util.Constant;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.QueryParam;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

@RegisterRestClient(configKey = "seismic-portal-api")
public interface SeismicPortalClient {

  @GET
  @Path("/fdsnws/event/1/query")
  FeatureCollection getHistoricalEvents(@QueryParam("format")
                                        @DefaultValue(Constant.SEISMIC_PORTAL_API_RESPONSE_FORMAT_JSON) String format,
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
