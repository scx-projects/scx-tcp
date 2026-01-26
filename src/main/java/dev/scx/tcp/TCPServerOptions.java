package dev.scx.tcp;

/// TCPServerOptions
///
/// 此类承载 TCPServer 构造期的少量 TCP 层配置.
/// 目前唯一有意义的选项是 backlog, 用于指定 ServerSocket 的 listen 队列长度.
/// 不涉及其他功能性配置, 以保持 TCPServer 纯粹性.
///
/// @author scx567888
/// @version 0.0.1
public final class TCPServerOptions {

    private int backlog;

    public TCPServerOptions() {
        this.backlog = 128; // 默认背压大小 128
    }

    public TCPServerOptions(TCPServerOptions oldOptions) {
        backlog(oldOptions.backlog());
    }

    public int backlog() {
        return backlog;
    }

    public TCPServerOptions backlog(int backlog) {
        this.backlog = backlog;
        return this;
    }

}

