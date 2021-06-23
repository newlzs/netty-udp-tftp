package org.lizishi.netty.udp.tftp.common.utils;


import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Lzs
 * @date 2021/6/22 11:03
 * @description
 */
@Slf4j
public class ChannelUtils {
    public static Map<Integer, Channel> channelMap = new HashMap<>();

    public static void bindPort(Bootstrap b, int port) {
        try{
            Channel channel = b.bind(port).sync().channel();
            channelMap.put(port, channel);
        } catch (InterruptedException e) {
            log.error("ChannelUtils.bindPort-> port:{}",port, e);
        }
    }

    public synchronized static int getUsefulPortAndBind(Bootstrap b) {
        ServerSocket serverSocket = null; //读取空闲的可用端口
        try {
            serverSocket = new ServerSocket(0);
            int port = serverSocket.getLocalPort();
            Channel channel = b.bind(port).sync().channel();
            channelMap.put(port, channel);
            return port;
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        throw new RuntimeException("获取可用端口失败");
    }

}