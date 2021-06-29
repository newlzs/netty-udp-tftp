package org.lizishi.netty.udp.tftp;

import cn.hutool.core.util.ObjectUtil;
import org.lizishi.netty.udp.tftp.service.ClientService;

import java.net.InetSocketAddress;
import java.util.Scanner;

/**
 * @author Lzs
 * @date 2021/6/21 15:18
 * @description
 */
public class TFTPClientApplication {
    public static void main(String[] args) {
        InetSocketAddress remoteAddress = new InetSocketAddress("127.0.0.1", 69);
        ClientService.rootPath = "./testFile/clientFile/";

        ClientService clientService = new ClientService(remoteAddress);

        clientService.startClient();

        try {
            Scanner input = new Scanner(System.in);
            String order, fileName;
            while(true) {
                if(ObjectUtil.isNotNull(clientService.fileName)) {
                    Thread.sleep(100);
                    continue;
                }

                System.out.println("请输入操作类型：read/write/exit");
                order = input.next();
                if(order.equals("read")) {
                    System.out.println("请输入文件名：");
                    fileName = input.next();
                    clientService.readFile(fileName);
                }else if(order.equals("write")) {
                    System.out.println("请输入文件名：");
                    fileName = input.next();
                    clientService.writeFile(fileName);
                }else if(order.equals("exit")) {
                    break;
                }
            }
            clientService.channel.close();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}