package com.assets.board;

import com.assets.board.service.DividendService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@EnableAsync
@SpringBootApplication
public class AssetsApplication {
    public static void main(String[] args) {

        // 1. Capture the context returned by run()
        ConfigurableApplicationContext context = SpringApplication.run(AssetsApplication.class, args);

        // 2. Retrieve the bean by its class or name
        DividendService service = context.getBean(DividendService.class);

        // 3. Call the method
        service.calculateDividendTax();
    }
} 