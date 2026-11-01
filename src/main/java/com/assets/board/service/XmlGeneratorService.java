package com.assets.board.service;

import com.assets.board.model.tax.Declar;
import com.assets.board.model.tax.DeclarBody;
import com.assets.board.model.tax.DeclarBodyF1;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.StringWriter;

@Service
public class XmlGeneratorService {

    private static final String ENCODING = "windows-1251";
    private static final JAXBContext JAXB_CONTEXT;

    static {
        try {
            JAXB_CONTEXT = JAXBContext.newInstance(Declar.class, DeclarBody.class, DeclarBodyF1.class);
        } catch (JAXBException e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    public String generateDeclarXml(Declar declar) throws Exception {
        Marshaller marshaller = createMarshaller();
        StringWriter writer = new StringWriter();
        marshaller.marshal(declar, writer);
        return writer.toString();
    }

    public void saveXmlToFile(Declar declar, String filepath) throws Exception {
        createMarshaller().marshal(declar, new File(filepath));
    }

    private Marshaller createMarshaller() throws JAXBException {
        Marshaller marshaller = JAXB_CONTEXT.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        marshaller.setProperty(Marshaller.JAXB_ENCODING, ENCODING);
        marshaller.setProperty(Marshaller.JAXB_FRAGMENT, false);
        return marshaller;
    }
}
