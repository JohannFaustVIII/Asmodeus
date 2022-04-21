package org.faust.file.token;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TCPToken implements Token {

    private static final Map<String, Integer> counters = new HashMap<>();

    private final byte[] destination;
    private final byte[] source;
    private final int type = 0x0008;

    private final byte version = 0x40;
    private final byte headerLength = 0x05;
    private final byte tos = 0x00;
    private final short totalLength;
    private final short identification = 0x00;
    private final short flags = 0x0000;
    private final short fragmentOffset = 0x0000;
    private final byte ttl = 0x40;
    private final byte protocol = 0x06;
    private final short headerChecksum = 0x00;
    private final byte[] sourceAddress;
    private final byte[] destinationAddress;

    private final short srcPort = 0x0400;
    private final short dstPort = 0x0300;
    private final int sequenceNumber;
    private final int ackNumber = 0x00;
    private final short headLength = 0x0050;
    private final short reserved = 0x0000;
    private final short codeBits = 0x0000;
    private final short windowSize = 0x0020;
    private final short checksum = 0x0000;
    private final short urgent = 0x0000;

    private final byte[] data;



    public TCPToken(String inputIP, String outputIP, byte[] data) {
        this.destination = new byte[6];
        this.source = new byte[6];
        this.totalLength = (short) (40 + data.length);
        this.sourceAddress = getBytesFromIP(inputIP);
        this.destinationAddress = getBytesFromIP(outputIP);
        this.data = data;

        this.sequenceNumber = getNextCounter(inputIP, outputIP, data.length);
    }

    private byte[] getBytesFromIP(String outputIP) {
        InetAddress ip = null;
        try {
            ip = InetAddress.getByName(outputIP);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        return ip.getAddress();
    }

    private int getNextCounter(String inputIP, String outputIP, int length) {
        String key = inputIP + "-" + outputIP;
        counters.putIfAbsent(key, 0);
        int value = counters.get(key);
        counters.put(key, value + length);
        return value;
    }

    @Override
    public byte[] toBytes() {
        List<byte[]> result = new ArrayList<>();

        result.add(destination);
        result.add(source);
        result.add(ConvertUtils.toBytes(type, 2));

        result.add(ConvertUtils.toBytes(version + headerLength, 1));
        result.add(ConvertUtils.toBytes(tos, 1));
        result.add(ConvertUtils.reverse(ConvertUtils.toBytes(totalLength, 2)));
        result.add(ConvertUtils.toBytes(identification, 2));
        result.add(ConvertUtils.toBytes(flags + fragmentOffset, 2));
        result.add(ConvertUtils.toBytes(ttl, 1));
        result.add(ConvertUtils.toBytes(protocol, 1));
        result.add(ConvertUtils.toBytes(headerChecksum, 2));
        result.add(sourceAddress);
        result.add(destinationAddress);

        result.add(ConvertUtils.reverse(ConvertUtils.toBytes(srcPort, 2)));
        result.add(ConvertUtils.reverse(ConvertUtils.toBytes(dstPort, 2)));
        result.add(ConvertUtils.reverse(ConvertUtils.toBytes(sequenceNumber, 4)));
        result.add(ConvertUtils.toBytes(ackNumber, 4));
        result.add(ConvertUtils.toBytes(headLength + reserved + codeBits, 2));
        result.add(ConvertUtils.toBytes(windowSize, 2));
        result.add(ConvertUtils.toBytes(checksum, 2));
        result.add(ConvertUtils.toBytes(urgent, 2));

        result.add(data);

        return ConvertUtils.join(result);
    }

    public int size() {
        return 6 + 6 + 2 + 1 + 1 + 2 + 2 + 2 + 1 + 1 + 2 + 4 + 4 + 2 + 2 + 4 + 4 + 2 + 2 + 2 + 2 + data.length;
    }
}
