package org.lizishi.netty.udp.tftp;

import org.lizishi.netty.udp.tftp.service.TFTPservice;

/**
 * @author Lzs
 * @date 2021/6/21 15:23
 * @description
 */
public class TFTPServerApplication {

    public static void main(String[] args) {
        TFTPservice.rootPath = "G:/桌面/";
        TFTPservice service = new TFTPservice();

        service.run();
    }
}