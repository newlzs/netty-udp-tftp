package org.lizishi.netty.udp.tftp.common.utils;

import io.netty.buffer.ByteBuf;
import io.netty.channel.socket.DatagramPacket;
import org.lizishi.netty.udp.tftp.packet.BasePacket;

import java.net.InetSocketAddress;

/**
 * @author Lzs
 * @date 2021/6/23 11:13
 * @description
 */
public class DatagramUtils {

    public static DatagramPacket buildDatagramPacket(BasePacket packet, InetSocketAddress remoteAddress) {
        ByteBuf buf = PacketUtils.toByteBuf(packet);
        return new DatagramPacket(buf, remoteAddress);
    }
}