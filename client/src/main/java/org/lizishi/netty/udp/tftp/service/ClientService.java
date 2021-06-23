package org.lizishi.netty.udp.tftp.service;

import cn.hutool.core.util.ObjectUtil;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.DatagramPacket;
import io.netty.channel.socket.nio.NioDatagramChannel;
import lombok.extern.slf4j.Slf4j;
import org.lizishi.netty.udp.tftp.common.coder.manager.Coder;
import org.lizishi.netty.udp.tftp.common.coder.manager.impl.RRQCoder;
import org.lizishi.netty.udp.tftp.common.utils.FileUtils;
import org.lizishi.netty.udp.tftp.enums.ModelType;
import org.lizishi.netty.udp.tftp.handler.ClientHandler;
import org.lizishi.netty.udp.tftp.packet.entry.RRQPacket;

import java.net.InetSocketAddress;

/**
 * @author Lzs
 * @date 2021/6/21 16:09
 * @description
 */
@Slf4j
public class ClientService {
    public String fileName;

    private NioDatagramChannel channel;
    private InetSocketAddress remoteAddress;

    public ClientService(InetSocketAddress inetSocketAddress) {
        this.remoteAddress = inetSocketAddress;
    }

    public void readFile(String path) {
        if(ObjectUtil.isNotNull(fileName)) {
            log.info("ClientService.readFile-> file is in use, fileName:{}", fileName);
            return ;
        }
        RRQPacket reqPacket = new RRQPacket(path, ModelType.octet);

        Coder<RRQPacket> coder = RRQCoder.getCoder();
        ByteBuf buf = coder.encoder(reqPacket);

        DatagramPacket datagramPacket = new DatagramPacket(buf, remoteAddress);

        channel.writeAndFlush(datagramPacket);

        this.fileName = FileUtils.getFileName(path);
    }

    public void writeFile(String path) {
        return ;
    }

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
                            pipeline.addLast(new ClientHandler(ClientService.this));
                        }
                    });

            ChannelFuture future = b.bind(0).sync();
            channel = (NioDatagramChannel) future.channel();
            log.info("StartService.run-> tftp client start success.......");

        } catch (InterruptedException e) {
            workGroup.shutdownGracefully();
            log.error("ClientService.run-> ", e);
        }

    }
}