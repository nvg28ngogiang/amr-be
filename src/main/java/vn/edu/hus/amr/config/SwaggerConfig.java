package vn.edu.hus.amr.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.RequestHandler;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.ParameterBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.schema.ModelRef;
import springfox.documentation.service.*;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Configuration
@EnableSwagger2
public class SwaggerConfig {

    @Value("${app.api.title}")
    private String title;

    @Value("${app.api.version}")
    private String version;

    @Value("${app.api.description}")
    private String description;

    @Value("${app.api.base-package}")
    private String basePackage;

    @Value("${app.api.contact-name}")
    private String contactName;

    @Value("${app.api.email}")
    private String contactEmail;
    @Bean
    public Docket api() {
        return new Docket(DocumentationType.SWAGGER_2)
                .select()
                .apis(RequestHandlerSelectors.basePackage(basePackage))
                .paths(PathSelectors.any())
                .build()
                .globalOperationParameters(Collections.singletonList(headerParameter()))
                .apiInfo(apiInfo());
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title(title)
                .description(description)
                .version(version)
                .contact(new Contact(contactName, null, contactEmail))
                .build();
    }

    private Parameter headerParameter() {
        return new ParameterBuilder()
                .name("Authorization")
                .description("Add token here")
                .modelRef(new ModelRef("string"))
                .parameterType("header")
                .required(false)
                .build();
    }
}
