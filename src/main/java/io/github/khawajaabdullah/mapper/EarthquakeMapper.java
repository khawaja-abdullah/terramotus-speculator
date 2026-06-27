package io.github.khawajaabdullah.mapper;

import io.github.khawajaabdullah.dto.response.EarthquakeResponse;
import io.github.khawajaabdullah.dto.seismicportal.Feature;
import io.github.khawajaabdullah.dto.seismicportal.Properties;
import io.github.khawajaabdullah.entity.EarthquakeEntity;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class EarthquakeMapper {

  public EarthquakeEntity mapFeatureToEarthquakeEntity(Feature feature) {
    Properties properties = feature.properties();
    return new EarthquakeEntity(
        feature.id(),
        properties.lastUpdate(),
        properties.time(),
        properties.flynnRegion(),
        properties.latitude(),
        properties.longitude(),
        properties.depth(),
        properties.magnitude()
    );
  }

  public EarthquakeResponse mapEarthquakeEntityToResponse(EarthquakeEntity earthquakeEntity) {
    return new EarthquakeResponse(
        earthquakeEntity.getId(),
        earthquakeEntity.getLastUpdate(),
        earthquakeEntity.getTime(),
        earthquakeEntity.getFlynnRegion(),
        earthquakeEntity.getLatitude(),
        earthquakeEntity.getLongitude(),
        earthquakeEntity.getDepth(),
        earthquakeEntity.getMagnitude()
    );
  }

}
