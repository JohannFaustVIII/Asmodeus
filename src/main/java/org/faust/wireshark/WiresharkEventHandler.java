package org.faust.wireshark;

import java.util.Objects;
import java.util.function.Consumer;

public class WiresharkEventHandler {

    private final Consumer<WiresharkEventHandler> endAction;

    WiresharkEventHandler(Consumer<WiresharkEventHandler> endAction) {
        this.endAction = Objects.requireNonNull(endAction);
    }

    public void deregister() {
        endAction.accept(this);
    }
}
