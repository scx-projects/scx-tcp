package cool.scx.tcp.tls;

import javax.net.ssl.*;
import java.io.IOException;
import java.nio.file.Path;
import java.security.*;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

/// TLSHelper
///
/// @author scx567888
/// @version 0.0.1
final class TLSHelper {

    public static SSLContext createSSLContext(Path path, String password) {
        try {
            // 1, createKeyStore
            var keyStore = KeyStore.getInstance(path.toFile(), password.toCharArray());
            // 2, createKeyManagerFactory
            var keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
            keyManagerFactory.init(keyStore, password.toCharArray());
            // 3, createTrustManagerFactory
            var trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            trustManagerFactory.init(keyStore);
            // 4, createSSLContext
            var sslContext = SSLContext.getInstance("TLS");
            sslContext.init(keyManagerFactory.getKeyManagers(), trustManagerFactory.getTrustManagers(), null);
            return sslContext;
        } catch (CertificateException | KeyStoreException | IOException | NoSuchAlgorithmException |
                 UnrecoverableKeyException | KeyManagementException e) {
            throw new IllegalArgumentException("Failed to create SSLContext", e);
        }
    }

    /// 获取系统默认证书并返回 TLS 对象（多用于客户端）
    public static SSLContext createDefaultSSLContext() {
        try {
            // 1, createTrustManagerFactory
            var trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            trustManagerFactory.init((KeyStore) null);
            // 2, createSSLContext
            var sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, trustManagerFactory.getTrustManagers(), null);
            return sslContext;
        } catch (KeyManagementException | NoSuchAlgorithmException | KeyStoreException e) {
            throw new IllegalStateException("Failed to create Default SSLContext", e);
        }
    }

    /// 创建自定义 TrustManager, 忽略证书验证（建议仅用于测试环境）
    public static SSLContext createTrustAnySSLContext() {

        var trustAnyManager = new X509TrustManager() {

            public X509Certificate[] getAcceptedIssuers() {
                return new X509Certificate[]{};
            }

            public void checkClientTrusted(X509Certificate[] certs, String authType) {
                // 此处忽略客户端证书验证逻辑
            }

            public void checkServerTrusted(X509Certificate[] certs, String authType) {
                // 此处忽略服务器证书验证逻辑
            }

        };

        try {
            var sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, new TrustManager[]{trustAnyManager}, null);
            return sslContext;
        } catch (NoSuchAlgorithmException | KeyManagementException e) {
            throw new IllegalStateException("Failed to create TrustAny SSLContext", e);
        }
    }

}
