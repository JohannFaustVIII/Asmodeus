package org.faust.wireshark.token;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
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
        return Stream.generate(() -> createTestingCase()).limit(100);
    }

    private static Arguments createTestingCase() {


        int[] inputIp = IntStream.generate(() -> ThreadLocalRandom.current().nextInt(256)).limit(4).toArray();
        int[] outputIp = IntStream.generate(() -> ThreadLocalRandom.current().nextInt(256)).limit(4).toArray();

        String inputIpString = IntStream.of(inputIp).mapToObj(String::valueOf).collect(Collectors.joining("."));
        String outputIpString = IntStream.of(outputIp).mapToObj(String::valueOf).collect(Collectors.joining("."));

        byte[] inputIpBytes = toByteArray(inputIp);
        byte[] outputIpBytes = toByteArray(outputIp);

        byte[] data = toByteArray(IntStream.generate(() -> ThreadLocalRandom.current().nextInt(256)).limit(ThreadLocalRandom.current().nextInt(101)).toArray());

        int inputPort = ThreadLocalRandom.current().nextInt(65536);
        int outputPort = ThreadLocalRandom.current().nextInt(65536);

        byte[] begin = new byte[] {
                0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
                0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
                0x08, 0x00,
                0x45,
                0x00,
                0x00, (byte) (40 + data.length),
                0x00, 0x00,
                0x00, 0x00,
                0x40,
                0x06,
                0x00, 0x00,
                inputIpBytes[0], inputIpBytes[1], inputIpBytes[2], inputIpBytes[3],
                outputIpBytes[0], outputIpBytes[1], outputIpBytes[2], outputIpBytes[3],
                (byte)((inputPort >> 8) & 0xff), (byte)inputPort,
                (byte)((outputPort >> 8) & 0xff), (byte)outputPort,
                0x00, 0x00, 0x00, 0x00,
                0x00, 0x00, 0x00, 0x00,
                0x50, 0x00,
                0x20, 0x00,
                0x00, 0x00,
                0x00, 0x00,
        };

        byte[] result = new byte[begin.length + data.length];
        System.arraycopy(begin, 0, result, 0, begin.length);
        System.arraycopy(data, 0, result, begin.length, data.length);

        return Arguments.of(inputIpString, outputIpString, inputPort, outputPort, data, result);
    }


    private static byte[] toByteArray(int[] input) {
        byte[] result = new byte[input.length];
        for (int i = 0; i != input.length; i++) {
            result[i] = (byte) (input[i] & 0xff);
        }
        return result;
    }
}