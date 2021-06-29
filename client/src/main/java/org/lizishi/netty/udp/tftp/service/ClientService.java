package org.lizishi.netty.udp.tftp.service;

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
import org.lizishi.netty.udp.tftp.common.coder.manager.impl.WRQCoder;
import org.lizishi.netty.udp.tftp.common.utils.FileUtils;
import org.lizishi.netty.udp.tftp.enums.ModelType;
import org.lizishi.netty.udp.tftp.handler.ClientHandler;
import org.lizishi.netty.udp.tftp.packet.entry.RRQPacket;
import org.lizishi.netty.udp.tftp.packet.entry.WRQPacket;

import java.net.InetSocketAddress;

/**
 * @author Lzs
 * @date 2021/6/21 16:09
 * @description
 */
@Slf4j
public class ClientService {
    public static String rootPath = "./";

    public volatile String fileName;

    public NioDatagramChannel channel;

    private InetSocketAddress remoteAddress;

    public ClientService(InetSocketAddress inetSocketAddress) {
        this.remoteAddress = inetSocketAddress;
    }

    /**
     * 传输完成，或者出现异常，重置
     */
    public void reset() {
        this.fileName = null;
        this.channel.pipeline().removeLast();
        this.channel.pipeline().addLast(new ClientHandler(ClientService.this));
    }

    public void resetSimple() {
        this.fileName = null;
    }

    public void readFile(String path) {

        RRQPacket reqPacket = new RRQPacket(path, ModelType.octet);

        Coder<RRQPacket> coder = RRQCoder.getCoder();
        ByteBuf buf = coder.encoder(reqPacket);

        DatagramPacket datagramPacket = new DatagramPacket(buf, remoteAddress);

        channel.writeAndFlush(datagramPacket);

        this.fileName = FileUtils.getFileName(path);

    }

    public void writeFile(String fileName) {

        WRQPacket wrqPacket = new WRQPacket(fileName, ModelType.octet);

        Coder<WRQPacket> coder = WRQCoder.getCoder();
        ByteBuf buf = coder.encoder(wrqPacket);

        DatagramPacket datagramPacket = new DatagramPacket(buf, remoteAddress);

        channel.writeAndFlush(datagramPacket);

        this.fileName = FileUtils.getFileName(fileName);
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