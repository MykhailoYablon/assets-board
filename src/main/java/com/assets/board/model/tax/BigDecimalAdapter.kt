package com.assets.board.model.tax

import jakarta.xml.bind.annotation.adapters.XmlAdapter
import java.math.BigDecimal

internal class BigDecimalAdapter : XmlAdapter<String?, BigDecimal?>() {
    override fun unmarshal(v: String?): BigDecimal? {
        return if (v == null) null else BigDecimal(v)
    }

    override fun marshal(v: BigDecimal?): String? {
        return v?.toPlainString()
    }
}
