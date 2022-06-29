package org.faust.pcap;

import org.faust.pcap.token.RawDataToken;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

public class PcapEventHandler {

    private final Consumer<PcapEventHandler> endAction;

    private final Executor executor = Executors.newSingleThreadExecutor();

    private final PcapEventContainer eventContainer;

    PcapEventHandler(int counter, int packetAge, Consumer<PcapEventHandler> endAction) {
        this.eventContainer = new PcapEventContainer(counter, packetAge);
        this.endAction = Objects.requireNonNull(endAction);
    }

    public void deregister() {
        endAction.accept(this); //TODO: think about removing? won't happen now
    }

    public void addEvent(PcapForwardEvent event) {
        executor.execute(() -> eventContainer.addEvent(event));
    }

    public List<RawDataToken> getRawPackets() {
        return eventContainer.getRawPackets();
    }
}
