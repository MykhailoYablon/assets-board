package com.assets.board;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.TimeZone;

@EnableScheduling
@EnableAsync
@SpringBootApplication
public class AssetsApplication {
    public static void main(String[] args) {

        TimeZone.setDefault(TimeZone.getTimeZone("Europe/Kyiv"));
        SpringApplication.run(AssetsApplication.class, args);
        // 1. Capture the context returned by run()
//        ConfigurableApplicationContext context = SpringApplication.run(AssetsApplication.class, args);
//
//        // 2. Retrieve the bean by its class or name
//        TaxService service = context.getBean(TaxService.class);
//
//        // 3. Call the method
//        service.calculateDividendTax(ibReportFile, false);
    }
} 