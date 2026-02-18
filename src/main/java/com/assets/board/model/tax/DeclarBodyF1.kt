package com.assets.board.model.tax

import jakarta.xml.bind.annotation.*
import lombok.Data

@Data
@XmlRootElement(name = "DECLARBODY")
@XmlAccessorType(XmlAccessType.FIELD)
class DeclarBodyF1 {
    @XmlAttribute(name = "type", namespace = "http://www.w3.org/2001/XMLSchema-instance")
    val type = "DECLARBODY_F1"

    @XmlElement(name = "HBOS")
    var hbos: String? = null

    @XmlElement(name = "HTIN")
    var htin: String? = null

    @XmlElement(name = "HZ")
    var hz: String? = null

    @XmlElement(name = "HZY")
    var hzy: String? = null

    @XmlElement(name = "R001G4")
    var r001g4: String? = null

    @XmlElement(name = "R001G5")
    var r001g5: String? = null

    @XmlElement(name = "R001G6")
    var r001g6: String? = null

    @XmlElement(name = "R003G6")
    var r003g6: String? = null

    @XmlElement(name = "R004G6")
    var r004g6: String? = null

    @XmlElement(name = "R005G6")
    var r005g6: String? = null

    @XmlElement(name = "R031G6")
    var r031g6: String? = null

    @XmlElement(name = "R042G6")
    var r042g6: String? = null

    @XmlElement(name = "R052G6")
    var r052g6: String? = null

    @XmlElement(name = "T1RXXXXG2")
    var t1rxxxxg2: MutableList<RowNumField?> = ArrayList<RowNumField?>()

    @XmlElement(name = "T1RXXXXG3S")
    var t1rxxxxg3s: MutableList<RowNumField?> = ArrayList<RowNumField?>()

    @XmlElement(name = "T1RXXXXG4")
    var t1rxxxxg4: MutableList<RowNumField?> = ArrayList<RowNumField?>()

    @XmlElement(name = "T1RXXXXG5")
    var t1rxxxxg5: MutableList<RowNumField?> = ArrayList<RowNumField?>()

    @XmlElement(name = "T1RXXXXG6")
    var t1rxxxxg6: MutableList<RowNumField?> = ArrayList<RowNumField?>()

    // Helper method to add a complete row
    fun addTableRow(
        rowNum: String?, g2: String?, g3s: String?,
        g4: String?, g5: String?, g6: String?
    ) {
        t1rxxxxg2.add(RowNumField(rowNum, g2))
        t1rxxxxg3s.add(RowNumField(rowNum, g3s))
        t1rxxxxg4.add(RowNumField(rowNum, g4))
        t1rxxxxg5.add(RowNumField(rowNum, g5))
        t1rxxxxg6.add(RowNumField(rowNum, g6))
    }
}
