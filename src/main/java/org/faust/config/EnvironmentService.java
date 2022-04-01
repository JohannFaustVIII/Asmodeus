package org.faust.config;

import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class EnvironmentService {

    public int getInputPort() {
        return getEnvInt("ASMO_IN_PORT", 8012);
    }

    public int getOutputPort() {
        return getEnvInt("ASMO_OUT_PORT", 5432);
    }

    public String getOutIP() {
        return getEnvString("ASMO_OUT_IP", "127.0.0.1");
    }

    public int getHttpPort() {
        return getEnvInt("ASMO_HTTP_PORT", 8080);
    }

    private int getEnvInt(String name, int defaultValue) {
        return getEnv(name).map(Integer::parseInt).orElse(defaultValue);
    }

    private String getEnvString(String name, String defaultValue) {
        return getEnv(name).orElse(defaultValue);
    }

    private Optional<String> getEnv(String name) {
        return Optional.ofNullable(System.getenv(name));
    }


}
