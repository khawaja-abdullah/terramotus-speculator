package io.github.khawajaabdullah.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class EarthquakeEntity {

  @Id
  private String id;
  private String place;
  private double magnitude;

  public EarthquakeEntity() {}

  public EarthquakeEntity(String id, String place, double magnitude) {
    this.id = id;
    this.place = place;
    this.magnitude = magnitude;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getPlace() {
    return place;
  }

  public void setPlace(String place) {
    this.place = place;
  }

  public double getMagnitude() {
    return magnitude;
  }

  public void setMagnitude(double magnitude) {
    this.magnitude = magnitude;
  }

}
