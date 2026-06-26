package io.github.khawajaabdullah.dto.usgs;

import java.util.List;

public record Geometry(String type, List<Double> coordinates) {
}
