package com.assets.board.repository

import com.assets.board.entity.ExchangeRate
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.time.LocalDate
import java.util.*

@Repository
interface ExchangeRateRepository : JpaRepository<ExchangeRate?, Long?> {
    fun findByDate(date: LocalDate?): ExchangeRate?

    fun findByDateBetween(start: LocalDate?, end: LocalDate?): MutableList<ExchangeRate?>?

    @Query(
        "SELECT e FROM ExchangeRate e WHERE e.date = " +
                "(SELECT MAX(e2.date) FROM ExchangeRate e2 WHERE e2.date <= :date)"
    )
    fun findLatestBeforeOrOn(@Param("date") date: LocalDate?): Optional<ExchangeRate?>?
}
