package io.github.khawajaabdullah.service;

import io.github.khawajaabdullah.dto.response.EarthquakeResponse;
import io.github.khawajaabdullah.dto.seismicportal.FeatureCollection;
import io.github.khawajaabdullah.entity.EarthquakeEntity;
import io.github.khawajaabdullah.mapper.EarthquakeMapper;
import io.github.khawajaabdullah.repository.EarthquakeRepository;
import io.quarkus.panache.common.Page;
import io.quarkus.panache.common.Sort;
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

  public List<EarthquakeResponse> findAll(Integer pageNumber, Integer pageSize) {
    if (pageNumber == null && pageSize == null) {
      return findAll();
    } else if (pageNumber == null) {
      return findAll(0, pageSize.intValue());
    } else if (pageSize == null) {
      return findAll(pageNumber.intValue(), 10);
    } else {
      return findAll(pageNumber.intValue(), pageSize.intValue());
    }
  }

  private List<EarthquakeResponse> findAll() {
    return earthquakeRepository.findAll(Sort.descending("time"))
        .stream()
        .map(earthquakeEntity -> earthquakeMapper.mapEarthquakeEntityToResponse(earthquakeEntity))
        .toList();
  }

  private List<EarthquakeResponse> findAll(int pageNumber, int pageSize) {
    return earthquakeRepository.findAll(Sort.descending("time"))
        .page(Page.of(pageNumber, pageSize))
        .stream()
        .map(earthquakeEntity -> earthquakeMapper.mapEarthquakeEntityToResponse(earthquakeEntity))
        .toList();
  }

}
