package com.example.common;

public final class Bytes {

    public static byte[] intToByteArray(int num) {
        // 0 - 4 下标从高位到低位
        return new byte[]{
                (byte) (num >>> 24 & 0xFF),
                (byte) (num >>> 16 & 0xFF),
                (byte) (num >>> 8 & 0xFF),
                (byte) (num & 0xFF),
        };
    }

    public static int byteArrayToInt(byte[] array) {
        int a = array[0] << 24;
        int b = array[1] << 16 & 0xFFFFFF;
        int c = array[2] << 8 & 0xFFFF;
        int d = array[3] & 0xFF;
        return a | b | c | d;
    }
}
