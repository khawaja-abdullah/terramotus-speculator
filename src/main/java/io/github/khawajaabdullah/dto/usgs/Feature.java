package io.github.khawajaabdullah.dto.usgs;

public record Feature(String type, Properties properties, Geometry geometry, String id) {
}
