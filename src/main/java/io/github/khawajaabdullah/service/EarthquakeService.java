package io.github.khawajaabdullah.service;

import io.github.khawajaabdullah.dto.response.EarthquakeRecord;

import java.util.List;

public interface EarthquakeService {

  void upsert(EarthquakeRecord earthquakeRecord);

  void upsertMultiple(List<EarthquakeRecord> earthquakeRecords);

  List<EarthquakeRecord> findAll(Integer pageNumber, Integer pageSize);

  List<EarthquakeRecord> getHistoricalEvents(String start, String end);

  List<EarthquakeRecord> getHistoricalEvents(String format, String start, String end, Double minMag, Double maxMag,
                                             Integer limit, String eventId, Double lat, Double lon, Double maxRadius);
}
