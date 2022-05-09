package org.faust.listeners.forwarding;

import org.faust.forwarding.ForwardingStream;
import org.faust.wireshark.WiresharkService;
import org.faust.statistics.StatisticsService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.Random;
import java.util.concurrent.Phaser;
import java.util.stream.Stream;

class ForwardingStreamTest {

    @Mock
    private static StatisticsService mockedStats;
    @Mock
    private static WiresharkService wiresharkService;

    @BeforeAll
    static void setUp() {
        mockedStats = Mockito.mock(StatisticsService.class);
        wiresharkService = Mockito.mock(WiresharkService.class);
    }

    @ParameterizedTest(name = "forwardBytes: {0}")
    @MethodSource("forwardBytesSource")
    void forwardBytes(byte[] inputData) {
        // given
        Phaser phaser = new Phaser();
        InputStream inStream = new ByteArrayInputStream(inputData);
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        ForwardingStream stream = new ForwardingStream.ForwardingStreamBuilder()
                .phaser(phaser)
                .inputStream(inStream)
                .outputStream(outStream)
                .statsService(mockedStats)
                .wsService(wiresharkService)
                .build();

        //when
        stream.run();

        //then
        Assertions.assertArrayEquals(inputData, outStream.toByteArray());
    }

    @Test
    void moveToNextPhaseOnClose() {
        // given
        Phaser phaser = new Phaser();
        InputStream inStream = new ByteArrayInputStream(new byte[0]);
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        ForwardingStream stream = new ForwardingStream.ForwardingStreamBuilder()
                .phaser(phaser)
                .inputStream(inStream)
                .outputStream(outStream)
                .statsService(mockedStats)
                .wsService(wiresharkService)
                .build();

        //when
        stream.run();

        //then
        Assertions.assertEquals(1, phaser.getPhase());
    }

    private static Stream<Arguments> forwardBytesSource() {
        Random rand = new Random();
        return Stream
                .generate(() -> generateArgumentStreamWithArray(rand))
                .limit(1000);
    }

    private static Arguments generateArgumentStreamWithArray(Random rand) {
        int size = rand.nextInt(100);
        byte[] result = new byte[size];
        for (int i = 0; i < size; i++) {
            result[i] = (byte) rand.nextInt(256);
        }
        return Arguments.of(result);
    }

}