package org.faust.wireshark.token;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class TCPTokenTest {

    @ParameterizedTest
    @MethodSource("convertToBytesCases")
    void convertToBytes(String inputIp, String outputIp, int inputPort, int outputPort, byte[] data, byte[] expectedResult) {
        //given
        TCPToken token = new TCPToken(inputIp, outputIp, inputPort, outputPort, data);

        //when
        byte[] result = token.toBytes();

        //then
        assertArrayEquals(expectedResult, result);
    }

    public static Stream<Arguments> convertToBytesCases() {
        return Stream.of(
                Arguments.of(
                        "127.0.0.1", "1.2.3.4", 20, 50, new byte[] {0x00},
                        new byte[] {
                                0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
                                0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
                                0x08, 0x00,
                                0x45,
                                0x00,
                                0x00, 0x29,
                                0x00, 0x00,
                                0x00, 0x00,
                                0x40,
                                0x06,
                                0x00, 0x00,
                                0x7f, 0x00, 0x00, 0x01,
                                0x01, 0x02, 0x03, 0x04,
                                0x00, 0x14,
                                0x00, 0x32,
                                0x00, 0x00, 0x00, 0x00,
                                0x00, 0x00, 0x00, 0x00,
                                0x50, 0x00,
                                0x20, 0x00,
                                0x00, 0x00,
                                0x00, 0x00,
                                0x00
                        }
                )
        );
    }
}