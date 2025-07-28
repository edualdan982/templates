package com.farmacia.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource("classpath:perfiles/help.properties")
@Profile("help")
public class PropertiessourceHelp {

}
