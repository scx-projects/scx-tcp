package cool.scx.tcp;

import javax.net.ssl.SNIHostName;
import javax.net.ssl.SNIServerName;
import javax.net.ssl.SSLParameters;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;

/// ScxTLSManager
///
/// @author scx567888
/// @version 0.0.1
public interface ScxTLSManager {

    void setUseClientMode(boolean mode);

    void setHandshakeApplicationProtocolSelector(BiFunction<ScxTLSManager, List<String>, String> selector);

    String getApplicationProtocol();

    SSLParameters getSSLParameters();

    void setSSLParameters(SSLParameters params);

    default void setApplicationProtocols(String... protocols) {
        var sslParameters = getSSLParameters();
        sslParameters.setApplicationProtocols(protocols);
        setSSLParameters(sslParameters);
    }

    default void setServerNames(String... serverNames) {
        var sslParameters = getSSLParameters();
        var list = new ArrayList<SNIServerName>();
        for (var serverName : serverNames) {
            list.add(new SNIHostName(serverName));
        }
        sslParameters.setServerNames(list);
        setSSLParameters(sslParameters);
    }

}
