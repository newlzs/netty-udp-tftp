package org.lizishi.netty.udp.tftp.service.opService;

import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;
import org.lizishi.netty.udp.tftp.handler.ServerWriteHandler;
import org.lizishi.netty.udp.tftp.packet.BasePacket;
import org.lizishi.netty.udp.tftp.packet.entry.WRQPacket;
import org.lizishi.netty.udp.tftp.service.ChannelFactory;

/**
 * @author Lzs
 * @date 2021/6/23 20:44
 * @description
 */
@Slf4j
public class WriteService {
    public static void create(BasePacket basePacket) {
        WRQPacket wrqPacket = (WRQPacket) basePacket;
        log.info("WriteService.create-> wrqPacket: {}", wrqPacket);
        ServerWriteHandler serverWriteHandler = new ServerWriteHandler();
        // 建立新的端口, 发送初始ack
        try{
            Channel channel = ChannelFactory.getNewChannel(serverWriteHandler);
            serverWriteHandler.handleWriteRequestPacket(channel, wrqPacket);
        }catch (Exception e) {
            log.error("WriteService.create-> ", e);
        }
    }
}