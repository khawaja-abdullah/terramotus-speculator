package io.github.khawajaabdullah.service;

import io.github.khawajaabdullah.dto.EarthquakeRecord;
import io.smallrye.mutiny.Multi;

import java.time.LocalDateTime;
import java.util.List;

public interface EarthquakeService {

  void upsert(EarthquakeRecord earthquakeRecord);

  void upsertMultiple(List<EarthquakeRecord> earthquakeRecords);

  List<EarthquakeRecord> getAll(LocalDateTime startTime, LocalDateTime endTime, Integer pageNumber, Integer pageSize);

  int backfillGap();


  List<EarthquakeRecord> getHistoricalEvents(String start, String end);

  List<EarthquakeRecord> getHistoricalEvents(String format, String start, String end, Double minMag, Double maxMag,
                                             Integer limit, String eventId, Double lat, Double lon, Double maxRadius);


  void broadcast(EarthquakeRecord earthquakeRecord);

  Multi<EarthquakeRecord> getStream();

}
