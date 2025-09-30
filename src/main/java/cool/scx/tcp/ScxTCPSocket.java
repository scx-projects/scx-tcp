package cool.scx.tcp;

import cool.scx.tcp.tls.TLS;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;

/// ScxTCPSocket
///
/// @author scx567888
/// @version 0.0.1
public interface ScxTCPSocket extends Closeable {

    InputStream inputStream();

    OutputStream outputStream();

    InetSocketAddress remoteAddress();

    InetSocketAddress localAddress();

    ScxTCPSocket upgradeToTLS(TLS tls) throws IOException;

    boolean isTLS();

    ScxTCPSocket startHandshake() throws IOException;

    ScxTLSManager tlsManager();

    boolean isClosed();

}
