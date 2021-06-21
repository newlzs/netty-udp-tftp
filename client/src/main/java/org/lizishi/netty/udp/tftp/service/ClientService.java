package org.lizishi.netty.udp.tftp.service;

/**
 * @author Lzs
 * @date 2021/6/21 16:18
 * @description 客户端服务
 */
public interface ClientService {
    void readFile(String path);

    void writeFile(String path);

    void startClient();
}
