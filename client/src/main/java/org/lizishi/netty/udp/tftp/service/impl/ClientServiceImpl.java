package org.lizishi.netty.udp.tftp.service.impl;

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
import org.lizishi.netty.udp.tftp.enums.ModelType;
import org.lizishi.netty.udp.tftp.enums.PacketType;
import org.lizishi.netty.udp.tftp.handler.ClientHandler;
import org.lizishi.netty.udp.tftp.packet.entry.REQPacket;
import org.lizishi.netty.udp.tftp.service.ClientService;

/**
 * @author Lzs
 * @date 2021/6/21 16:09
 * @description
 */
@Slf4j
public class ClientServiceImpl implements ClientService {
    NioDatagramChannel channel;

    @Override
    public void readFile(String path) {
        REQPacket reqPacket = new REQPacket();
        reqPacket.setOpcode(PacketType.REQ.getCode());
        reqPacket.setFileName(path);
        reqPacket.setModel(ModelType.octet.getCode());
        channel.writeAndFlush(reqPacket);
    }

    @Override
    public void writeFile(String path) {
        return ;
    }

    @Override
    public void startClient() {
        EventLoopGroup workGroup = new NioEventLoopGroup();

        try {
            Bootstrap b = new io.netty.bootstrap.Bootstrap();

            b.group(workGroup)
                    .channel(NioDatagramChannel.class)
                    .handler(new ChannelInitializer<NioDatagramChannel>() {
                        @Override
                        protected void initChannel(NioDatagramChannel channel) throws Exception {
                            ChannelPipeline pipeline = channel.pipeline();
                            pipeline.addLast(new Decoder())
                                    .addLast(new ClientHandler())
                                    .addLast(new Encoder());
                        }
                    });

            ChannelFuture future = b.bind().sync();
            channel = (NioDatagramChannel) future.channel();
            log.info("StartService.run-> tftp server start success.......");

            channel.closeFuture().sync();
        } catch (InterruptedException e) {
            workGroup.shutdownGracefully();
            log.error("ClientService.run-> ", e);
        }

    }
}