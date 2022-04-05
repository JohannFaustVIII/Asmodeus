package org.faust.stats;

import io.micrometer.core.instrument.Metrics;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@EnableScheduling
@Service
public class StatsService {

    private final List<ForwardingStats> stats = new ArrayList<>();
    private final List<ForwardingStats> prometheusStats;

    public StatsService() {
        prometheusStats = Metrics.gauge("readBytes", Collections.emptyList(), new ArrayList<>(),
                list -> list
                        .stream()
                        .filter(stat -> System.currentTimeMillis() - stat.getTimestamp() < 1000)
                        .mapToInt(ForwardingStats::getCount)
                        .sum());
        ScheduledExecutorService executorService = Executors.newScheduledThreadPool(1);
        executorService.scheduleAtFixedRate(this::show, 5, 5, TimeUnit.SECONDS);
    }

    public void add(ForwardingStats forwardingStats) {
        synchronized (stats) {
            stats.add(forwardingStats);
            prometheusStats.add(forwardingStats);
        }
    }

    public void show() {
        synchronized (stats) {
            System.out.println("Last reads: " + stats.size());
            for (ForwardingStats stat : stats) {
                System.out.println("ThreadId: " + stat.getThreadId() + "\t timestamp: " + stat.getTimestamp() + "\t read: " + stat.getCount());
            }
            stats.clear();
        }
    }

    @Scheduled(fixedRate = 1000)
    public void removePrometheusStats() {
        List<ForwardingStats> statsToRemove = prometheusStats.stream()
                .filter(stat -> System.currentTimeMillis() - stat.getTimestamp() > 1000)
                .collect(Collectors.toList());
        prometheusStats.removeAll(statsToRemove);
        if (statsToRemove.size() > 0) {
            System.out.println("Removed " + statsToRemove.size() + " Prometheus stats");
        }
    }
}
