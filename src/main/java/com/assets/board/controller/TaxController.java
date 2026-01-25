package com.assets.board.controller;

import com.assets.board.dto.TaxReportDto;
import com.assets.board.model.tax.*;
import com.assets.board.service.TaxService;
import com.assets.board.service.XmlGeneratorService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.UUID;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/taxes")
@AllArgsConstructor
public class TaxController {

    private static final String EXPORT_DIR = "exports";

    private final TaxService taxService;
    private final XmlGeneratorService xmlGeneratorService;

    @PostMapping("/dividends")
    public TaxReportDto calculateDividendTaxes(@RequestParam("file") MultipartFile ibReportFile,
                                               @RequestParam boolean isMilitary) {
        return taxService.calculateDividendTax(ibReportFile, isMilitary);
    }


    @GetMapping("/generate")
    public ResponseEntity<String> generateXml() throws Exception {
        UUID uuid = UUID.randomUUID();
        String mainFilename = String.format("F0100214_Zvit_%s.xml", uuid);
        String f1Filename = String.format("F0121214_DodatokF1_%s.xml", uuid);

        String mainFilePath = EXPORT_DIR + File.separator + mainFilename;
        String f1FilePath = EXPORT_DIR + File.separator + f1Filename;

        Declar mainDeclar = createSampleDeclar(uuid);
        Declar f1Declar = createDeclarF1(uuid);


        xmlGeneratorService.saveXmlToFile(mainDeclar, mainFilePath);
        xmlGeneratorService.saveXmlToFile(f1Declar, f1FilePath);

//        String xml = xmlGeneratorService.generateDeclarXml(declar);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_XML);

        return ResponseEntity.ok()
                .headers(headers)
                .body("OK");
    }

    private Declar createSampleDeclar(UUID uuid) {
        Declar declar = new Declar();

        DeclarHead head = new DeclarHead();
        head.setTin("");
        head.setCDoc("F01");
        head.setCDocSub("002");
        head.setCDocVer("11");
        head.setCDocType("0");
        head.setCDocCnt("1");
        head.setCReg("0");
        head.setCRaj("15");
        head.setPeriodMonth("12");
        head.setPeriodType("5");
        head.setPeriodYear("2025");
        head.setCStiOrig("915");
        head.setCDocStan("1");

        //date of generation
        head.setDFill("16012026");
//        head.setSoftware("https://ua-tax.web.app/ 1.7.0");

        // Create linked docs
        Doc doc = new Doc();
        doc.setNum("1");
        doc.setType("1");
        doc.setCDoc("F01");
        doc.setCDocSub("212");
        doc.setCDocVer("11");
        doc.setCDocType("1");
        doc.setCDocCnt("1");
        doc.setCDocStan("1");

        doc.setFilename("F0121214_DodatokF1_" + uuid + ".xml");

        LinkedDocs linkedDocs = new LinkedDocs();
        linkedDocs.setDocs(Arrays.asList(doc));

        head.setLinkedDocs(linkedDocs);
        declar.setDeclarHead(head);

        // DECLARBODY
        DeclarBody body = new DeclarBody();
        body.setH01("1");
        body.setH03("1");
        body.setH05("1");
        body.setHbos("Test");
        body.setHcity("Test");
        body.setHd1("1");
        body.setHfill("16012026");
        body.setHname("Test Test");
        body.setHsti("ДПС");
        body.setHstreet("Stree");
        body.setHtin("");
        body.setHz("1");
        body.setHzy("2025");
        body.setR0104g3(BigDecimal.valueOf(2323.86));
        body.setR0104g6(BigDecimal.valueOf(209.15));
        body.setR0104g7(BigDecimal.ZERO);
        body.setR0108g3(BigDecimal.ZERO);
        body.setR0108g6(BigDecimal.ZERO);
        body.setR0108g7(BigDecimal.ZERO);
        body.setR01010g2s("Інші проценти");
        body.setR010g3(BigDecimal.ZERO);
        body.setR010g6(BigDecimal.ZERO);
        body.setR010g7(BigDecimal.ZERO);
        body.setR012g3(BigDecimal.ZERO);
        body.setR013g3(BigDecimal.ZERO);
        body.setR018g3(BigDecimal.ZERO);
        body.setR0201g3(BigDecimal.ZERO);
        body.setR0211g3(BigDecimal.ZERO);

        declar.setDeclarBody(body);

        return declar;
    }

    private Declar createDeclarF1(UUID uuid) {
        Declar declar = new Declar();

        // DECLARHEAD
        DeclarHead head = new DeclarHead();
        head.setTin("");
        head.setCDoc("F01");
        head.setCDocSub("212");
        head.setCDocVer("11");
        head.setCDocType("1");
        head.setCDocCnt("1");
        head.setCReg("0");
        head.setCRaj("15");
        head.setPeriodMonth("12");
        head.setPeriodType("5");
        head.setPeriodYear("2025");
        head.setCStiOrig("915");
        head.setCDocStan("1");
        head.setDFill("16012026");

        // Create linked docs
        Doc doc = new Doc();
        doc.setNum("1");
        doc.setType("2");
        doc.setCDoc("F01");
        doc.setCDocSub("002");
        doc.setCDocVer("11");
        doc.setCDocType("0");
        doc.setCDocCnt("1");
        doc.setCDocStan("1");
        doc.setFilename("F0100214_Zvit_" + uuid + ".xml");

        LinkedDocs linkedDocs = new LinkedDocs();
        linkedDocs.setDocs(Arrays.asList(doc));
        head.setLinkedDocs(linkedDocs);

        declar.setDeclarHead(head);

        // DECLARBODY F1
        DeclarBodyF1 body = new DeclarBodyF1();
        body.setHbos("Test Test");
        body.setHtin("");
        body.setHz("1");
        body.setHzy("2025");
        body.setR001g4("32130.56");
        body.setR001g5("31196.75");
        body.setR001g6("933.81");
        body.setR003g6("933.81");
        body.setR004g6("168.09");
        body.setR005g6("46.69");
        body.setR031g6("933.81");
        body.setR042g6("168.09");
        body.setR052g6("46.69");

        // Add table rows
        body.addTableRow("1", "4", "GOOGL 1шт.", "6903.66", "6259.61", "644.05");
        body.addTableRow("2", "4", "CRM 1шт.", "11047.23", "11016.3", "30.93");
        body.addTableRow("3", "4", "VTI 1шт.", "14179.67", "13920.84", "258.83");

        declar.setDeclarBody(body);

        return declar;
    }
}
