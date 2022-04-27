package org.faust.config;

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

    public void setForwarders(List<ForwardConfig> forwarders) {
        this.forwarders = forwarders;
    }

    @Override
    public String toString() {
        return "ForwardYAMLConfig{" +
                "forwarders=" + forwarders +
                '}';
    }
}
