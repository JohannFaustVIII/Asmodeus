package org.faust.pcap.token;

import java.util.List;

public class ConvertUtils {

    private ConvertUtils() {}

    public static byte[] toBytes(int value, int size) {
        byte[] result = new byte[size];
        for (int i = 0; i < size; i++) {
            result[i] = (byte) value;
            value >>= 8;
        }
        return result;
    }

    public static byte[] toBytes(short value, int size) {
        byte[] result = new byte[size];
        for (int i = 0; i < size; i++) {
            result[i] = (byte) value;
            value >>= 8;
        }
        return result;
    }

    public static byte[] join(List<byte[]> bytesList) {
        int size = bytesList.stream().mapToInt(bytes -> bytes.length).sum();
        byte[] result = new byte[size];
        int index = 0;
        for (int i = 0; i < bytesList.size(); i++) {
            byte[] bytes = bytesList.get(i);
            System.arraycopy(bytes, 0, result, index, bytes.length);
            index += bytes.length;
        }
        return result;
    }

    public static byte[] reverse(byte[] input) {
        byte[] result = new byte[input.length];

        for (int i = 0; i != input.length; i++) {
            result[i] = input[input.length - i - 1];
        }

        return result;
    }

    public static int toInt(byte[] input) {
        if (input.length != 4) {
            throw new ConvertUtilsException("Input array length is different than 4");
        }

        int value = 0;
        for (int i = 0; i < 4; i++) {
            int b = input[i] & 0xff;
            value += b << (i*8);
        }
        return value;
    }

    public static byte[] subArray(byte[] src, int start, int size) {
        byte[] result = new byte[size];
        System.arraycopy(src, start, result, 0, size);
        return result;
    }

    private static class ConvertUtilsException extends RuntimeException {
        public ConvertUtilsException(String message) {
            super(message);
        }
    }
}
