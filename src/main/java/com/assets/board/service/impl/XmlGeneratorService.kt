package com.assets.board.service.impl

import com.assets.board.model.tax.Declar
import com.assets.board.model.tax.DeclarBody
import com.assets.board.model.tax.DeclarBodyF1
import jakarta.xml.bind.JAXBContext
import jakarta.xml.bind.Marshaller
import org.springframework.stereotype.Service
import java.io.File
import java.io.StringWriter

@Service
class XmlGeneratorService {
    @Throws(Exception::class)
    fun generateDeclarXml(declar: Declar?): String {
        val context = JAXBContext.newInstance(Declar::class.java)
        val marshaller = context.createMarshaller()

        // Configure marshaller
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true)
        marshaller.setProperty(Marshaller.JAXB_ENCODING, ENCODING)
        marshaller.setProperty(Marshaller.JAXB_FRAGMENT, false)

        val writer = StringWriter()
        marshaller.marshal(declar, writer)

        return writer.toString()
    }

    @Throws(Exception::class)
    fun saveXmlToFile(declar: Declar?, filepath: String) {
        val context = JAXBContext.newInstance(Declar::class.java, DeclarBody::class.java, DeclarBodyF1::class.java)
        val marshaller = context.createMarshaller()

        // Configure marshaller
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true)
        marshaller.setProperty(Marshaller.JAXB_ENCODING, ENCODING)
        marshaller.setProperty(Marshaller.JAXB_FRAGMENT, false)


        val file = File(filepath)
        marshaller.marshal(declar, file)
    }

    companion object {
        private const val ENCODING = "windows-1251"
    }
}
