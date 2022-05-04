# Asmodeus

## Asmodeus Environment Variables

Name | Default | Description |
--- | --- | --- |
ASMO_CONFIG_FILE | null | Defines path to configuration file. If set, `ASMO_IN_PORT`, `ASMO_OUT_PORT` and `ASMO_OUT_IP` are ignored.
ASMO_IN_PORT | 8012 | Defines port listened by Asmodeus. Can be connected from outside to forward traffic.
ASMO_OUT_IP | 127.0.0.1 | Defines IP to which Asmodeus will connect to forward traffic.
ASMO_OUT_PORT | 5432 | Defines port to which Asmodeus will connect to forward traffic.
ASMO_HTTP_PORT | 8080 | Defines HTTP port used by Asmodeus.
ASMO_WS_FILE | file.pcap | Defines path to pcap file. Asmodeus will save forwarded packets to this file.