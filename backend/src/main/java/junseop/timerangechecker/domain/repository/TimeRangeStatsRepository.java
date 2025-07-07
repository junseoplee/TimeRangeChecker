package junseop.timerangechecker.domain.repository;

import java.util.List;
import java.util.Optional;
import junseop.timerangechecker.domain.TimeRangeStats;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface TimeRangeStatsRepository extends JpaRepository<TimeRangeStats, Long> {

  Optional<TimeRangeStats> findByRangeKeyAndIsActiveTrue(String rangeKey);

  List<TimeRangeStats> findByIsActiveTrueOrderByRequestCountDesc();

  List<TimeRangeStats> findByIsActiveTrueOrderByMatchCountDesc();

  @Query("SELECT t FROM TimeRangeStats t WHERE t.isActive = true ORDER BY t.lastAccessed DESC")
  List<TimeRangeStats> findByIsActiveTrueOrderByLastAccessedDesc();

  @Query("SELECT t FROM TimeRangeStats t WHERE t.isActive = true AND t.requestCount > 0 ORDER BY (CAST(t.matchCount AS double) / t.requestCount) DESC")
  List<TimeRangeStats> findByIsActiveTrueOrderByMatchRateDesc();

  @Query("SELECT COUNT(t) FROM TimeRangeStats t WHERE t.isActive = true")
  Long countActiveStats();
} 