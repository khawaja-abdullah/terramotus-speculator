package io.github.khawajaabdullah.mapper;

import io.github.khawajaabdullah.dto.response.EarthquakeRecord;
import io.github.khawajaabdullah.dto.seismicportal.Feature;
import io.github.khawajaabdullah.dto.seismicportal.Properties;
import io.github.khawajaabdullah.entity.EarthquakeEntity;
import io.github.khawajaabdullah.util.Constant;
import jakarta.enterprise.context.ApplicationScoped;

import java.time.OffsetDateTime;

@ApplicationScoped
public class EarthquakeMapper {

  public EarthquakeRecord mapFeatureToEarthquakeDto(Feature feature) {
    Properties properties = feature.properties();
    return new EarthquakeRecord(
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

  public EarthquakeEntity mapEarthquakeDtoToEntity(EarthquakeRecord earthquakeRecord) {
    return EarthquakeEntity.newBuilder()
        .setId(earthquakeRecord.id())
        .setLastUpdate(OffsetDateTime.parse(earthquakeRecord.lastUpdate()).toLocalDateTime())
        .setTime(OffsetDateTime.parse(earthquakeRecord.time()).toLocalDateTime())
        .setFlynnRegion(earthquakeRecord.flynnRegion())
        .setLatitude(earthquakeRecord.latitude())
        .setLongitude(earthquakeRecord.longitude())
        .setDepth(earthquakeRecord.depth())
        .setMagnitude(earthquakeRecord.magnitude());
  }

  public EarthquakeRecord mapEarthquakeEntityToDto(EarthquakeEntity earthquakeEntity) {
    return new EarthquakeRecord(
        earthquakeEntity.getId(),
        earthquakeEntity.getLastUpdate().format(Constant.ISO_ZULU_FORMATTER),
        earthquakeEntity.getTime().format(Constant.ISO_ZULU_FORMATTER),
        earthquakeEntity.getFlynnRegion(),
        earthquakeEntity.getLatitude(),
        earthquakeEntity.getLongitude(),
        earthquakeEntity.getDepth(),
        earthquakeEntity.getMagnitude()
    );
  }

}
