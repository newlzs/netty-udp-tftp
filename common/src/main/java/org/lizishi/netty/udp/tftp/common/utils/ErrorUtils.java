package org.lizishi.netty.udp.tftp.common.utils;

import io.netty.channel.Channel;
import io.netty.channel.socket.DatagramPacket;
import org.lizishi.netty.udp.tftp.packet.entry.ErrorPacket;

import java.net.InetSocketAddress;

/**
 * @author Lzs
 * @date 2021/6/28 21:35
 * @description
 */
public class ErrorUtils {

    public static void sendErrorAndCloseChannel(Channel ctx, int code, String msg, InetSocketAddress remoteAddress) {
        ErrorPacket errorPacket = PacketUtils.buildErrorPacket(code, msg);
        DatagramPacket datagramPacket = DatagramUtils.buildDatagramPacket(errorPacket, remoteAddress);
        ctx.writeAndFlush(datagramPacket);
        ChannelUtils.removeAndCloseByChannel(ctx);
    }
}