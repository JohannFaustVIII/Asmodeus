package org.faust.stats;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class StatsServiceTest {

    @ParameterizedTest(name = "showStats: {0}")
    @MethodSource("showStatsCases")
    void showStats(List<ForwardingStats> statsList) throws IOException {
        //given
        StatsService service = new StatsService();
        ByteArrayOutputStream printResult = redirectSystemOut();
        for (ForwardingStats stats : statsList) {
            service.add(stats);
        }

        //when
        service.show();

        //then
        String[] resultLines = getSystemOutResultLines(printResult);

        Assertions.assertEquals("Last reads: " + statsList.size(), resultLines[0]);
        assertDataLinesContainCorrectData(statsList, resultLines);
    }

    @ParameterizedTest(name = "showStats: {0}")
    @MethodSource("showStatsCases")
    void showEmptyStatsAfterSecondShow(List<ForwardingStats> statsList) throws IOException {
        //given
        StatsService service = new StatsService();
        ByteArrayOutputStream printResult = redirectSystemOut();
        for (ForwardingStats stats : statsList) {
            service.add(stats);
        }
        service.show();
        cleanSystemOut(printResult);

        //when
        service.show();

        //then
        String[] resultLines = getSystemOutResultLines(printResult);

        Assertions.assertEquals("Last reads: 0", resultLines[0]);
        Assertions.assertEquals(1, resultLines.length);
    }

    private void assertDataLinesContainCorrectData(List<ForwardingStats> statsList, String[] resultLines) {
        for (int i = 0; i < statsList.size(); i++) {
            ForwardingStats stat = statsList.get(i);
            Assertions.assertEquals(stat.getLogMessage(), resultLines[i + 1]);
        }
    }

    private ByteArrayOutputStream redirectSystemOut() {
        ByteArrayOutputStream systemOutStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(systemOutStream));
        return systemOutStream;
    }

    private void cleanSystemOut(ByteArrayOutputStream systemOutStream) {
        systemOutStream.reset();
    }

    private String[] getSystemOutResultLines(ByteArrayOutputStream systemOutStream) throws IOException {
        systemOutStream.flush();
        String result = new String(systemOutStream.toByteArray());
        String[] resultLines = result.split("\n");
        return resultLines;
    }

    private static Stream<Arguments> showStatsCases() {
        return IntStream
                .range(0, 1000)
                .mapToObj(i -> Arguments.of(generateListOfRandomForwardingStats(i)));
    }

    private static List<ForwardingStats> generateListOfRandomForwardingStats(int size) {
        Random rand = new Random();
        return Stream
                .generate(() -> new ForwardingStats(1, rand.nextInt(10000)))
                .limit(size)
                .collect(Collectors.toList());
    }
}