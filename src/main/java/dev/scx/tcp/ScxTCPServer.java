package dev.scx.tcp;

import dev.scx.function.Function1Void;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;

/// ScxTCPServer
///
/// 这是一个极简, 纯粹的 TCPServer 抽象, 只负责接收连接并将 [Socket] 所有权完全转移给用户处理,
/// 从本质上看, 它只是 [ServerSocket#accept()] 的一种结构化写法.
///
/// ## 设计定位
///
/// - ScxTCPServer 只负责:
///     - 1, 绑定端口.
///     - 2, 阻塞等待 TCP 连接.
///     - 3, 在连接建立后, 将 [Socket] 交付给用户提供的处理器.
///
/// - ScxTCPServer **不管理连接状态**
/// - ScxTCPServer **不管理线程状态**
/// - ScxTCPServer **不理解任何协议语义**
/// - ScxTCPServer **不调度, 不限制, 不统计并发**
///
/// 换句话说, ScxTCPServer 的职责边界严格等同于一个 accept 循环, 它不是一个 "服务器框架", 也不是一个 "连接管理器".
///
/// ## 并发模型
///
/// 每一个成功接受的 TCP 连接, 都会在一个新的虚拟线程中处理.
///
/// - 虚拟线程的创建是廉价的.
/// - 虚拟线程不是受管资源.
/// - ScxTCPServer 不跟踪, 也不感知虚拟线程的数量.
///
/// 虚拟线程在此仅作为一种执行语义存在, 而非 ScxTCPServer 所拥有或管理的资源.
///
/// ## 连接处理器 (connectHandler)
///
/// 用户必须提供一个 连接处理器, 以供 ScxTCPServer会在 收到新 TCP 连接时调用.
///
/// ScxTCPServer 不会, 也不应该:
/// - 中断 处理器 的执行.
/// - 管理 Socket 的生命周期 (即使在 处理器 发生异常时).
/// - 对 处理器 施加超时, 并发或协议层面的约束.
///
/// 当然这也意味着用户必须在 connectHandler 中妥善处理 Socket, 以防止资源泄露.
///
/// ## 关闭语义 (stop)
///
/// [ScxTCPServer#stop()] 的语义非常明确:
///
/// - 停止接受新的 TCP 连接.
/// - 不干涉已建立连接的处理过程.
///
/// 已经交付给处理器的连接, 将自然运行至完成.
/// ScxTCPServer 不实现所谓的 "优雅关闭" 或 "强制关闭" , 因为这些语义只能在协议层或业务层定义.
///
/// ## 关于刻意未实现的功能 (以下功能被有意排除在 ScxTCPServer 之外)
///
/// - maxConcurrentConnections 之类的任何所谓的安全配置, 原因如下:
///     - 1, ScxTCPServer 能干涉的连接 永远都是三次握手成功的连接, 也就是说无法防止半连接攻击.
///     - 2, 虚拟线程的创建成本是很低的, 起码远高于服务器能承受的 socket 的数量. 所以无需做线程相关的限制.
///     - 3, 真正有意义的 TCP 防护 (如 DDoS 等) 只能在更下层, 如 操作系统配置, 硬件防火墙 或 云服务商安全服务.
///     - 4, 协议层面的防护 (如 Slowloris 等) 只能在更上层(连接处理器), 因为 ScxTCPServer 不能也不该干涉 协议层面.
/// - 连接或线程数量统计.
/// - 连接超时管理.
/// - 负载、限流、QoS 控制.
/// - 协议级的防护或调度.
///
/// 这些能力要么属于操作系统层面, 要么属于协议层或应用层, 均不应由一个 TCP 接受器承担.
/// 综上所述, 经过考量, 此处 ScxTCPServer 只抽象最纯粹的核心功能.
///
/// ## 为什么没有 onError 回调 ?
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
///    仅需将其显式移交给该线程的 UncaughtExceptionHandler 即可, 注意我们不会替用户 关闭 对应的 Socket.
///
/// 之所以没有 onError 却又允许用户在 onConnect 回调中直接抛出异常,
/// 是为了保留最原始的异常信息, 避免用户被迫将受检异常包装成 RuntimeException, 从而减少不必要的封装层级.
///
/// 实际上, 绝大多数情况下, 引入 onError 回调反而会增加理解成本, 并可能误导用户做无效处理.
///
/// 若需要自定义异常处理 (例如告警, 统计等), 可在 onConnect 内自行 try/catch 并实现相应逻辑.
///
/// ## 设计原则总结
///
/// - 只做 ScxTCPServer 这一层 "应该做的事".
/// - 不持有多余状态.
/// - 不越权干涉协议或业务语义.
/// - 保持语义, 边界与行为的自洽.
///
/// 如果你在这里发现 "可以很方便地加一个功能", 那它大概率不属于这一层.
///
/// @author scx567888
/// @version 0.0.1
public interface ScxTCPServer {

    ScxTCPServer onConnect(Function1Void<Socket, ?> connectHandler);

    void start(SocketAddress localAddress) throws IOException;

    void stop();

    InetSocketAddress localAddress();

    default void start(int port) throws IOException {
        start(new InetSocketAddress(port));
    }

}
