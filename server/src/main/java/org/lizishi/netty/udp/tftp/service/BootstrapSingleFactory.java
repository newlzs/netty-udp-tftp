package org.lizishi.netty.udp.tftp.service;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioDatagramChannel;

/**
 * @author Lzs
 * @date 2021/6/22 11:33
 * @description
 */
public class BootstrapSingleFactory {
    /**
     * 添加volatile关键字，保证在读操作前，写操作必须全部完成
     */
    private static volatile EventLoopGroup workGroup;
    private static volatile Bootstrap bootstrap;

    private BootstrapSingleFactory(){}

    public static Bootstrap getInstance() {
        if(bootstrap == null) {
            synchronized (BootstrapSingleFactory.class) {
                if(bootstrap == null) {
                    workGroup = new NioEventLoopGroup();
                    bootstrap = new Bootstrap();
                    bootstrap.group(workGroup);
                    bootstrap.channel(NioDatagramChannel.class);
                }
            }
        }
        return bootstrap;
    }
}