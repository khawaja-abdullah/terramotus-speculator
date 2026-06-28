package io.github.khawajaabdullah.repository;

import io.github.khawajaabdullah.entity.EarthquakeEntity;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import org.hibernate.StatelessSession;

import java.util.List;

@ApplicationScoped
public class EarthquakeRepository implements PanacheRepositoryBase<EarthquakeEntity, String> {

  private final StatelessSession statelessSession;

  public EarthquakeRepository(StatelessSession statelessSession) {
    this.statelessSession = statelessSession;
  }

  @Transactional
  public void upsert(EarthquakeEntity entities) {
    statelessSession.upsert(entities);
  }

  @Transactional
  public void upsertMultiple(List<EarthquakeEntity> entities) {
    statelessSession.upsertMultiple(entities);
  }

}
