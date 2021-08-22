package com.example.nio;

import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

public class FileChannelExamples {

    @Test
    public void testFileChannel() throws Exception {
        // 随机读写
        RandomAccessFile file = new RandomAccessFile("../files/hello.txt", "rw");

        // 获取fileChannel
        FileChannel channel = file.getChannel();

        // 读取
        ByteBuffer buf = ByteBuffer.allocate(512);
        channel.read(buf);

        buf.flip();
        ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
        while (buf.hasRemaining()) {
            byteOut.write(buf.get());
        }
        System.out.println(byteOut);

        // 写入
        // 移动指针到文件尾部
        file.seek(file.length());
        buf.clear();
        buf.put("\nNew Content!".getBytes());
        buf.flip();
        channel.write(buf);

        // 关闭file
        file.close();
    }
}
