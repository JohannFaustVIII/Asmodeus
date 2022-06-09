# Asmodeus

The idea of Asmodeus is to fulfill "lust" of monitoring traffic between containers. Traffic, coming through Asmodeus, is monitored and saved into pcap file. (and it is a playground for new ideas)

## Asmodeus Environment Variables

Name | Default | Description |
--- | --- | --- |
ASMO_CONFIG_FILE | null | Defines path to configuration file. If set, `ASMO_IN_PORT`, `ASMO_OUT_PORT` and `ASMO_OUT_IP` are ignored.
ASMO_IN_PORT | 8012 | Defines port listened by Asmodeus. Can be connected from outside to forward traffic.
ASMO_OUT_IP | 127.0.0.1 | Defines IP to which Asmodeus will connect to forward traffic.
ASMO_OUT_PORT | 5432 | Defines port to which Asmodeus will connect to forward traffic.
ASMO_HTTP_PORT | 8080 | Defines HTTP port used by Asmodeus.
ASMO_WS_FILE | file.pcap | Defines path to pcap file. Asmodeus will save forwarded packets to this file.
ASMO_PACKET_COUNT | 1000 | Defines how many last packets should be written to a pcap file.
ASMO_PACKET_AGE | 60 | Defines max age of a packet in pcap file in seconds.

## Asmodeus Configuration File Format

Configuration file enables setting multiple forwarding paths in Asmodeus. An example of configuration file can be seen below.

```yaml
forwarders:
  - inputPort: 8901
    outputPort: 5432
    outputIp: '192.145.0.12'
    packetsCount: 10
    packetAge: 30
  - inputPort: 8902
    outputPort: 3840
    outputIp: '127.0.0.1'
    packetsCount: 1000
    packetAge: 90
  - inputPort: 8903
    outputPort: 8203
    outputIp: 'container'
    packetsCount: 500
    packetAge: 120
```

Configuration of forwarding paths declares in a list of `forwarders`, following yaml format. Each element in a list has to declare three values:
* `inputPort` - declares what port will be listened by Asmodeus to forward that path. Value can't be repeated between forwarding paths.
* `outputPort` - declares to what port Asmodeus will connect to forward traffic.
* `outputIp` - declares to what IP Asmodeus will connect to forward traffic.
* `packetsCount` - declares how many packets from the connection should be put in pcap file.
* `packetAge` - declared max age of a packet in pcap file in seconds