package com.example.nio.myreactor;

import java.io.IOException;

public interface ChannelHandler {

    void handle() throws IOException;
}
