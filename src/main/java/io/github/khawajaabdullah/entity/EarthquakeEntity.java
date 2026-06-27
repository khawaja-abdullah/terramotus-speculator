package io.github.khawajaabdullah.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

@Entity
@Table(name = "earthquake")
public class EarthquakeEntity {

  @Id
  private String id;
  @Column(name = "last_update")
  private LocalDateTime lastUpdate;
  private LocalDateTime time;
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

  public String getId() {
    return id;
  }

  public EarthquakeEntity setId(String id) {
    this.id = id;
    return this;
  }

  public LocalDateTime getLastUpdate() {
    return lastUpdate;
  }

  public EarthquakeEntity setLastUpdate(LocalDateTime lastUpdate) {
    this.lastUpdate = lastUpdate;
    return this;
  }

  public LocalDateTime getTime() {
    return time;
  }

  public EarthquakeEntity setTime(LocalDateTime time) {
    this.time = time;
    return this;
  }

  public String getFlynnRegion() {
    return flynnRegion;
  }

  public EarthquakeEntity setFlynnRegion(String flynnRegion) {
    this.flynnRegion = flynnRegion;
    return this;
  }

  public double getLatitude() {
    return latitude;
  }

  public EarthquakeEntity setLatitude(double latitude) {
    this.latitude = latitude;
    return this;
  }

  public double getLongitude() {
    return longitude;
  }

  public EarthquakeEntity setLongitude(double longitude) {
    this.longitude = longitude;
    return this;
  }

  public double getDepth() {
    return depth;
  }

  public EarthquakeEntity setDepth(double depth) {
    this.depth = depth;
    return this;
  }

  public double getMagnitude() {
    return magnitude;
  }

  public EarthquakeEntity setMagnitude(double magnitude) {
    this.magnitude = magnitude;
    return this;
  }

}
