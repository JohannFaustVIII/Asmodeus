package org.faust.listeners;

import org.faust.config.EnvironmentService;
import org.faust.stats.StatsService;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ListenerConfig {

    private final ApplicationContext applicationContext;

    public ListenerConfig(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @Bean
    public Listener createListener() {
        EnvironmentService envService = applicationContext.getBean(EnvironmentService.class);
        StatsService statsService = applicationContext.getBean(StatsService.class);
        int inputPort = envService.getInputPort();
        int outputPort = envService.getOutputPort();
        String outIP = envService.getOutIP();

        return new Listener(inputPort, outputPort, outIP, statsService);
    }

}
