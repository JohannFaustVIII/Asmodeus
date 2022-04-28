package org.faust.listeners;

import org.faust.config.EnvironmentService;
import org.faust.config.ForwardConfig;
import org.faust.file.WSService;
import org.faust.stats.StatsService;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
public class ListenerService {

    private final EnvironmentService envService;
    private final StatsService statsService;
    private final WSService wsService;

    public ListenerService(EnvironmentService envService, StatsService statsService, WSService wsService) {
        this.envService = envService;
        this.statsService = statsService;
        this.wsService = wsService;
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
                            .statsService(statsService)
                            .wsService(wsService)
                            .build()
                            .listen();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        });
    }
}
