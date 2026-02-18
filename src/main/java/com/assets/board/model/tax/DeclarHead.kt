package com.assets.board.model.tax;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import lombok.Data;

@Data
@XmlAccessorType(XmlAccessType.FIELD)
public class DeclarHead {

    @XmlElement(name = "TIN")
    private String tin;

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

    @XmlElement(name = "C_REG")
    private String cReg;

    @XmlElement(name = "C_RAJ")
    private String cRaj;

    @XmlElement(name = "PERIOD_MONTH")
    private String periodMonth;

    @XmlElement(name = "PERIOD_TYPE")
    private String periodType;

    @XmlElement(name = "PERIOD_YEAR")
    private String periodYear;

    @XmlElement(name = "C_STI_ORIG")
    private String cStiOrig;

    @XmlElement(name = "C_DOC_STAN")
    private String cDocStan;

    @XmlElement(name = "LINKED_DOCS")
    private LinkedDocs linkedDocs;

    @XmlElement(name = "D_FILL")
    private String dFill;

    @XmlElement(name = "SOFTWARE")
    private String software;
}
