package io.github.khawajaabdullah.dto.seismicportal;

public record Feature(String type, Geometry geometry, String id, Properties properties) {
}
