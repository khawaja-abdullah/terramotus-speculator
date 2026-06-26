package io.github.khawajaabdullah.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * Represents earthquake table in DB
 * <p>
 * id        Maps to USGS Feature "id" (e.g., "us7000lzqy")
 * magnitude
 * place     Location description
 * time      Event time (epoch ms)
 * updated   Last updated time (epoch ms)
 * status    "automatic" or "reviewed"
 * longitude
 * latitude
 * depth in km
 */
@Entity
@Table(name = "earthquake")
public class EarthquakeEntity {

  @Id
  private String id;
  private double magnitude;
  private String place;
  private long time;
  private long updated;
  private String status;
  private double longitude;
  private double latitude;
  private double depth;

  public EarthquakeEntity() {
  }

  public EarthquakeEntity(String id, double magnitude, String place, long time, long updated, String status,
                          double longitude, double latitude, double depth) {
    this.id = id;
    this.magnitude = magnitude;
    this.place = place;
    this.time = time;
    this.updated = updated;
    this.status = status;
    this.longitude = longitude;
    this.latitude = latitude;
    this.depth = depth;
  }

  public String getId() {
    return id;
  }

  public double getMagnitude() {
    return magnitude;
  }

  public String getPlace() {
    return place;
  }

  public long getTime() {
    return time;
  }

  public long getUpdated() {
    return updated;
  }

  public String getStatus() {
    return status;
  }

  public double getLongitude() {
    return longitude;
  }

  public double getLatitude() {
    return latitude;
  }

  public double getDepth() {
    return depth;
  }

}
