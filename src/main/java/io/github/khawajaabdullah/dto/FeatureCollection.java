package io.github.khawajaabdullah.dto;

import java.util.List;

public record FeatureCollection(String type, List<Feature> features) {
}
