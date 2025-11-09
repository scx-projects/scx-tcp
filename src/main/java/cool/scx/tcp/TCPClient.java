package cool.scx.tcp;

import java.io.IOException;
import java.net.Socket;
import java.net.SocketAddress;

/// TCPClient
///
/// @author scx567888
/// @version 0.0.1
public final class TCPClient implements ScxTCPClient {

    @Override
    public ScxTCPSocket connect(SocketAddress endpoint, int timeout) throws IOException {
        var socket = new Socket();
        socket.connect(endpoint, timeout);
        return new TCPSocket(socket);
    }

}
