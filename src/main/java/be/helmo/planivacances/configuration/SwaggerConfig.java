package be.helmo.planivacances.configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.context.annotation.Import;
import org.springdoc.core.SwaggerUiConfigParameters;
import org.springdoc.core.SpringDocUtils;


@Configuration
@Import(SwaggerUiConfigParameters.class)
public class SwaggerConfig {
    static {
        SpringDocUtils.getConfig().addAnnotationsToIgnore(
                org.springframework.context.annotation.Configuration.class
        );
    }

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Planivacances API Documentation")
                        .version("0.0.1")
                        .description("API Rest projet de gestion de vacances"));
    }
}
