package io.github.khawajaabdullah.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "earthquake")
public class EarthquakeEntity {

  @Id
  private String id;
  private String lastUpdate;
  private String time;
  private String flynnRegion;
  private double latitude;
  private double longitude;
  private double depth;
  private double magnitude;

  public EarthquakeEntity() {
  }

  public EarthquakeEntity(String id, String lastUpdate, String time, String flynnRegion, double latitude,
                          double longitude, double depth, double magnitude) {
    this.id = id;
    this.lastUpdate = lastUpdate;
    this.time = time;
    this.flynnRegion = flynnRegion;
    this.latitude = latitude;
    this.longitude = longitude;
    this.depth = depth;
    this.magnitude = magnitude;
  }

  public String getId() {
    return id;
  }

  public String getLastUpdate() {
    return lastUpdate;
  }

  public String getTime() {
    return time;
  }

  public String getFlynnRegion() {
    return flynnRegion;
  }

  public double getLatitude() {
    return latitude;
  }

  public double getLongitude() {
    return longitude;
  }

  public double getDepth() {
    return depth;
  }

  public double getMagnitude() {
    return magnitude;
  }

}
