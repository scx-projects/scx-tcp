package dev.scx.tcp;

import java.io.IOException;
import java.net.Socket;
import java.net.SocketAddress;

/// ScxTCPClient
///
/// @author scx567888
/// @version 0.0.1
public interface ScxTCPClient {

    Socket connect(SocketAddress endpoint, int timeout) throws IOException;

    default Socket connect(SocketAddress endpoint) throws IOException {
        return connect(endpoint, 0);
    }

}
