package io.github.khawajaabdullah.dto.seismicportal;

import java.util.List;

public record FeatureCollection(String type, Metadata metadata, List<Feature> features) {
}
