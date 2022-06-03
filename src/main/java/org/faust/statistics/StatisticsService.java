package org.faust.statistics;

import io.micrometer.core.instrument.Metrics;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@EnableScheduling
@Service
public class StatisticsService {

    private final List<Statistics> stats = new ArrayList<>();
    private final List<Statistics> prometheusStats;
    private final Executor statsExecutor = Executors.newSingleThreadExecutor();
    private final Executor prometheusExecutor = Executors.newSingleThreadExecutor();

    public StatisticsService() {
        prometheusStats = Metrics.gauge("readBytes", Collections.emptyList(), new ArrayList<>(),
                list -> list
                        .stream()
                        .filter(stat -> System.currentTimeMillis() - stat.getTimestamp() < 1000)
                        .mapToInt(Statistics::getCount)
                        .sum());
        ScheduledExecutorService executorService = Executors.newScheduledThreadPool(1);
        executorService.scheduleAtFixedRate(this::show, 5, 5, TimeUnit.SECONDS);
    }

    public void add(Statistics forwardingStats) {
        statsExecutor.execute(() -> {
            synchronized (stats) {
                stats.add(forwardingStats);
            }
        });
        prometheusExecutor.execute(() -> {
            synchronized (prometheusStats) {
                prometheusStats.add(forwardingStats);
            }
        });
    }

    public void show() {
        synchronized (stats) {
            System.out.println("Last reads: " + stats.size());
            for (Statistics stat : stats) {
                System.out.println(stat.getLogMessage());
            }
            stats.clear();
        }
    }

    @Scheduled(fixedRate = 1000)
    public void removePrometheusStats() {
        synchronized (prometheusStats) {
            List<Statistics> statsToRemove = prometheusStats.stream()
                    .filter(stat -> System.currentTimeMillis() - stat.getTimestamp() > 1000)
                    .collect(Collectors.toList());
            prometheusStats.removeAll(statsToRemove);
            if (statsToRemove.size() > 0) {
                System.out.println("Removed " + statsToRemove.size() + " Prometheus stats");
            }
        }
    }
}
