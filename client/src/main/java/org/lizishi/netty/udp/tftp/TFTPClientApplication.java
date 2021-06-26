package org.lizishi.netty.udp.tftp;

import org.lizishi.netty.udp.tftp.service.ClientService;

import java.net.InetSocketAddress;

/**
 * @author Lzs
 * @date 2021/6/21 15:18
 * @description
 */
public class TFTPClientApplication {
    public static void main(String[] args) {
        InetSocketAddress remoteAddress = new InetSocketAddress("127.0.0.1", 69);
        ClientService.rootPath = "G:/桌面/file/";

        ClientService clientService = new ClientService(remoteAddress);

        clientService.startClient();

        try {
            clientService.readFile("a.txt");
//            clientService.writeFile("a.txt");
            clientService.channel.closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}