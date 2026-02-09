package com.assets.board.repository;

import com.assets.board.entity.TotalTaxReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TotalTaxReportRepository extends JpaRepository<TotalTaxReport, Long> {

    Optional<TotalTaxReport> findByYear(Short year);

}
