package com.mystrapi.strapi.validation;

import org.springframework.boot.autoconfigure.validation.ValidationConfigurationCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * bean validation 配置
 * @author tangqiang
 */
@Configuration
public class StrapiValidationConfiguration {

    @Bean
    public ValidationConfigurationCustomizer validationConfigurationCustomizer(){
        return configuration -> {
            // do something
        };
    }

}
