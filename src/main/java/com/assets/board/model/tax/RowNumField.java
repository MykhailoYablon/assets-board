package com.assets.board.model.tax;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlValue;
import lombok.Data;

@Data
@XmlAccessorType(XmlAccessType.FIELD)
public class RowNumField {

    @XmlAttribute(name = "ROWNUM")
    private String rowNum;

    @XmlValue
    private String value;

    public RowNumField() {
    }

    public RowNumField(String rowNum, String value) {
        this.rowNum = rowNum;
        this.value = value;
    }
}
