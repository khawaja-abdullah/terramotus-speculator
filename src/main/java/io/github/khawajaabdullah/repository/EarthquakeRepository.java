package io.github.khawajaabdullah.repository;

import io.github.khawajaabdullah.entity.EarthquakeEntity;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.List;

@ApplicationScoped
public class EarthquakeRepository implements PanacheRepositoryBase<EarthquakeEntity, String> {

  private final DataSource dataSource;

  public EarthquakeRepository(DataSource dataSource) {
    this.dataSource = dataSource;
  }

  @Transactional
  public void nativePersistBatch(List<EarthquakeEntity> entities) {
    String sql = """
          INSERT INTO earthquake (id, last_update, time, flynn_region, latitude, longitude, depth, magnitude)
          VALUES (?, ?, ?, ?, ?, ?, ?, ?)
          ON CONFLICT (id)
          DO UPDATE
          SET
            last_update = EXCLUDED.last_update,
            time = EXCLUDED.time,
            flynn_region = EXCLUDED.flynn_region,
            latitude = EXCLUDED.latitude,
            longitude = EXCLUDED.longitude,
            depth = EXCLUDED.depth,
            magnitude = EXCLUDED.magnitude
        """;

    try (var connection = dataSource.getConnection();
         var preparedStatement = connection.prepareStatement(sql)) {
      for (var entity : entities) {
        preparedStatement.setString(1, entity.getId());
        preparedStatement.setString(2, entity.getLastUpdate());
        preparedStatement.setString(3, entity.getTime());
        preparedStatement.setString(4, entity.getFlynnRegion());
        preparedStatement.setDouble(5, entity.getLatitude());
        preparedStatement.setDouble(6, entity.getLongitude());
        preparedStatement.setDouble(7, entity.getDepth());
        preparedStatement.setDouble(8, entity.getMagnitude());
        preparedStatement.addBatch();
      }
      preparedStatement.executeBatch();
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

}
