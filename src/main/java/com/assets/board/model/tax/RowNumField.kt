package com.assets.board.model.tax

import jakarta.xml.bind.annotation.XmlAccessType
import jakarta.xml.bind.annotation.XmlAccessorType
import jakarta.xml.bind.annotation.XmlAttribute
import jakarta.xml.bind.annotation.XmlValue
import lombok.Data

@Data
@XmlAccessorType(XmlAccessType.FIELD)
class RowNumField {
    @XmlAttribute(name = "ROWNUM")
    var rowNum: String? = null

    @XmlValue
    var value: String? = null

    constructor()

    constructor(rowNum: String?, value: String?) {
        this.rowNum = rowNum
        this.value = value
    }
}
