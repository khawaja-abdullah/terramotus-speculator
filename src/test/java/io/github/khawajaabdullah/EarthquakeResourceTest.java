package io.github.khawajaabdullah;

import io.github.khawajaabdullah.dto.FeatureCollection;
import io.github.khawajaabdullah.entity.EarthquakeEntity;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.common.mapper.TypeRef;
import org.jboss.resteasy.reactive.RestResponse;
import org.junit.jupiter.api.Test;

import java.util.List;

import static io.restassured.RestAssured.given;

@QuarkusTest
class EarthquakeResourceTest {

  @Test
  void shouldListEarthquakesByFeedType() {
    given()
        .when().get("/earthquakes/all_hour.geojson")
        .then()
        .statusCode(RestResponse.StatusCode.OK)
        .extract().as(FeatureCollection.class);
  }

  @Test
  void shouldListEarthquakes() {
    given()
        .when().get("/earthquakes")
        .then()
        .statusCode(RestResponse.StatusCode.OK)
        .extract().as(new TypeRef<List<EarthquakeEntity>>() {
        });
  }

}