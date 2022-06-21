package org.faust.wireshark.token;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class HeaderTokenTest {

    @Test
    void convertToBytes() {
        //given
        HeaderToken token = new HeaderToken();
        byte[] expected = {
                (byte) 0xd4, (byte) 0xc3, (byte) 0xb2, (byte) 0xa1,
                0x02, 0x00,
                0x04, 0x00,
                0x00, 0x00, 0x00, 0x00,
                0x00, 0x00, 0x00, 0x00,
                (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff,
                0x01, 0x00, 0x00, (byte) 0x90
        };

        //when
        byte[] result = token.toBytes();

        //then
        assertArrayEquals(expected, result);
    }

}