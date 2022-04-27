package org.faust.config;

import org.faust.file.WSService;
import org.faust.listeners.ListenerService;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class RunAfterEvent {

    private final ListenerService listenerService;
    private final WSService wsService;

    public RunAfterEvent(ListenerService listenerService, WSService wsService) {
        this.listenerService = listenerService;
        this.wsService = wsService;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void runAfterStartup() {
        wsService.processEvents();
        listenerService.startListeners();
    }
}
