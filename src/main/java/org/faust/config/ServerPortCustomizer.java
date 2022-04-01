package org.faust.config;

import org.springframework.boot.web.server.ConfigurableWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.stereotype.Component;

@Component
public class ServerPortCustomizer implements WebServerFactoryCustomizer<ConfigurableWebServerFactory> {

    private final EnvironmentService environmentService;

    public ServerPortCustomizer(EnvironmentService environmentService) {
        this.environmentService = environmentService;
    }

    @Override
    public void customize(ConfigurableWebServerFactory factory) {
        factory.setPort(environmentService.getHttpPort());
    }
}
