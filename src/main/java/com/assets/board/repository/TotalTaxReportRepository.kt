package com.assets.board.repository

import com.assets.board.entity.TotalTaxReport
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface TotalTaxReportRepository : JpaRepository<TotalTaxReport?, Long?> {
    fun findByYear(year: Short): TotalTaxReport?
}
