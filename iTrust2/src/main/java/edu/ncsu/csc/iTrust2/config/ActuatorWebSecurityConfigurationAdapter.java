package edu.ncsu.csc.iTrust2.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

/**
 * Configure permissions for Actuator endpoints, so it doesn't require
 * authentication on those endpoints.
 * 
 * @author bvolpat
 */
@Configuration
@EnableWebSecurity
@Order ( 10 )
public class ActuatorWebSecurityConfigurationAdapter extends WebSecurityConfigurerAdapter {
    @Override
    protected void configure ( final HttpSecurity http ) throws Exception {
        http.antMatcher( "/actuator/**" ).anonymous().and().csrf().disable();
    }
}
