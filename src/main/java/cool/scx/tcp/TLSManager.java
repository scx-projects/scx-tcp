package cool.scx.tcp;

import javax.net.ssl.SSLParameters;
import javax.net.ssl.SSLSocket;
import java.util.List;
import java.util.function.BiFunction;

/// TLSManager
///
/// @author scx567888
/// @version 0.0.1
public final class TLSManager implements ScxTLSManager {

    private final SSLSocket sslSocket;

    public TLSManager(SSLSocket sslSocket) {
        this.sslSocket = sslSocket;
    }

    @Override
    public void setUseClientMode(boolean mode) {
        sslSocket.setUseClientMode(mode);
    }

    @Override
    public void setHandshakeApplicationProtocolSelector(BiFunction<ScxTLSManager, List<String>, String> selector) {
        sslSocket.setHandshakeApplicationProtocolSelector((sslEngine, list) -> selector.apply(this, list));
    }

    @Override
    public String getApplicationProtocol() {
        return sslSocket.getApplicationProtocol();
    }

    @Override
    public SSLParameters getSSLParameters() {
        return sslSocket.getSSLParameters();
    }

    @Override
    public void setSSLParameters(SSLParameters params) {
        sslSocket.setSSLParameters(params);
    }

}
