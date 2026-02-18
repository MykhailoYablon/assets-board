package com.assets.board.model.tax;

import jakarta.xml.bind.annotation.*;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import lombok.Data;

import java.math.BigDecimal;

@Data
@XmlRootElement(name = "DECLARBODY")
@XmlAccessorType(XmlAccessType.FIELD)
public class DeclarBody {

    @XmlAttribute(name = "type", namespace = "http://www.w3.org/2001/XMLSchema-instance")
    private String type = "DECLARBODY_MAIN";

    @XmlElement(name = "H01")
    private String h01;

    @XmlElement(name = "H03")
    private String h03;

    @XmlElement(name = "H05")
    private String h05;

    @XmlElement(name = "HBOS")
    private String hbos;

    @XmlElement(name = "HCITY")
    private String hcity;

    @XmlElement(name = "HD1")
    private String hd1;

    @XmlElement(name = "HFILL")
    private String hfill;

    @XmlElement(name = "HNAME")
    private String hname;

    @XmlElement(name = "HSTI")
    private String hsti;

    @XmlElement(name = "HSTREET")
    private String hstreet;

    @XmlElement(name = "HTIN")
    private String htin;

    @XmlElement(name = "HZ")
    private String hz;

    @XmlElement(name = "HZY")
    private String hzy;

    @XmlElement(name = "R0104G3")
    @XmlJavaTypeAdapter(BigDecimalAdapter.class)
    private BigDecimal r0104g3;

    @XmlElement(name = "R0104G6")
    @XmlJavaTypeAdapter(BigDecimalAdapter.class)
    private BigDecimal r0104g6;

    @XmlElement(name = "R0104G7")
    @XmlJavaTypeAdapter(BigDecimalAdapter.class)
    private BigDecimal r0104g7;

    @XmlElement(name = "R0108G3")
    @XmlJavaTypeAdapter(BigDecimalAdapter.class)
    private BigDecimal r0108g3;

    @XmlElement(name = "R0108G6")
    @XmlJavaTypeAdapter(BigDecimalAdapter.class)
    private BigDecimal r0108g6;

    @XmlElement(name = "R0108G7")
    @XmlJavaTypeAdapter(BigDecimalAdapter.class)
    private BigDecimal r0108g7;

    @XmlElement(name = "R01010G2S")
    private String r01010g2s;

    @XmlElement(name = "R010G3")
    @XmlJavaTypeAdapter(BigDecimalAdapter.class)
    private BigDecimal r010g3;

    @XmlElement(name = "R010G6")
    @XmlJavaTypeAdapter(BigDecimalAdapter.class)
    private BigDecimal r010g6;

    @XmlElement(name = "R010G7")
    @XmlJavaTypeAdapter(BigDecimalAdapter.class)
    private BigDecimal r010g7;

    @XmlElement(name = "R012G3")
    @XmlJavaTypeAdapter(BigDecimalAdapter.class)
    private BigDecimal r012g3;

    @XmlElement(name = "R013G3")
    @XmlJavaTypeAdapter(BigDecimalAdapter.class)
    private BigDecimal r013g3;

    @XmlElement(name = "R018G3")
    @XmlJavaTypeAdapter(BigDecimalAdapter.class)
    private BigDecimal r018g3;

    @XmlElement(name = "R0201G3")
    @XmlJavaTypeAdapter(BigDecimalAdapter.class)
    private BigDecimal r0201g3;

    @XmlElement(name = "R0211G3")
    @XmlJavaTypeAdapter(BigDecimalAdapter.class)
    private BigDecimal r0211g3;

}

