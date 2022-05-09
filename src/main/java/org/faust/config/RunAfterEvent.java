package org.faust.config;

import org.faust.wireshark.WiresharkService;
import org.faust.forwarding.ListenerService;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

@Service
public class RunAfterEvent {

    private final ListenerService listenerService;
    private final WiresharkService wiresharkService;

    public RunAfterEvent(ListenerService listenerService, WiresharkService wiresharkService) {
        this.listenerService = listenerService;
        this.wiresharkService = wiresharkService;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void runAfterStartup() {
        wiresharkService.processEvents();
        listenerService.startListeners();
    }
}
