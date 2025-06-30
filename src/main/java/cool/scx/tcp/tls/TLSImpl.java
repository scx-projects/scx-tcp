package cool.scx.tcp.tls;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocketFactory;

/// TLSImpl
///
/// @author scx567888
/// @version 0.0.1
final class TLSImpl implements TLS {

    private final SSLContext sslContext;
    private final SSLServerSocketFactory serverSocketFactory;
    private final SSLSocketFactory socketFactory;

    TLSImpl(SSLContext sslContext) {
        this.sslContext = sslContext;
        this.serverSocketFactory = this.sslContext.getServerSocketFactory();
        this.socketFactory = this.sslContext.getSocketFactory();
    }

    @Override
    public SSLContext sslContext() {
        return sslContext;
    }

    @Override
    public SSLServerSocketFactory serverSocketFactory() {
        return serverSocketFactory;
    }

    @Override
    public SSLSocketFactory socketFactory() {
        return socketFactory;
    }

}
