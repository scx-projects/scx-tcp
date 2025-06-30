package cool.scx.tcp;

import cool.scx.functional.ScxConsumer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;

/// ScxTCPServer
///
/// 为什么没有 onError 回调 ?
///
/// ScxTCPServer 旨在提供最小化, 底层的 Socket 连接抽象, 只负责接收连接并交由用户处理.
///
/// 在 TCP 服务器层面, 异常大致可分为两类:
///
/// 1. **系统异常 (如 accept 异常)**
///     通常是由于系统资源耗尽, 监听端口被强制关闭等不可恢复的错误导致,
///     由于没有可操作的上下文, 用户无需干预也无法干预,
///     仅需内部记录日志并关闭 ServerSocket 即可, 无需额外暴露 onError 回调.
///
/// 2. **用户处理器异常 (如用户在 onConnect 回调中抛出异常)**
///    用户除了当前 Socket 外, 通常没有更多上下文信息, 即使提供 onError 回调, 能做的也非常有限,
///    仅需内部记录日志并关闭 对应 Socket 即可.
///
///    之所以没有 onError 却又允许用户在 onConnect 回调中直接抛出异常,
///    是为了保留最原始的异常信息, 避免用户被迫将受检异常包装成 RuntimeException, 从而减少不必要的封装层级.
///
/// 实际上, 绝大多数情况下, 仅记录日志即可满足需求, 引入 onError 回调反而会增加理解成本，并可能误导用户做无效处理.
///
/// 若需要自定义异常处理 (例如告警, 统计等), 可在 onConnect 内自行 try/catch 并实现相应逻辑.
///
/// @author scx567888
/// @version 0.0.1
public interface ScxTCPServer {

    ScxTCPServer onConnect(ScxConsumer<ScxTCPSocket, ?> connectHandler);

    void start(SocketAddress localAddress) throws IOException;

    void stop();

    InetSocketAddress localAddress();

    default void start(int port) throws IOException {
        start(new InetSocketAddress(port));
    }

}
