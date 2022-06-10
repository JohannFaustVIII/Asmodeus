package org.faust.wireshark;

import org.faust.wireshark.token.RawDataToken;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
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

    private final Executor executor = Executors.newSingleThreadExecutor();

    private final WiresharkEventContainer eventContainer;

    WiresharkEventHandler(int counter, int packetAge, Consumer<WiresharkEventHandler> endAction) {
        this.eventContainer = new WiresharkEventContainer(counter, packetAge);
        this.endAction = Objects.requireNonNull(endAction);
    }

    public void deregister() {
        endAction.accept(this);
    }

    public void addEvent(WiresharkForwardEvent event) {
        executor.execute(() -> eventContainer.addEvent(event));
    }

    public List<RawDataToken> getRawPackets() {
        return eventContainer.getRawPackets();
    }
}
