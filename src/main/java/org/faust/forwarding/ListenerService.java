package org.faust.forwarding;

import org.faust.environment.EnvironmentService;
import org.faust.pcap.PcapService;
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
    private final PcapService pcapService;

    public ListenerService(EnvironmentService envService, StatisticsService statisticsService, PcapService pcapService) {
        this.envService = envService;
        this.statisticsService = statisticsService;
        this.pcapService = pcapService;
    }

    public void startListeners() {
        List<ForwardConfig> forwardConfigList = envService.getForwardingConfig();

        forwardConfigList.forEach(this::startListener);
    }

    public void startListener(ForwardConfig config) {
        // TODO: add registering to a list, to enable turning down later

        Listener listener = new Listener.ListenerBuilder()
                .inputPort(config.getInputPort())
                .outputPort(config.getOutputPort())
                .outIp(config.getOutputIp())
                .statsService(statisticsService)
                .wsHandler(pcapService.getHandler(config.getPacketsCount(), config.getPacketAge(), config.getForwardName()))
                .build();

        new Thread(() -> {
            try {
                listener.listen(); // TODO: consume IOException inside the method?
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }
}
