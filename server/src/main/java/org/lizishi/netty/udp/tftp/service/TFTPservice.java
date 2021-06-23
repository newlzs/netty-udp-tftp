package org.lizishi.netty.udp.tftp.service;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.nio.NioDatagramChannel;
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

    public void run() {
        log.info("StartService.run-> tftp server start ....");
        Bootstrap b = BootstrapSingleFactory.getInstance();
        try{
            b.handler(new ChannelInitializer<NioDatagramChannel>() {
                @Override
                protected void initChannel(NioDatagramChannel channel) throws Exception {
                    ChannelPipeline pipeline = channel.pipeline();
                    pipeline.addLast(new ServerHandler());
                }
            });
            ChannelFuture future = b.bind(port).sync();
            log.info("StartService.run-> tftp server start success.......");

        } catch (InterruptedException e) {
            log.error("StartService.run-> ", e);
        }
    }
}