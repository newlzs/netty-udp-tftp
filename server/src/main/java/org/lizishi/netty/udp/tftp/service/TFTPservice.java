package org.lizishi.netty.udp.tftp.service;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioDatagramChannel;
import lombok.extern.slf4j.Slf4j;
import org.lizishi.netty.udp.tftp.common.coder.Decoder;
import org.lizishi.netty.udp.tftp.common.coder.Encoder;
import org.lizishi.netty.udp.tftp.handler.ServerHandler;

/**
 * @author Lzs
 * @date 2021/6/21 15:24
 * @description
 */
@Slf4j
public class TFTPservice {
    int port = 69;

    public void run() {
        log.info("StartService.run-> tftp server start ....");
        EventLoopGroup workGroup = new NioEventLoopGroup();

        try{
            Bootstrap b = new Bootstrap();

            b.group(workGroup)
                    .channel(NioDatagramChannel.class)
                    .handler(new ChannelInitializer<NioDatagramChannel>() {
                        @Override
                        protected void initChannel(NioDatagramChannel channel) throws Exception {
                            ChannelPipeline pipeline = channel.pipeline();
                            pipeline.addLast(new Decoder())
                                    .addLast(new ServerHandler())
                                    .addLast(new Encoder());
                        }
                    });

            ChannelFuture future = b.bind(port).sync();
            log.info("StartService.run-> tftp server start success.......");

            future.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            workGroup.shutdownGracefully();
            log.error("StartService.run-> ", e);
        }
    }
}