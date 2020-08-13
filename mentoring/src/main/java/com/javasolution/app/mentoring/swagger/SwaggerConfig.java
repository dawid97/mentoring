package com.javasolution.app.mentoring.swagger;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.service.VendorExtension;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

@Configuration
@EnableSwagger2
public class SwaggerConfig {

    List<VendorExtension> vendorExtension=new ArrayList<>();


    Contact contact=new Contact(
            "Dawid Ulfik",
            "https://www.facebook.com/dawid.ulfik.3",
            "dawulf97@gmail.pl"
    );

    ApiInfo apiInfo = new ApiInfo(
            "Mentoring app RESTful Web Service documentation",
            "This pages documents Mentoring app RESTful Web Service endpoints",
            "1.0",
            "http://www.javasolutionblog.com",
            contact,
            "Apache 2.0",
            "http://www.apache.org/licenses/LICENSE-2.0",
            vendorExtension);

    @Bean
    public Docket apiDocket() {

        Docket docket = new Docket(DocumentationType.SWAGGER_2)
                .protocols(new HashSet<>(Arrays.asList("HTTP")))
                .apiInfo(apiInfo)
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.javasolution.app.mentoring"))
                .paths(PathSelectors.any())
                .build();

        return docket;
    }
}