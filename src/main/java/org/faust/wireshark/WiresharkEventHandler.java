package org.faust.wireshark;

import java.util.Objects;
import java.util.function.Consumer;

/**
 * Idea: put here all packets from a single forwarder (so many connections)
 * Problem: many threads putting here at the same time, need synchronization without much blocking.
 * It should be optional, work as observer?
 * The best - run as another thread and run these data from other threads if possible.
 * Use distinct logger, packet handler in forwarding stream, and read from them? That's a lot of redundant data.
 *
 */
public class WiresharkEventHandler {

    private final Consumer<WiresharkEventHandler> endAction;

    WiresharkEventHandler(Consumer<WiresharkEventHandler> endAction) {
        this.endAction = Objects.requireNonNull(endAction);
    }

    public void deregister() {
        endAction.accept(this);
    }
}
