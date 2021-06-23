package org.lizishi.netty.udp.tftp.common.coder.manager.impl;

import cn.hutool.core.util.ObjectUtil;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import org.lizishi.netty.udp.tftp.common.coder.manager.Coder;
import org.lizishi.netty.udp.tftp.enums.PacketType;
import org.lizishi.netty.udp.tftp.packet.entry.ACKPacket;

/**
 * @author Lzs
 * @date 2021/6/21 12:56
 * @description ack编解码
 */
public class ACKCoder implements Coder<ACKPacket>{
    private static Coder coder;

    // 单例模式-懒汉
    public static Coder getCoder() {
        if(ObjectUtil.isNull(coder)) {
            synchronized (ACKCoder.class) {
                if(ObjectUtil.isNull(coder)) {
                    coder = new ACKCoder();
                }
            }
        }
        return coder;
    }

    @Override
    public ByteBuf encoder(ACKPacket packet) {
        ByteBuf byteBuf = Unpooled.buffer(4);
        byteBuf.writeBytes(packet.getPacketType().toByteArray());
        byteBuf.writeShort(packet.getBlockNum());
        return byteBuf;
    }

    @Override
    public ACKPacket decoder(ByteBuf buf) {
        ACKPacket ackPacket = new ACKPacket();
        ackPacket.setPacketType(PacketType.getByCode(buf.readUnsignedShort()));
        ackPacket.setBlockNum(buf.readUnsignedShort());
        return ackPacket;
    }
}