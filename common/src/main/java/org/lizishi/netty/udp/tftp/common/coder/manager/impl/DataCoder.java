package org.lizishi.netty.udp.tftp.common.coder.manager.impl;

import cn.hutool.core.util.ObjectUtil;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import org.lizishi.netty.udp.tftp.common.coder.manager.Coder;
import org.lizishi.netty.udp.tftp.enums.PacketType;
import org.lizishi.netty.udp.tftp.packet.entry.DataPacket;

/**
 * @author Lzs
 * @date 2021/6/21 12:55
 * @description
 */
public class DataCoder implements Coder<DataPacket> {
    private static Coder coder;

    // 单例模式-懒汉
    public static Coder getCoder() {
        if(ObjectUtil.isNull(coder)) {
            synchronized (DataCoder.class) {
                if(ObjectUtil.isNull(coder)) {
                    coder = new DataCoder();
                }
            }
        }
        return coder;
    }

    @Override
    public ByteBuf encoder(DataPacket packet) {
        ByteBuf buf = Unpooled.buffer(2+2+packet.getData().length);
        buf.writeBytes(packet.getPacketType().toByteArray());
        buf.writeShort(packet.getBlockNum());
        if(packet.getData().length > 0) {
            buf.writeBytes(packet.getData());
        }
        return buf;
    }

    @Override
    public DataPacket decoder(ByteBuf buf) {
        DataPacket packet = new DataPacket();
        packet.setPacketType(PacketType.getByCode(buf.readUnsignedShort()));
        packet.setBlockNum(buf.readUnsignedShort());
        byte[] data;
        if (buf.readableBytes() > 0) {
            data = new byte[buf.readableBytes()];
            buf.readBytes(data);
        } else {
             data = new byte[0];
        }
        packet.setData(data);
        return packet;
    }
}