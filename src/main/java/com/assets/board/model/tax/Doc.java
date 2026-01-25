package com.assets.board.model.tax;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import lombok.Data;

@Data
@XmlAccessorType(XmlAccessType.FIELD)
public class Doc {

    @XmlAttribute(name = "NUM")
    private String num;

    @XmlAttribute(name = "TYPE")
    private String type;

    @XmlElement(name = "C_DOC")
    private String cDoc;

    @XmlElement(name = "C_DOC_SUB")
    private String cDocSub;

    @XmlElement(name = "C_DOC_VER")
    private String cDocVer;

    @XmlElement(name = "C_DOC_TYPE")
    private String cDocType;

    @XmlElement(name = "C_DOC_CNT")
    private String cDocCnt;

    @XmlElement(name = "C_DOC_STAN")
    private String cDocStan;

    @XmlElement(name = "FILENAME")
    private String filename;
}
