package io.github.khawajaabdullah.dto.response;

public record EarthquakeResponse(String id, double magnitude, String place, long time, long updated, String status,
                                 double longitude, double latitude, double depth) {
}