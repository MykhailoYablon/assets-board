package com.assets.board.model.tax

import jakarta.xml.bind.annotation.XmlAccessType
import jakarta.xml.bind.annotation.XmlAccessorType
import jakarta.xml.bind.annotation.XmlElement
import lombok.Data

@Data
@XmlAccessorType(XmlAccessType.FIELD)
class LinkedDocs {
    @XmlElement(name = "DOC")
    var docs: MutableList<Doc>? = null
}
