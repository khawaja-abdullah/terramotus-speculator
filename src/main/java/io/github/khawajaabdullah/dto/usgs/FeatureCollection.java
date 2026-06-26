package io.github.khawajaabdullah.dto.usgs;

import java.util.List;

public record FeatureCollection(String type, Metadata metadata, List<Feature> features, List<Double> bbox) {
}
