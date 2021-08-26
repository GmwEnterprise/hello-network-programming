package com.example.common;

import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;

public class PackReader {

    private final ByteArrayOutputStream byteStore = new ByteArrayOutputStream();
    private int position = 0;
    private int contentLen = 0;

    private Pack pack = null;
    private boolean finishRead = false;

    public Pack packPrepared() {
        return pack;
    }

    public void readBuffer(ByteBuffer buf) {
        if (finishRead) {
            throw new RuntimeException("pack已就绪，请重新创建一个packReader以继续读取");
        }
        int bufAvailable = buf.limit() - buf.position();
        if (position < 5) {
            int headerNeed = 5 - position;
            int readLen = Math.min(headerNeed, bufAvailable);
            byteStore.write(buf.array(), buf.position(), readLen);
            buf.position(buf.position() + readLen);
            position += readLen;
        }

        if (position >= 5) {
            if (contentLen == 0) {
                byte[] contentLenByteArray = new byte[4];
                byte[] byteArray = byteStore.toByteArray();
                System.arraycopy(byteArray, 1, contentLenByteArray, 0, 4);
                contentLen = Bytes.byteArrayToInt(contentLenByteArray);
            }

            if (buf.hasRemaining()) {
                bufAvailable = buf.limit() - buf.position();
                int contentNeed = 5 + contentLen - position;
                int readLen = Math.min(bufAvailable, contentNeed);
                if (readLen > 0) {
                    byteStore.write(buf.array(), buf.position(), readLen);
                    buf.position(buf.position() + readLen);
                    position += readLen;
                }
            }

            if (contentLen + 5 == position) {
                // 读完一个pack
                byte[] bytes = byteStore.toByteArray();
                byte[] res = new byte[bytes.length - 5];
                System.arraycopy(bytes, 5, res, 0, res.length);
                pack = new Pack(res);
                finishRead = true;
            }
        }
    }
}
