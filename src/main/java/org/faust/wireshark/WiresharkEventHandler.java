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

    public void addEvent(WiresharkForwardEvent event) {
        /**
         * Create two files, and put bytes in one of them, if finished, switch to another, if another is full, clean previous. Repeat.
         * When getting to read, join them, but read all from actual, and put as many as needed from previous, to fulfill counter.
         * This is slow, needs thread pool to put bytes in container, or it's gonna slow forwarder.
         */
    }
}
