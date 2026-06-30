package io.github.khawajaabdullah;

import io.github.khawajaabdullah.dto.EarthquakeRecord;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.common.mapper.TypeRef;
import org.jboss.resteasy.reactive.RestResponse;
import org.junit.jupiter.api.Test;

import java.util.List;

import static io.restassured.RestAssured.given;

@QuarkusTest
class EarthquakeResourceTest {

  @Test
  void shouldGetHistoricalEarthquakeEvents() {
    given()
        .when().get("/api/v1/earthquakes/historical?format=json&limit=10")
        .then()
        .statusCode(RestResponse.StatusCode.OK)
        .extract().as(new TypeRef<List<EarthquakeRecord>>() {
        });
  }

  @Test
  void shouldFindAllEarthquakes() {
    given()
        .when().get("/api/v1/earthquakes")
        .then()
        .statusCode(RestResponse.StatusCode.OK)
        .extract().as(new TypeRef<List<EarthquakeRecord>>() {
        });
  }

  @Test
  void shouldFindAllEarthquakesPaginated() {
    given()
        .when().get("/api/v1/giearthquakes?pageNumber=0&pageSize=10")
        .then()
        .statusCode(RestResponse.StatusCode.OK)
        .extract().as(new TypeRef<List<EarthquakeRecord>>() {
        });
  }

}