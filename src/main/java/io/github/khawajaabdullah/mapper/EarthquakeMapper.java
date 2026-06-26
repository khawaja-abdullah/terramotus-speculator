package io.github.khawajaabdullah.mapper;

import io.github.khawajaabdullah.dto.response.EarthquakeResponse;
import io.github.khawajaabdullah.dto.usgs.Feature;
import io.github.khawajaabdullah.dto.usgs.Geometry;
import io.github.khawajaabdullah.dto.usgs.Properties;
import io.github.khawajaabdullah.entity.EarthquakeEntity;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;

@ApplicationScoped
public class EarthquakeMapper {

  public EarthquakeEntity mapFeatureToEarthquakeEntity(Feature feature) {
    Properties properties = feature.properties();
    Geometry geometry = feature.geometry();
    List<Double> coordinates = geometry.coordinates();
    return new EarthquakeEntity(
        feature.id(),
        properties.mag(),
        properties.place(),
        properties.time(),
        properties.updated(),
        properties.status(),
        coordinates.get(0),
        coordinates.get(1),
        coordinates.get(2)
    );
  }

  public EarthquakeResponse mapEarthquakeEntityToResponse(EarthquakeEntity earthquakeEntity) {
    return new EarthquakeResponse(
        earthquakeEntity.getId(),
        earthquakeEntity.getMagnitude(),
        earthquakeEntity.getPlace(),
        earthquakeEntity.getTime(),
        earthquakeEntity.getUpdated(),
        earthquakeEntity.getStatus(),
        earthquakeEntity.getLongitude(),
        earthquakeEntity.getLatitude(),
        earthquakeEntity.getDepth()
    );
  }

}
