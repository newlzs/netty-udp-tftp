package org.lizishi.netty.udp.tftp.common.coder;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import org.lizishi.netty.udp.tftp.common.coder.manager.Coder;
import org.lizishi.netty.udp.tftp.packet.BasePacket;

/**
 * @author Lzs
 * @date 2021/6/21 15:37
 * @description
 */
public class Encoder extends MessageToByteEncoder<BasePacket> {

    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, BasePacket packet, ByteBuf byteBuf) throws Exception {
        Coder coder = CoderHandler.getCoder(packet.getOpcode());

        byteBuf.writeBytes(coder.encoder(packet));
    }
}