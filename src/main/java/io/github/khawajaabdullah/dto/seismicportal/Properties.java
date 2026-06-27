package io.github.khawajaabdullah.dto.seismicportal;

import com.fasterxml.jackson.annotation.JsonProperty;

public record Properties(@JsonProperty("source_id") String sourceId,
                         @JsonProperty("source_catalog") String sourceCatalog,
                         @JsonProperty("lastupdate") String lastUpdate,
                         String time,
                         @JsonProperty("flynn_region") String flynnRegion,
                         @JsonProperty("lat") double latitude,
                         @JsonProperty("lon") double longitude,
                         double depth,
                         @JsonProperty("evtype") String eventType,
                         String auth,
                         @JsonProperty("mag") double magnitude,
                         @JsonProperty("magtype") String magnitudeType,
                         @JsonProperty("unid") String unId) {
}
