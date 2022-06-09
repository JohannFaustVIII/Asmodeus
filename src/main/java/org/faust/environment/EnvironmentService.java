package org.faust.environment;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import org.faust.forwarding.ForwardConfig;
import org.faust.forwarding.ForwardYAMLConfig;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
public class EnvironmentService {

    public List<ForwardConfig> getForwardingConfig() {
        String configFile = getConfigFile();
        if (configFile != null) {
            ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
            ForwardYAMLConfig config = null;
            try {
                config = mapper.readValue(new File(configFile), ForwardYAMLConfig.class);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return config.getForwarders();
        } else {
            return Arrays.asList(new ForwardConfig(
                    getInputPort(),
                    getOutputPort(),
                    getOutIP(),
                    getPacketsCount(),
                    getPacketAge()
            ));
        }
    }

    private int getInputPort() {
        return getEnvInt("ASMO_IN_PORT", 8012);
    }

    private int getOutputPort() {
        return getEnvInt("ASMO_OUT_PORT", 5432);
    }

    private String getOutIP() {
        return getEnvString("ASMO_OUT_IP", "127.0.0.1");
    }

    public int getHttpPort() {
        return getEnvInt("ASMO_HTTP_PORT", 8080);
    }

    public int getPacketsCount() {
        return getEnvInt("ASMO_PACKET_COUNT", 1000);
    }

    public int getPacketAge() {
        return getEnvInt("ASMO_PACKET_AGE", 60);
    }

    private String getConfigFile() {
        return getEnvString("ASMO_CONFIG_FILE", null);
    }

    public String getWSFilePath() {
        return getEnvString("ASMO_WS_FILE", "file.pcap");
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
