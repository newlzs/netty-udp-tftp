package org.lizishi.netty.udp.tftp.handler;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.DatagramPacket;
import lombok.extern.slf4j.Slf4j;
import org.lizishi.netty.udp.tftp.common.utils.PacketUtils;
import org.lizishi.netty.udp.tftp.packet.BasePacket;
import org.lizishi.netty.udp.tftp.packet.entry.ErrorPacket;
import org.lizishi.netty.udp.tftp.service.ClientService;

/**
 * @author Lzs
 * @date 2021/6/21 16:13
 * @description
 */
@Slf4j
public class ClientHandler extends SimpleChannelInboundHandler<DatagramPacket> {
    private ClientService clientService;

    public ClientHandler(ClientService clientService) {
        this.clientService = clientService;
    }

    @Override
    protected void messageReceived(ChannelHandlerContext ctx, DatagramPacket datagramPacket) throws Exception {
        ByteBuf buf = datagramPacket.content();
        BasePacket packet = PacketUtils.create(buf);
        packet.setRemoteAddress(datagramPacket.sender());
        // 重置
        buf.readerIndex(0);
        switch (packet.getPacketType()) {
            case DATA:
                log.info("ClientHandler.channelRead0-> readFile start.....");
                ctx.pipeline().addLast(new ClientReadHandler(this.clientService));
                ctx.pipeline().remove(this);
                ctx.fireChannelRead(datagramPacket);
                break;
            case ACK:
                log.info("ClientHandler.channelRead0-> writeFile start.....");
                ctx.pipeline().addLast(new ClientWriteHandler(this.clientService));
                ctx.pipeline().remove(this);
                ctx.fireChannelRead(datagramPacket);
                break;
            case ERROR:
                log.error("ClientHandler.messageReceived-> error:{}", (ErrorPacket) packet);
                clientService.resetSimple();
            default:
                break;
        }
    }
}