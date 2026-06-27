package io.github.khawajaabdullah.dto.seismicportal;

import java.util.List;

public record Geometry(String type, List<Double> coordinates) {
}
