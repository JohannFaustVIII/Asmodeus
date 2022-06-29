package org.faust.pcap.token;

import java.util.ArrayList;
import java.util.List;

public class HeaderToken implements Token {

    private final int magicNumber = 0xA1B2C3D4;
    private final short majorVersion = 2;
    private final short minorVersion = 4;
    private final int reserved1 = 0;
    private final int reserved2 = 0;
    private final int snapLen = 0xffffffff;
    private final int fcs = 0x90000000;
    private final int linkType = 1;


    @Override
    public byte[] toBytes() {
        List<byte[]> result = new ArrayList<>();
        result.add(ConvertUtils.toBytes(magicNumber, 4));
        result.add(ConvertUtils.toBytes(majorVersion, 2));
        result.add(ConvertUtils.toBytes(minorVersion, 2));
        result.add(ConvertUtils.toBytes(reserved1, 4));
        result.add(ConvertUtils.toBytes(reserved2, 4));
        result.add(ConvertUtils.toBytes(snapLen, 4));
        result.add(ConvertUtils.toBytes(fcs + linkType, 4));
        return ConvertUtils.join(result);
    }
}
