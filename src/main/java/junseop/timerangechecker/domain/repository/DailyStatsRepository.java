package junseop.timerangechecker.domain.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import junseop.timerangechecker.domain.DailyStats;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface DailyStatsRepository extends JpaRepository<DailyStats, Long> {

  Optional<DailyStats> findByDateAndIsActiveTrue(LocalDate date);

  List<DailyStats> findByDateBetweenAndIsActiveTrueOrderByDateDesc(LocalDate startDate, LocalDate endDate);

  List<DailyStats> findByIsActiveTrueOrderByDateDesc();

  @Query("SELECT d FROM DailyStats d WHERE d.isActive = true ORDER BY d.totalRequests DESC")
  List<DailyStats> findByIsActiveTrueOrderByTotalRequestsDesc();

  @Query("SELECT SUM(d.totalRequests) FROM DailyStats d WHERE d.date BETWEEN :startDate AND :endDate AND d.isActive = true")
  Long sumTotalRequestsByDateBetweenAndIsActiveTrue(@Param("startDate") LocalDate startDate, 
                                                    @Param("endDate") LocalDate endDate);

  @Query("SELECT SUM(d.uniqueSessions) FROM DailyStats d WHERE d.date BETWEEN :startDate AND :endDate AND d.isActive = true")
  Long sumUniqueSessionsByDateBetweenAndIsActiveTrue(@Param("startDate") LocalDate startDate, 
                                                     @Param("endDate") LocalDate endDate);

  @Query("SELECT d.popularRange, COUNT(d) as cnt FROM DailyStats d " +
         "WHERE d.date BETWEEN :startDate AND :endDate AND d.isActive = true AND d.popularRange IS NOT NULL " +
         "GROUP BY d.popularRange ORDER BY cnt DESC")
  List<Object[]> findMostPopularRangeByDateBetween(@Param("startDate") LocalDate startDate, 
                                                   @Param("endDate") LocalDate endDate);
} 