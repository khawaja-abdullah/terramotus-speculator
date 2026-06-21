package io.github.khawajaabdullah.dto;

import java.util.List;

public record Geometry(String type, List<Double> coordinates) {
}
