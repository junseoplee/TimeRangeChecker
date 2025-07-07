package junseop.timerangechecker.domain.repository;

import java.time.LocalDateTime;
import java.util.List;
import junseop.timerangechecker.domain.TimeCheckLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface TimeCheckLogRepository extends JpaRepository<TimeCheckLog, Long> {

  List<TimeCheckLog> findBySessionIdAndIsActiveTrue(String sessionId);

  Page<TimeCheckLog> findByIsActiveTrueOrderByCreatedAtDesc(Pageable pageable);

  @Query("SELECT COUNT(t) FROM TimeCheckLog t WHERE t.createdAt BETWEEN :startDate AND :endDate AND t.isActive = true")
  Long countByCreatedAtBetweenAndIsActiveTrue(@Param("startDate") LocalDateTime startDate, 
                                              @Param("endDate") LocalDateTime endDate);

  @Query("SELECT COUNT(DISTINCT t.sessionId) FROM TimeCheckLog t WHERE t.createdAt BETWEEN :startDate AND :endDate AND t.isActive = true")
  Long countDistinctSessionByCreatedAtBetweenAndIsActiveTrue(@Param("startDate") LocalDateTime startDate, 
                                                             @Param("endDate") LocalDateTime endDate);

  @Query("SELECT t.startHour, t.endHour, COUNT(t) as cnt FROM TimeCheckLog t " +
         "WHERE t.createdAt BETWEEN :startDate AND :endDate AND t.isActive = true " +
         "GROUP BY t.startHour, t.endHour ORDER BY cnt DESC")
  List<Object[]> findPopularTimeRangeByCreatedAtBetween(@Param("startDate") LocalDateTime startDate, 
                                                        @Param("endDate") LocalDateTime endDate);
} 