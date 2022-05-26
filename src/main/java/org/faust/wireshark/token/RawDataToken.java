package org.faust.wireshark.token;

import java.util.LinkedList;
import java.util.List;

public class RawDataToken {

    private final int seconds;
    private final int microseconds;
    private final byte[] bytes;

    public RawDataToken(byte[] bytes) {
        this.seconds = ConvertUtils.toInt(ConvertUtils.subArray(bytes, 0, 4));
        this.microseconds = ConvertUtils.toInt(ConvertUtils.subArray(bytes, 4, 8));
        this.bytes = bytes;
    }

    public static List<RawDataToken> getTokens(byte[] bytes) {
        List<RawDataToken> result = new LinkedList<>();

        int offset = 0;
        while (offset < bytes.length) {
            int size = ConvertUtils.toInt(ConvertUtils.subArray(bytes, offset + 8, offset + 12));
            result.add(new RawDataToken(ConvertUtils.subArray(bytes, offset, offset + size + 16)));
            offset += size;
        }

        return result;
    }

}
