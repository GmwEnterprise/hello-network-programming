package com.example.nio.common;

import java.nio.ByteBuffer;

public class Pack {

    private final byte[] content;
    private final int len;
    private static final int MAX_BUFF_SIZE = 1024;

    public Pack(String content) {
        this.content = content.getBytes();
        this.len = this.content.length;
    }

    public ByteBuffer[] getPack() {
        int n = len % MAX_BUFF_SIZE;
        ByteBuffer[] res = new ByteBuffer[n + 1];
        ByteBuffer head = writeHead();
        head.flip();
        res[0] = head;
        int i = 0, a = 1;
        ByteBuffer item;
        while (a < n) {
            int writeLen = Math.min(len - i, MAX_BUFF_SIZE);
            item = ByteBuffer.allocate(writeLen);
            item.put(content, i, writeLen);
            item.flip();
            res[a++] = item;
            i += writeLen;
        }
        return res;
    }

    private ByteBuffer writeHead() {
        ByteBuffer head = ByteBuffer.allocate(5);
        head.put(((byte) 0));
        head.put(intToByteArray(len));
        return head;
    }

    public static byte[] intToByteArray(int num) {
        return new byte[]{
                (byte) (num >> 24),
                (byte) ((num >> 16) & 0xFF),
                (byte) ((num >> 8) & 0xFF),
                (byte) (num & 0xFF),
                };
    }

    public static int calculateLen(byte[] header) {
        byte[] bytes = new byte[4];
        System.arraycopy(header, 1, bytes, 0, 4);
        return byteArrayToInt(bytes);
    }

    public static int byteArrayToInt(byte[] array) {
        return (array[0] << 24) | (array[1] << 16) | (array[2] << 8) | array[3];
    }

    public static void main(String[] args) {
        System.out.println(byteArrayToInt(intToByteArray(1288888)));
    }
}
