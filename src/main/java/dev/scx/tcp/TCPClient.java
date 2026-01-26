package dev.scx.tcp;

import java.io.IOException;
import java.net.Socket;
import java.net.SocketAddress;

/// TCPClient
///
/// @author scx567888
/// @version 0.0.1
public final class TCPClient implements ScxTCPClient {

    @Override
    public Socket connect(SocketAddress endpoint, int timeout) throws IOException {
        var tcpSocket = new Socket();
        tcpSocket.connect(endpoint, timeout);
        return tcpSocket;
    }

}
