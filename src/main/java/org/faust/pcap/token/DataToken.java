package org.faust.pcap.token;

import org.faust.pcap.PcapForwardEvent;

import java.util.ArrayList;
import java.util.List;

public class DataToken implements Token {

    private final int seconds;
    private final int microseconds;
    private final int capturedPacketLength;
    private final int originalPacketLength;
    private final TCPToken tcpToken;

    public DataToken(PcapForwardEvent event) {
        this.seconds = (int) (event.getTimestamp() / 1_000);
        this.microseconds = (int) (event.getTimestamp() % 1_000);
        this.tcpToken = new TCPToken(event.getInIP(), event.getOutIP(), event.getInPort(), event.getOutPort(), event.getBytes());
        this.capturedPacketLength = this.tcpToken.size();
        this.originalPacketLength = this.tcpToken.size();
    }

    @Override
    public byte[] toBytes() {
        List<byte[]> result = new ArrayList<>();
        result.add(ConvertUtils.toBytes(seconds, 4));
        result.add(ConvertUtils.toBytes(microseconds, 4));
        result.add(ConvertUtils.toBytes(capturedPacketLength, 4));
        result.add(ConvertUtils.toBytes(originalPacketLength, 4));
        result.add(tcpToken.toBytes());
        return ConvertUtils.join(result);
    }
}
