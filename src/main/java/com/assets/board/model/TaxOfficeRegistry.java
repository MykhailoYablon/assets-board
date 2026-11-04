package com.assets.board.model;

import org.springframework.stereotype.Component;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilderFactory;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class TaxOfficeRegistry {

    private final List<TaxOfficeEntry> offices;
    private final Map<String, TaxOfficeEntry> byCSTI;

    public TaxOfficeRegistry() throws Exception {
        offices = parse();
        byCSTI = offices.stream().collect(Collectors.toMap(TaxOfficeEntry::cStiOrig, Function.identity()));
    }

    public List<TaxOfficeEntry> getAll() {
        return offices;
    }

    public TaxOfficeEntry findByCSTI(String cStiOrig) {
        TaxOfficeEntry entry = byCSTI.get(cStiOrig);
        if (entry == null) throw new IllegalArgumentException("Unknown tax office code: " + cStiOrig);
        return entry;
    }

    private static List<TaxOfficeEntry> parse() throws Exception {
        List<TaxOfficeEntry> result = new ArrayList<>();
        try (InputStream is = TaxOfficeRegistry.class.getResourceAsStream("/SPR_STI.xml")) {
            var doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(is);
            NodeList rows = doc.getElementsByTagName("ROW");
            for (int i = 0; i < rows.getLength(); i++) {
                Element row = (Element) rows.item(i);
                String cReg = text(row, "C_REG");
                NodeList stiRows = row.getElementsByTagName("ROW_STI");
                for (int j = 0; j < stiRows.getLength(); j++) {
                    Element sti = (Element) stiRows.item(j);
                    String cStiOrig = text(sti, "C_STI");
                    String cRaj = text(sti, "C_RAJ");
                    String name = text(sti, "NAME_STI");
                    result.add(new TaxOfficeEntry(cReg, cRaj, cStiOrig, name));
                }
            }
        }
        return result;
    }

    private static String text(Element el, String tag) {
        NodeList nl = el.getElementsByTagName(tag);
        return nl.getLength() > 0 ? nl.item(0).getTextContent().trim() : "";
    }
}
