package cris.prs.msg;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.integration.config.EnableIntegration;

@SpringBootApplication
@ComponentScan(basePackages = {"cris.prs.msg"})
@EnableIntegration
public class App {

    public static void main(String[] args) {
        SpringApplication.run(App.class, args);
    }
}