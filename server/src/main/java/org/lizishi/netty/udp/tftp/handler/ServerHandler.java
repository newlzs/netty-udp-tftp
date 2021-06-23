package org.lizishi.netty.udp.tftp.handler;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.DatagramPacket;
import lombok.extern.slf4j.Slf4j;
import org.lizishi.netty.udp.tftp.common.utils.PacketUtils;
import org.lizishi.netty.udp.tftp.packet.BasePacket;
import org.lizishi.netty.udp.tftp.service.opService.ReadService;

/**
 * @author Lzs
 * @date 2021/6/21 15:50
 * @description
 */
@Slf4j
public class ServerHandler extends SimpleChannelInboundHandler<DatagramPacket> {

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, DatagramPacket datagramPacket) throws Exception {
        log.info("ServerHandler.channelRead0-> sender:{}", datagramPacket.sender());
        ByteBuf buf = datagramPacket.content();
        BasePacket basePacket = PacketUtils.create(buf);
        basePacket.setRemoteAddress(datagramPacket.sender());
        switch (basePacket.getPacketType()) {
            case RRQ: // 单独的读通道
                ReadService.create(basePacket);
                break;
//            case WRQ: // 单独的写通道
//                createWriteChannel(basePacket);
//                break;
            default:
                break;
        }
    }
}