package cool.scx.tcp.test;

import cool.scx.tcp.TCPClient;

import java.io.IOException;
import java.net.InetSocketAddress;

public class TCPClientTest {

    public static void main(String[] args) throws InterruptedException, IOException {
        test1();
    }

    public static void test1() throws IOException {
        //先启动服务器
        TCPServerTest.test1();

        for (int j = 0; j < 10; j = j + 1) {
            Thread.ofVirtual().start(() -> {
                var tcpClient = new TCPClient();
                try (var tcpSocket = tcpClient.connect(new InetSocketAddress(8899))) {
                    var out = tcpSocket.outputStream();
                    var i = 0;
                    while (i < 10000) {
                        out.write(((i = i + 1) + "\r\n" + (i = i + 1) + "\r\n").getBytes());
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        }

    }

}
