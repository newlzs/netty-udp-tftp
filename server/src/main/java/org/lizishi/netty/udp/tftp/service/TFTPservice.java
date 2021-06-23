package org.lizishi.netty.udp.tftp.service;

import cn.hutool.core.util.ObjectUtil;
import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;
import org.lizishi.netty.udp.tftp.handler.ServerHandler;

/**
 * @author Lzs
 * @date 2021/6/21 15:24
 * @description
 */
@Slf4j
public class TFTPservice {

    public static String rootPath;

    int port = 69;

    public void run()  {
        try {
            log.info("StartService.run-> tftp server start ....");
            Channel channel = ChannelFactory.getNewChannel(new ServerHandler(), port);
            if(ObjectUtil.isNotNull(channel)) {
                log.info("StartService.run-> tftp server start success.......");
            }
            channel.closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}