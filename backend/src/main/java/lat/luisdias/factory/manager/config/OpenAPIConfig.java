package lat.luisdias.factory.manager.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.i18n.LocaleContextHolder;

import java.util.Locale;

@Configuration
public class OpenAPIConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Luís Eduardo Dias")
                                .email("edu_vahl@outlook.com")));
    }

    @Bean
    public OpenApiCustomizer openApiI18nCustomizer(MessageSource messageSource) {
        return openApi -> {
            Locale locale = LocaleContextHolder.getLocale();

            String title = messageSource.getMessage("swagger.api.title", null, locale);
            String description = messageSource.getMessage("swagger.api.description", null, locale);

            openApi.getInfo().setTitle(title);
            openApi.getInfo().setDescription(description);
        };
    }
}
