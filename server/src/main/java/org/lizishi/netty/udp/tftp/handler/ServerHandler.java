package org.lizishi.netty.udp.tftp.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.lizishi.netty.udp.tftp.packet.BasePacket;

/**
 * @author Lzs
 * @date 2021/6/21 15:50
 * @description
 */
public class ServerHandler extends SimpleChannelInboundHandler<BasePacket> {

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, BasePacket basePacket) throws Exception {

    }
}