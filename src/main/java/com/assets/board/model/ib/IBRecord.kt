package com.assets.board.model.ib

import com.opencsv.bean.CsvBindByPosition
import lombok.Data
import java.math.BigDecimal
import java.time.LocalDate
import java.util.regex.Matcher
import java.util.regex.Pattern

@Data
data class IBRecord (
    @CsvBindByPosition(position = 0)
    private val recordType: String? = null,

    @CsvBindByPosition(position = 1)
    private val dataType: String? = null,

    @CsvBindByPosition(position = 3)
    private val date: String? = null,

    @CsvBindByPosition(position = 4)
    private val description: String? = null,

    @CsvBindByPosition(position = 5)
    private val amount: BigDecimal? = null
) {

    val symbol: String?
        get() {
            if (description == null) return null
            val matcher: Matcher = SYMBOL_PATTERN.matcher(description)
            return if (matcher.find()) matcher.group(1) else null
        }

    val dateAsLocalDate: LocalDate?
        get() = if (date != null) LocalDate.parse(date) else null

    val isDividend: Boolean
        get() = "Dividends" == recordType && "Data" == dataType && ("Total" != date)

    val isWithholdingTax: Boolean
        get() = "Withholding Tax" == recordType && "Data" == dataType

    companion object {
        private val SYMBOL_PATTERN: Pattern = Pattern.compile("^([A-Z]+)(?=\\()")
    }
}
