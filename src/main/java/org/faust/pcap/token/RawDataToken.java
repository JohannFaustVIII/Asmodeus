package org.faust.pcap.token;

import java.util.LinkedList;
import java.util.List;

public class RawDataToken {

    private final int seconds;
    private final int microseconds;
    private final byte[] bytes;

    public RawDataToken(byte[] bytes) {
        this.seconds = ConvertUtils.toInt(ConvertUtils.subArray(bytes, 0, 4));
        this.microseconds = ConvertUtils.toInt(ConvertUtils.subArray(bytes, 4, 4));
        this.bytes = bytes;
    }

    public static List<RawDataToken> getTokens(byte[] bytes) {
        List<RawDataToken> result = new LinkedList<>();

        int offset = 0;
        while (offset < bytes.length) {
            int size = ConvertUtils.toInt(ConvertUtils.subArray(bytes, offset + 8, 4));
            result.add(new RawDataToken(ConvertUtils.subArray(bytes, offset, size + 16)));
            offset += size + 16;
        }
        return result;
    }

    public int getSeconds() {
        return seconds;
    }

    public int getMicroseconds() {
        return microseconds;
    }

    public byte[] getBytes() {
        return bytes;
    }

    public long getTime() {
        return 1_000L*seconds + microseconds;
    }
}
