package org.faust;

import org.faust.listeners.Listener;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class RunAfterEvent {

    private final Listener listener;

    public RunAfterEvent(Listener listener) {
        this.listener = listener;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void runAfterStartup() throws IOException {
        listener.startListening();
    }
}
