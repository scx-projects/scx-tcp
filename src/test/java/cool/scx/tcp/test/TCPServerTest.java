package cool.scx.tcp.test;

import cool.scx.tcp.TCPServer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class TCPServerTest {

    public static void main(String[] args) throws IOException {
        test1();
    }

    public static void test1() throws IOException {
        var tcpServer = new TCPServer();

        tcpServer.onConnect(c -> {
            System.out.println("客户端连接了 !!!");

            var dataReader = new BufferedReader(new InputStreamReader(c.inputStream()));
            while (true) {
                try {
                    var s = dataReader.readLine();
                    if (s == null) {
                        break;
                    }
                    System.out.println(c.remoteAddress() + " : " + s);
                } catch (Throwable e) {
                    break;
                }
            }
            System.err.println("完成");
        });

        tcpServer.start(8899);

        System.out.println("已监听端口号 : " + tcpServer.localAddress().getPort());

    }

}
