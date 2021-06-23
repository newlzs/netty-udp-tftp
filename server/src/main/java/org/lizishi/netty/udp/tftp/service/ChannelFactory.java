package org.lizishi.netty.udp.tftp.service;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioDatagramChannel;
import lombok.extern.slf4j.Slf4j;
import org.lizishi.netty.udp.tftp.common.utils.ChannelUtils;

/**
 * @author Lzs
 * @date 2021/6/22 11:33
 * @description
 */
@Slf4j
public class ChannelFactory {
    /**
     * 添加volatile关键字，保证在读操作前，写操作必须全部完成
     */
    private static volatile EventLoopGroup workGroup;
    private static volatile Bootstrap bootstrap;

    private ChannelFactory(){}

    public static synchronized Channel getNewChannel(SimpleChannelInboundHandler handler) {
        return ChannelFactory.getNewChannel(handler, -1);
    }

    public static synchronized Channel getNewChannel(SimpleChannelInboundHandler handler, int port) {
        if(bootstrap == null) {
            workGroup = new NioEventLoopGroup();
            bootstrap = new Bootstrap();
            bootstrap.group(workGroup);
            bootstrap.channel(NioDatagramChannel.class);
        }

        bootstrap.handler(new ChannelInitializer<NioDatagramChannel>() {
            @Override
            protected void initChannel(NioDatagramChannel channel) throws Exception {
                ChannelPipeline pipeline = channel.pipeline();
                pipeline.addLast(handler);
            }
        });

        if(port <= 0) {
            port = ChannelUtils.getUsefulPortAndBind(bootstrap);
        }else {
            ChannelUtils.bindPort(bootstrap, port);
        }

        log.info("ChannelFactory.getNewChannel-> port:{}", port);

        return ChannelUtils.channelMap.get(port);
    }

}