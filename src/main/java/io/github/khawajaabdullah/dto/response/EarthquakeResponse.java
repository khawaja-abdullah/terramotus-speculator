package io.github.khawajaabdullah.dto.response;

public record EarthquakeResponse(String id, String lastUpdate, String time, String flynnRegion, double latitude,
                                 double longitude, double depth, double magnitude) {
}