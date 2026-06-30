package io.github.khawajaabdullah.dto;

public record EarthquakeRecord(String id, String lastUpdate, String time, String flynnRegion, double latitude,
                               double longitude, double depth, double magnitude) {
}