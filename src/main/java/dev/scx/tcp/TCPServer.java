package dev.scx.tcp;

import dev.scx.function.Function1Void;

import java.io.IOException;
import java.lang.System.Logger;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;

import static java.lang.System.Logger.Level.ERROR;
import static java.lang.System.Logger.Level.TRACE;

/// TCPServer
///
/// @author scx567888
/// @version 0.0.1
public final class TCPServer implements ScxTCPServer {

    private static final Logger LOGGER = System.getLogger(TCPServer.class.getName());

    private final TCPServerOptions options;
    private volatile boolean running;
    private Thread listenThread;
    private Function1Void<Socket, ?> connectHandler;
    private ServerSocket serverSocket;

    public TCPServer() {
        this(new TCPServerOptions());
    }

    public TCPServer(TCPServerOptions options) {
        this.options = options;
        this.running = false;
    }

    @Override
    public ScxTCPServer onConnect(Function1Void<Socket, ?> connectHandler) {
        this.connectHandler = connectHandler;
        return this;
    }

    @Override
    public void start(SocketAddress localAddress) throws IOException {
        if (running) {
            throw new IllegalStateException("服务器已在运行 !!!");
        }

        if (connectHandler == null) {
            throw new IllegalStateException("未设置 连接处理器 !!!");
        }

        serverSocket = new ServerSocket();
        serverSocket.bind(localAddress, options.backlog());

        running = true;

        listenThread = Thread.ofPlatform()
            .name("TCPServer-Listener-" + serverSocket.getLocalSocketAddress())
            .start(this::listen);
    }

    @Override
    public void stop() {
        if (!running) {
            return;
        }

        running = false;

        try {
            serverSocket.close();
        } catch (IOException e) {
            LOGGER.log(TRACE, "关闭 ServerSocket 时发生错误 !!!", e);
        }

        try {
            listenThread.join();
        } catch (InterruptedException _) {
            // 这里理论上永远都不会发生
        }

        serverSocket = null;
        listenThread = null;

    }

    @Override
    public InetSocketAddress localAddress() {
        if (serverSocket == null) {
            throw new IllegalStateException("服务器没有启动 !!!");
        }
        // 这里强转是安全的.
        return (InetSocketAddress) serverSocket.getLocalSocketAddress();
    }

    public TCPServerOptions options() {
        return options;
    }

    private void listen() {
        while (true) {
            try {
                var tcpSocket = serverSocket.accept();
                // 创建虚拟线程 处理连接
                Thread.ofVirtual()
                    .name("TCPServer-Handler-" + tcpSocket.getRemoteSocketAddress())
                    .start(() -> handle(tcpSocket));
            } catch (IOException e) {
                // 第一种情况 服务器主动关闭 触发的异常, 无需处理, 直接跳出循环即可
                if (!running) {
                    break;
                }
                // 第二种情况 accept 出现异常
                running = false;
                try {
                    serverSocket.close();
                } catch (IOException ex) {
                    e.addSuppressed(ex);
                }
                LOGGER.log(ERROR, "服务器 接受连接 时发生错误 !!!", e);
                break;
            }
        }
    }

    /// 将 connectHandler 中抛出的任何异常显式移交给当前线程的 UncaughtExceptionHandler.
    /// 这是一个刻意的设计选择, 而非疏忽:
    /// - Java 的线程模型 (Runnable) 不允许异常向上传播, 因此本层被迫捕获所有 Throwable.
    /// - 本层拒绝在 TCP 接受器这一层解释, 记录(日志)或吞掉异常, 以避免引入第二条异常处理路径.
    /// - 通过显式调用 UncaughtExceptionHandler, 尽可能保持异常行为与线程自然失败语义一致.
    /// 换言之, 本方法并不 "处理" 异常, 而是尽可能将异常归还给线程模型本身 (虽然本质上是在 Runnable 约束下的模拟行为).
    private void handle(Socket tcpSocket) {
        try {
            connectHandler.apply(tcpSocket);
        } catch (Throwable e) {
            var t = Thread.currentThread();
            t.getUncaughtExceptionHandler().uncaughtException(t, e);
        }
    }

}
