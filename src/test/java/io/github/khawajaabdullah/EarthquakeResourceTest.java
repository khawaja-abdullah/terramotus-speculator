package io.github.khawajaabdullah;

import io.github.khawajaabdullah.dto.FeatureCollection;
import io.quarkus.test.junit.QuarkusTest;
import org.jboss.resteasy.reactive.RestResponse;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;

@QuarkusTest
class EarthquakeResourceTest {

  @Test
  void shouldGetAllHourData() {
    given()
        .when().get("/earthquakes/hour/all")
        .then()
        .statusCode(RestResponse.StatusCode.OK)
        .extract().as(FeatureCollection.class);
  }

}