package com.assets.board.service;

import com.assets.board.model.tax.Declar;
import com.assets.board.model.tax.DeclarBody;
import com.assets.board.model.tax.DeclarBodyF1;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.Marshaller;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.StringWriter;

@Service
public class XmlGeneratorService {

    private static final String ENCODING = "windows-1251";

    public String generateDeclarXml(Declar declar) throws Exception {
        JAXBContext context = JAXBContext.newInstance(Declar.class);
        Marshaller marshaller = context.createMarshaller();

        // Configure marshaller
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        marshaller.setProperty(Marshaller.JAXB_ENCODING, ENCODING);
        marshaller.setProperty(Marshaller.JAXB_FRAGMENT, false);

        StringWriter writer = new StringWriter();
        marshaller.marshal(declar, writer);

        return writer.toString();
    }

    public void saveXmlToFile(Declar declar, String filepath) throws Exception {
        JAXBContext context = JAXBContext.newInstance(Declar.class, DeclarBody.class, DeclarBodyF1.class);
        Marshaller marshaller = context.createMarshaller();

        // Configure marshaller
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        marshaller.setProperty(Marshaller.JAXB_ENCODING, ENCODING);
        marshaller.setProperty(Marshaller.JAXB_FRAGMENT, false);


        File file = new File(filepath);
        marshaller.marshal(declar, file);
    }
}
