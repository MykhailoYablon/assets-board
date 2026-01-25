package com.assets.board.model.tax;

import jakarta.xml.bind.annotation.*;
import lombok.Data;

@Data
@XmlRootElement(name = "DECLAR")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlSeeAlso({DeclarBody.class, DeclarBodyF1.class})
public class Declar {

    @XmlAttribute(name = "noNamespaceSchemaLocation",
            namespace = "http://www.w3.org/2001/XMLSchema-instance")
    private String schemaLocation;

    @XmlElement(name = "DECLARHEAD", required = true)
    private DeclarHead declarHead;

    @XmlElements({
            @XmlElement(name = "DECLARBODY", type = DeclarBody.class),
            @XmlElement(name = "DECLARBODY", type = DeclarBodyF1.class)
    })
    private Object declarBody;

    // Helper methods
    public DeclarBody getDeclarBodyMain() {
        return declarBody instanceof DeclarBody ? (DeclarBody) declarBody : null;
    }

    public DeclarBodyF1 getDeclarBodyF1() {
        return declarBody instanceof DeclarBodyF1 ? (DeclarBodyF1) declarBody : null;
    }

    public void setDeclarBody(DeclarBody body) {
        this.declarBody = body;
        this.schemaLocation = "F0100214.xsd";
    }

    public void setDeclarBody(DeclarBodyF1 body) {
        this.declarBody = body;
        this.schemaLocation = "F0121214.xsd";
    }
}

