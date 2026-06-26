package io.github.khawajaabdullah.dto.usgs;

public record Metadata(long generated, String url, String title, int status, String api, int count) {
}
