package io.github.khawajaabdullah.dto.seismicportal;

import com.fasterxml.jackson.annotation.JsonProperty;

public record FeatureMessage(String action, @JsonProperty("data") Feature feature) {
}
