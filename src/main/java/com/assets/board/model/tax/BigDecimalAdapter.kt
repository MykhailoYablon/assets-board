package com.assets.board.model.tax;

import jakarta.xml.bind.annotation.adapters.XmlAdapter;

import java.math.BigDecimal;

// Custom adapter for BigDecimal formatting
class BigDecimalAdapter extends XmlAdapter<String, BigDecimal> {
    @Override
    public BigDecimal unmarshal(String v) {
        return v == null ? null : new BigDecimal(v);
    }

    @Override
    public String marshal(BigDecimal v) {
        return v == null ? null : v.toPlainString();
    }
}
