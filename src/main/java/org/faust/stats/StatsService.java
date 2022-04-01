package org.faust.stats;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Service
public class StatsService {

    private final List<ForwardingStats> stats = new ArrayList<>();

    public StatsService() {
        ScheduledExecutorService executorService = Executors.newScheduledThreadPool(1);
        executorService.scheduleAtFixedRate(this::show, 5, 5, TimeUnit.SECONDS);
    }

    public void add(ForwardingStats forwardingStats) {
        synchronized (stats) {
            stats.add(forwardingStats);
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
}
