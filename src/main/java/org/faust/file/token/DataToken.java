package org.faust.file.token;

import org.faust.file.WSForwardEvent;

import java.util.ArrayList;
import java.util.List;

public class DataToken implements Token {

    private final int seconds;
    private final int microseconds;
    private final int capturedPacketLength;
    private final int originalPacketLength;
    private final TCPToken tcpToken;

    public DataToken(WSForwardEvent event) {
        this.seconds = (int) (event.getTimestamp() / 1_000_000);
        this.microseconds = (int) (event.getTimestamp() % 1_000_000);
        this.tcpToken = new TCPToken(event.getInIP(), event.getOutIP(), event.getBytes());
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
