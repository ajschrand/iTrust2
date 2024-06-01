package edu.ncsu.csc.iTrust2.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Registers our audit logger with Spring so that events are logged properly
 * 
 * @author Kai Presler-Marshall
 *
 */
@Configuration
public class WebMvcInterceptorConfiguration implements WebMvcConfigurer {

    /**
     * Register the logging/audit interceptor on every REST endpoint
     *
     * @return RestTemplate with the interceptor
     */
    @Bean
    public AuditLogInterceptor getInterceptor () {
        return new AuditLogInterceptor();
    }

    @Override
    public void addInterceptors ( final InterceptorRegistry registry ) {
        registry.addInterceptor( getInterceptor() ).addPathPatterns( "/**" );
    }
}
