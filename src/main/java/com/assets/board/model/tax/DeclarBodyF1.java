package com.assets.board.model.tax;

import jakarta.xml.bind.annotation.*;
import lombok.Data;
import java.util.ArrayList;
import java.util.List;

@Data
@XmlRootElement(name = "DECLARBODY")
@XmlAccessorType(XmlAccessType.FIELD)
public class DeclarBodyF1 {

    @XmlAttribute(name = "type", namespace = "http://www.w3.org/2001/XMLSchema-instance")
    private String type = "DECLARBODY_F1";

    @XmlElement(name = "HBOS")
    private String hbos;

    @XmlElement(name = "HTIN")
    private String htin;

    @XmlElement(name = "HZ")
    private String hz;

    @XmlElement(name = "HZY")
    private String hzy;

    @XmlElement(name = "R001G4")
    private String r001g4;

    @XmlElement(name = "R001G5")
    private String r001g5;

    @XmlElement(name = "R001G6")
    private String r001g6;

    @XmlElement(name = "R003G6")
    private String r003g6;

    @XmlElement(name = "R004G6")
    private String r004g6;

    @XmlElement(name = "R005G6")
    private String r005g6;

    @XmlElement(name = "R031G6")
    private String r031g6;

    @XmlElement(name = "R042G6")
    private String r042g6;

    @XmlElement(name = "R052G6")
    private String r052g6;

    @XmlElement(name = "T1RXXXXG2")
    private List<RowNumField> t1rxxxxg2 = new ArrayList<>();

    @XmlElement(name = "T1RXXXXG3S")
    private List<RowNumField> t1rxxxxg3s = new ArrayList<>();

    @XmlElement(name = "T1RXXXXG4")
    private List<RowNumField> t1rxxxxg4 = new ArrayList<>();

    @XmlElement(name = "T1RXXXXG5")
    private List<RowNumField> t1rxxxxg5 = new ArrayList<>();

    @XmlElement(name = "T1RXXXXG6")
    private List<RowNumField> t1rxxxxg6 = new ArrayList<>();

    // Helper method to add a complete row
    public void addTableRow(String rowNum, String g2, String g3s,
                            String g4, String g5, String g6) {
        t1rxxxxg2.add(new RowNumField(rowNum, g2));
        t1rxxxxg3s.add(new RowNumField(rowNum, g3s));
        t1rxxxxg4.add(new RowNumField(rowNum, g4));
        t1rxxxxg5.add(new RowNumField(rowNum, g5));
        t1rxxxxg6.add(new RowNumField(rowNum, g6));
    }
}
