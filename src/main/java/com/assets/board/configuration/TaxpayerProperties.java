package com.assets.board.configuration;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "taxpayer")
public class TaxpayerProperties {

    private String tin;
    private String name;
    private String city;
    private String street;
    private String building;
    private String cReg;
    private String cRaj;
    private String cStiOrig;
    private String taxOffice;
}
