package com.assets.board.model.tax

import jakarta.xml.bind.annotation.*
import lombok.Data

@Data
@XmlRootElement(name = "DECLAR")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlSeeAlso(DeclarBody::class, DeclarBodyF1::class)
class Declar {
    @XmlAttribute(name = "noNamespaceSchemaLocation", namespace = "http://www.w3.org/2001/XMLSchema-instance")
    var schemaLocation: String? = null

    @XmlElement(name = "DECLARHEAD", required = true)
    var declarHead: DeclarHead? = null

    @XmlElements(
        XmlElement(name = "DECLARBODY", type = DeclarBody::class),
        XmlElement(name = "DECLARBODY", type = DeclarBodyF1::class)
    )
    var declarBody: Any? = null

    val declarBodyMain: DeclarBody?
        // Helper methods
        get() = if (declarBody is DeclarBody) declarBody as DeclarBody else null

    val declarBodyF1: DeclarBodyF1?
        get() = if (declarBody is DeclarBodyF1) declarBody as DeclarBodyF1 else null

    fun setDeclarBody(body: DeclarBody?) {
        this.declarBody = body
        this.schemaLocation = "F0100214.xsd"
    }

    fun setDeclarBody(body: DeclarBodyF1?) {
        this.declarBody = body
        this.schemaLocation = "F0121214.xsd"
    }
}

