package org.faust;

import org.faust.config.EnvironmentService;
import org.faust.listeners.Listener;
import org.faust.stats.StatsService;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ServiceConfig {

    private final ApplicationContext applicationContext;

    public ServiceConfig(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @Bean
    public Listener getListener() {
        EnvironmentService envService = applicationContext.getBean(EnvironmentService.class);
        StatsService statsService = applicationContext.getBean(StatsService.class);
        int inputPort = envService.getInputPort();
        int outputPort = envService.getOutputPort();
        String outIP = envService.getOutIP();

        Listener listener = new Listener(inputPort, outputPort, outIP, statsService);
        return listener;
    }

}
