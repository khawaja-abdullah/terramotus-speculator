package io.github.khawajaabdullah.service;

import io.github.khawajaabdullah.dto.response.EarthquakeResponse;
import io.github.khawajaabdullah.dto.usgs.FeatureCollection;
import io.github.khawajaabdullah.entity.EarthquakeEntity;
import io.github.khawajaabdullah.mapper.EarthquakeMapper;
import io.github.khawajaabdullah.repository.EarthquakeRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.util.List;

@ApplicationScoped
public class EarthquakeService {

  @Inject
  EarthquakeMapper earthquakeMapper;
  @Inject
  EarthquakeRepository earthquakeRepository;

  @Transactional
  public void persist(FeatureCollection featureCollection) {
    List<EarthquakeEntity> earthquakeEntities = featureCollection.features()
        .stream()
        .filter(feature -> earthquakeRepository.findByIdOptional(feature.id()).isEmpty())
        .map(feature -> earthquakeMapper.mapFeatureToEarthquakeEntity(feature))
        .toList();
    earthquakeRepository.persist(earthquakeEntities);
  }

  public List<EarthquakeResponse> listAll() {
    return earthquakeRepository.listAll()
        .stream()
        .map(earthquakeEntity -> earthquakeMapper.mapEarthquakeEntityToResponse(earthquakeEntity))
        .toList();
  }

}
