package org.faust.config;

import org.faust.pcap.PcapService;
import org.faust.forwarding.ListenerService;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

@Service
public class RunAfterEvent {

    private final ListenerService listenerService;
    private final PcapService pcapService;

    public RunAfterEvent(ListenerService listenerService, PcapService pcapService) {
        this.listenerService = listenerService;
        this.pcapService = pcapService;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void runAfterStartup() {
        listenerService.startListeners();
    }
}
