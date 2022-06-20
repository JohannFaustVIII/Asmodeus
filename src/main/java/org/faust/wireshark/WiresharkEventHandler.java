package org.faust.wireshark;

import org.faust.wireshark.token.RawDataToken;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

public class WiresharkEventHandler {

    private final Consumer<WiresharkEventHandler> endAction;

    private final Executor executor = Executors.newSingleThreadExecutor();

    private final WiresharkEventContainer eventContainer;

    WiresharkEventHandler(int counter, int packetAge, Consumer<WiresharkEventHandler> endAction) {
        this.eventContainer = new WiresharkEventContainer(counter, packetAge);
        this.endAction = Objects.requireNonNull(endAction);
    }

    public void deregister() {
        endAction.accept(this); //TODO: think about removing? won't happen now
    }

    public void addEvent(WiresharkForwardEvent event) {
        executor.execute(() -> eventContainer.addEvent(event));
    }

    public List<RawDataToken> getRawPackets() {
        return eventContainer.getRawPackets();
    }
}
