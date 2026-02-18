package com.assets.board.model.ib;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.opencsv.bean.CsvBindByPosition;
import lombok.Data;
import lombok.Setter;

import java.math.BigDecimal;

@Setter
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class DividendRecord {

    @CsvBindByPosition(position = 3)
    private String date;

    @CsvBindByPosition(position = 4)
    private String description;

    @CsvBindByPosition(position = 5)
    private BigDecimal amount;

//    USTaxUSD,USTaxUAH,TotalTaxUAH,USNetto,UANetto,Dividends$Netto
}
