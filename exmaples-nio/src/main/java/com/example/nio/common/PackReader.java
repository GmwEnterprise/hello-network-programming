package com.example.nio.common;

import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;

public class PackReader {

    private final byte[] header = new byte[5];
    private final ByteArrayOutputStream content = new ByteArrayOutputStream();

    private int pos = 0, contentLen;

    public void read(ByteBuffer buf) {
        if (pos <= 5) {
            while (pos <= 5 && buf.hasRemaining()) {
                header[pos] = buf.get();
                if (pos == 5) {
                    contentLen = Pack.calculateLen(header);
                }
                pos++;
            }
        }

        if (pos > 5) {
            content.write(buf.array(), buf.position(), buf.limit() - buf.position());
            buf.position(buf.limit());
        }

        // if ()
    }
}
