package io.github.khawajaabdullah.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "earthquake")
public class EarthquakeEntity {

  @Id
  private String id;
  @Column(name = "last_update")
  private String lastUpdate;
  private String time;
  @Column(name = "flynn_region")
  private String flynnRegion;
  private double latitude;
  private double longitude;
  private double depth;
  private double magnitude;

  private EarthquakeEntity() {
  }

  public static EarthquakeEntity newBuilder() {
    return new EarthquakeEntity();
  }

  public EarthquakeEntity setId(String id) {
    this.id = id;
    return this;
  }

  public EarthquakeEntity setLastUpdate(String lastUpdate) {
    this.lastUpdate = lastUpdate;
    return this;
  }

  public EarthquakeEntity setTime(String time) {
    this.time = time;
    return this;
  }

  public EarthquakeEntity setFlynnRegion(String flynnRegion) {
    this.flynnRegion = flynnRegion;
    return this;
  }

  public EarthquakeEntity setLatitude(double latitude) {
    this.latitude = latitude;
    return this;
  }

  public EarthquakeEntity setLongitude(double longitude) {
    this.longitude = longitude;
    return this;
  }

  public EarthquakeEntity setDepth(double depth) {
    this.depth = depth;
    return this;
  }

  public EarthquakeEntity setMagnitude(double magnitude) {
    this.magnitude = magnitude;
    return this;
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
