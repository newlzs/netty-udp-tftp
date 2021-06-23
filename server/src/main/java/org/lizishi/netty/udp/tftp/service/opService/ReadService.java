package org.lizishi.netty.udp.tftp.service.opService;

import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;
import org.lizishi.netty.udp.tftp.handler.ServerReadHandler;
import org.lizishi.netty.udp.tftp.packet.BasePacket;
import org.lizishi.netty.udp.tftp.packet.entry.RRQPacket;
import org.lizishi.netty.udp.tftp.service.ChannelFactory;

/**
 * @author Lzs
 * @date 2021/6/22 11:36
 * @description
 */
@Slf4j
public class ReadService {

    public static void create(BasePacket basePacket) {
        RRQPacket rrqPacket = (RRQPacket) basePacket;
        log.info("ReadService.create-> rrqPacket: {}", rrqPacket);
        ServerReadHandler serverReadHandler = new ServerReadHandler();
        try{
            Channel channel = ChannelFactory.getNewChannel(serverReadHandler);
            serverReadHandler.handleReadRequestPacket(channel, rrqPacket);
        }catch (Exception e) {
            log.error("ReadService.create-> ", e);
        }
    }
}