package org.lizishi.netty.udp.tftp.common.coder.manager.impl;

import cn.hutool.core.util.ObjectUtil;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.util.CharsetUtil;
import org.lizishi.netty.udp.tftp.common.coder.manager.Coder;
import org.lizishi.netty.udp.tftp.common.utils.PacketUtils;
import org.lizishi.netty.udp.tftp.enums.PacketType;
import org.lizishi.netty.udp.tftp.packet.entry.ErrorPacket;

/**
 * @author Lzs
 * @date 2021/6/21 12:55
 * @description
 */
public class ErrorCoder implements Coder<ErrorPacket> {
    private static Coder coder;

    // 单例模式-懒汉
    public static Coder getCoder() {
        if(ObjectUtil.isNull(coder)) {
            synchronized (ErrorCoder.class) {
                if(ObjectUtil.isNull(coder)) {
                    coder = new ErrorCoder();
                }
            }
        }
        return coder;
    }

    @Override
    public ByteBuf encoder(ErrorPacket packet) {
        ByteBuf buf = Unpooled.buffer(20);
        buf.writeBytes(packet.getPacketType().toByteArray());
        buf.writeShort(packet.getErrorCode());
        buf.writeBytes(PacketUtils.strToAscii(packet.getErrorMsg()));
        buf.writeByte(0);
        return buf;
    }

    @Override
    public ErrorPacket decoder(ByteBuf buf) {
        ErrorPacket errorPacket = new ErrorPacket();
        errorPacket.setPacketType(PacketType.getByCode(buf.readUnsignedShort()));
        errorPacket.setErrorCode(buf.readUnsignedShort());
        if (buf.isReadable() && buf.readableBytes() > 1) {
            byte[] msgByte = new byte[buf.readableBytes() - 1];
            buf.readBytes(msgByte);
            errorPacket.setErrorMsg(new String(msgByte, CharsetUtil.US_ASCII));
        }
        return errorPacket;
    }
}