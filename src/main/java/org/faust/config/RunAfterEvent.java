package org.faust.config;

import org.faust.file.WSService;
import org.faust.listeners.Listener;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class RunAfterEvent {

    private final Listener listener;
    private final WSService wsService;

    public RunAfterEvent(Listener listener, WSService wsService) {
        this.listener = listener;
        this.wsService = wsService;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void runAfterStartup() throws IOException, InterruptedException {
        wsService.processEvents();
        listener.listen();
    }
}
