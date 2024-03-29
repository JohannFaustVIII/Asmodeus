package org.faust.forwarding;

import java.util.List;

public class ForwardYAMLConfig {

    private List<ForwardConfig> forwarders;

    public ForwardYAMLConfig() {
    }

    public ForwardYAMLConfig(List<ForwardConfig> forwarders) {
        this.forwarders = forwarders;
    }

    public List<ForwardConfig> getForwarders() {
        return forwarders;
    }

    @Override
    public String toString() {
        return "ForwardYAMLConfig{" +
                "forwarders=" + forwarders +
                '}';
    }
}
