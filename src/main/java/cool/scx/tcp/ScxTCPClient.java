package cool.scx.tcp;

import java.io.IOException;
import java.net.SocketAddress;

/// ScxTCPClient
///
/// @author scx567888
/// @version 0.0.1
public interface ScxTCPClient {

    ScxTCPSocket connect(SocketAddress endpoint, int timeout) throws IOException;

    default ScxTCPSocket connect(SocketAddress endpoint) throws IOException {
        return connect(endpoint, 0);
    }

}
