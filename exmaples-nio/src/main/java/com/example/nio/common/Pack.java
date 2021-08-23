package com.example.nio.common;

import java.nio.ByteBuffer;

public class Pack {

    private final byte[] content;
    private final int len;
    private static final int MAX_BUFF_SIZE = 4;

    public Pack(String content) {
        this.content = content.getBytes();
        this.len = this.content.length;
    }

    public Pack(byte[] content) {
        this.content = content;
        this.len = this.content.length;
    }

    public String getContent() {
        return new String(content);
    }

    public ByteBuffer[] packageToByteBufferArray() {
        int n = len / MAX_BUFF_SIZE + 1;
        ByteBuffer[] res = new ByteBuffer[n + 1];
        ByteBuffer head = writeHead();
        head.flip();
        res[0] = head;
        int i = 0, a = 1;
        ByteBuffer item;
        while (a < res.length) {
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
        // 头部占用5个字节，第一位默认为0，后四位为包体长度
        ByteBuffer head = ByteBuffer.allocate(5);
        head.put(((byte) 0));
        head.put(Bytes.intToByteArray(len));
        return head;
    }
}
