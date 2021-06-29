package org.lizishi.netty.udp.tftp.common.utils;

import io.netty.buffer.ByteBuf;
import io.netty.util.CharsetUtil;
import org.lizishi.netty.udp.tftp.common.coder.CoderHandler;
import org.lizishi.netty.udp.tftp.common.coder.manager.Coder;
import org.lizishi.netty.udp.tftp.enums.PacketType;
import org.lizishi.netty.udp.tftp.packet.BasePacket;
import org.lizishi.netty.udp.tftp.packet.entry.ErrorPacket;

/**
 * @author Lzs
 * @date 2021/6/22 9:47
 * @description
 */
public class PacketUtils {

    public static BasePacket create(ByteBuf buf) {
        PacketType packetType = PacketType.getByCode(buf.readUnsignedShort());
        Coder coder = CoderHandler.getCoder(packetType);
        buf.readerIndex(buf.readerIndex() - 2);

        BasePacket packet = coder.decoder(buf);
        packet.setPacketType(packetType);
        return packet;
    }

    public static ByteBuf toByteBuf(BasePacket basePacket) {
        Coder coder = CoderHandler.getCoder(basePacket.getPacketType());
        return coder.encoder(basePacket);
    }

    public static byte[] strToAscii(String str) {
        return str.getBytes(CharsetUtil.US_ASCII);
    }


    public static ErrorPacket buildErrorPacket(int errorCode, String message) {
        ErrorPacket errorPacket = new ErrorPacket();
        errorPacket.setPacketType(PacketType.ERROR);
        errorPacket.setErrorCode(errorCode);
        errorPacket.setErrorMsg(message);
        return errorPacket;
    }
}