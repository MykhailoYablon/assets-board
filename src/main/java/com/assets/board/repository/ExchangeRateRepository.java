package com.assets.board.repository;

import com.assets.board.entity.ExchangeRate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface ExchangeRateRepository extends JpaRepository<ExchangeRate, Long> {

    Optional<ExchangeRate> findByDate(LocalDate date);

    List<ExchangeRate> findByDateBetween(LocalDate start, LocalDate end);

    @Query("SELECT e FROM ExchangeRate e WHERE e.date = " +
            "(SELECT MAX(e2.date) FROM ExchangeRate e2 WHERE e2.date <= :date)")
    Optional<ExchangeRate> findLatestBeforeOrOn(@Param("date") LocalDate date);
}
