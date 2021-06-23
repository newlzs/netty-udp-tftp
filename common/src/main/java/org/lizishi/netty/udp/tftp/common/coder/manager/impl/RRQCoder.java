package org.lizishi.netty.udp.tftp.common.coder.manager.impl;

import cn.hutool.core.util.ObjectUtil;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.util.CharsetUtil;
import lombok.extern.slf4j.Slf4j;
import org.lizishi.netty.udp.tftp.common.coder.manager.Coder;
import org.lizishi.netty.udp.tftp.common.utils.PacketUtils;
import org.lizishi.netty.udp.tftp.enums.ModelType;
import org.lizishi.netty.udp.tftp.enums.PacketType;
import org.lizishi.netty.udp.tftp.packet.entry.RRQPacket;

/**
 * @author Lzs
 * @date 2021/6/21 12:53
 * @description
 */
@Slf4j
public class RRQCoder implements Coder<RRQPacket> {
    private static Coder coder;

    // 单例模式-懒汉
    public static Coder getCoder() {
        if(ObjectUtil.isNull(coder)) {
            synchronized (RRQCoder.class) {
                if(ObjectUtil.isNull(coder)) {
                    coder = new RRQCoder();
                }
            }
        }
        return coder;
    }

    @Override
    public ByteBuf encoder(RRQPacket packet) {
        log.info("REQCoder.encoder-> packet: {}", packet);
        ByteBuf buf = Unpooled.buffer(20);
        buf.writeBytes(packet.getPacketType().toByteArray());
        buf.writeBytes(PacketUtils.strToAscii(packet.getFileName()));
        buf.writeByte((byte)0);
        buf.writeBytes(packet.getModel().getCode().getBytes(CharsetUtil.US_ASCII));
        return buf;
    }

    @Override
    public RRQPacket decoder(ByteBuf buf) {
        RRQPacket rrqPacket = new RRQPacket();
        rrqPacket.setPacketType(PacketType.getByCode(buf.readUnsignedShort()));
        // 读取剩下的字节为字符串
        byte[] bytes = new byte[buf.readableBytes()];
        buf.readBytes(bytes);
        String str = new String(bytes, CharsetUtil.US_ASCII);
        String[] strArray = str.split("\0");
        // 文件名
        rrqPacket.setFileName(strArray[0]);
        // 模式
        rrqPacket.setModel(ModelType.getByCode(strArray[1]));

        return rrqPacket;
    }
}