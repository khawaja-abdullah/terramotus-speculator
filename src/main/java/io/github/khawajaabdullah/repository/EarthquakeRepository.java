package io.github.khawajaabdullah.repository;

import io.github.khawajaabdullah.entity.EarthquakeEntity;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class EarthquakeRepository implements PanacheRepositoryBase<EarthquakeEntity, String> {
}
