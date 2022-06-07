package org.faust.forwarding;

import org.faust.environment.EnvironmentService;
import org.faust.wireshark.WiresharkService;
import org.faust.statistics.StatisticsService;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
public class ListenerService {

    private final EnvironmentService envService;
    private final StatisticsService statisticsService;
    private final WiresharkService wiresharkService;

    public ListenerService(EnvironmentService envService, StatisticsService statisticsService, WiresharkService wiresharkService) {
        this.envService = envService;
        this.statisticsService = statisticsService;
        this.wiresharkService = wiresharkService;
    }

    public void startListeners() {
        List<ForwardConfig> forwardConfigList = envService.getForwardingConfig();

        ExecutorService executorService = Executors.newFixedThreadPool(forwardConfigList.size());

        forwardConfigList.forEach(config -> {
            executorService.submit(() -> {
                try {
                    new Listener.ListenerBuilder()
                            .inputPort(config.getInputPort())
                            .outputPort(config.getOutputPort())
                            .outIp(config.getOutputIp())
                            .statsService(statisticsService)
                            .wsHandler(wiresharkService.getHandler(config.getPacketsCount()))
                            .count(config.getPacketsCount())
                            .build()
                            .listen();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        });
    }
}
