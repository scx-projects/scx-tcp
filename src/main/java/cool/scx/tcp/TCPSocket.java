package cool.scx.tcp;

import cool.scx.tcp.tls.TLS;

import javax.net.ssl.SSLSocket;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

/// TCPSocket
///
/// @author scx567888
/// @version 0.0.1
public final class TCPSocket implements ScxTCPSocket {

    private Socket socket;
    private InputStream in;
    private OutputStream out;
    private ScxTLSManager tlsManager;

    public TCPSocket(Socket socket) throws IOException {
        setSocket(socket);
    }

    @Override
    public InputStream inputStream() {
        return in;
    }

    @Override
    public OutputStream outputStream() {
        return out;
    }

    @Override
    public InetSocketAddress remoteAddress() {
        return (InetSocketAddress) socket.getRemoteSocketAddress();
    }

    @Override
    public InetSocketAddress localAddress() {
        return (InetSocketAddress) socket.getLocalSocketAddress();
    }

    @Override
    public TCPSocket upgradeToTLS(TLS tls) throws IOException {
        //创建 sslSocket (服务器端不需要设置 host 和 port)
        var sslSocket = tls.socketFactory().createSocket(socket, null, -1, true);
        setSocket(sslSocket);
        return this;
    }

    @Override
    public boolean isTLS() {
        return socket instanceof SSLSocket;
    }

    @Override
    public ScxTCPSocket startHandshake() throws IOException {
        if (socket instanceof SSLSocket sslSocket) {
            sslSocket.startHandshake();
        } else {
            throw new IllegalStateException("非 TLS 连接, 无法执行 TLS 握手");
        }
        return this;
    }

    @Override
    public ScxTLSManager tlsManager() {
        return tlsManager;
    }

    @Override
    public boolean isClosed() {
        return socket.isClosed();
    }

    @Override
    public void close() throws IOException {
        socket.close();
    }

    private void setSocket(Socket socket) throws IOException {
        this.socket = socket;
        this.in = socket.getInputStream();
        this.out = socket.getOutputStream();
        if (socket instanceof SSLSocket sslSocket) {
            this.tlsManager = new TLSManager(sslSocket);
        }
    }

}
