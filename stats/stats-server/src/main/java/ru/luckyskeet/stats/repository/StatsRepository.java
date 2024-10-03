package ru.luckyskeet.stats.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.luckyskeet.stats.model.EndpointHit;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface StatsRepository extends JpaRepository<EndpointHit, Long> {

    int countByAppAndUriEndingWithAndTimestampBetween(String app, String uri,
                                                      LocalDateTime start,
                                                      LocalDateTime end);

    @Query(value = "SELECT COUNT(DISTINCT ip) FROM statistics " +
            "WHERE app = ?1 AND uri LIKE '%' || ?2 AND event_time BETWEEN ?3 AND ?4", nativeQuery = true)
    Integer countUniqueIpWithParams(String app, String uri, LocalDateTime start, LocalDateTime end);

    @Query(value = "SELECT DISTINCT uri FROM statistics " +
            "WHERE app = ?1 AND event_time BETWEEN ?2 AND ?3", nativeQuery = true)
    List<String> findAllUrisWithParams(String app, LocalDateTime start, LocalDateTime end);
}
