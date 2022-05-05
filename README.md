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

## Asmodeus Configuration File Format

Configuration file enables setting multiple forwarding paths in Asmodeus. An example of configuration file can be seen below.

```yaml
forwarders:
  - inputPort: 8901
    outputPort: 5432
    outputIp: '192.145.0.12'
  - inputPort: 8902
    outputPort: 3840
    outputIp: '127.0.0.1'
  - inputPort: 8903
    outputPort: 8203
    outputIp: 'container'
```

Configuration of forwarding paths declares in a list of `forwarders`, following yaml format. Each element in a list has to declare three values:
* `inputPort` - declares what port will be listened by Asmodeus to forward that path. Value can't be repeated between forwarding paths.
* `outputPort` - declares to what port Asmodeus will connect to forward traffic.
* `outputIp` - declared to what IP Asmodeus will connect to forward traffic.