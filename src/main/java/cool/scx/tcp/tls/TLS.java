package cool.scx.tcp.tls;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocketFactory;
import java.nio.file.Path;

import static cool.scx.tcp.tls.TLSHelper.*;

/// TLS
///
/// @author scx567888
/// @version 0.0.1
public interface TLS {

    static TLS of(SSLContext sslContext) {
        return new TLSImpl(sslContext);
    }

    static TLS of(Path path, String password) {
        return new TLSImpl(createSSLContext(path, password));
    }

    static TLS ofDefault() {
        return new TLSImpl(createDefaultSSLContext());
    }

    static TLS ofTrustAny() {
        return new TLSImpl(createTrustAnySSLContext());
    }

    SSLContext sslContext();

    SSLServerSocketFactory serverSocketFactory();

    SSLSocketFactory socketFactory();

}
