package org.lizishi.netty.udp.tftp.common.coder;

import org.lizishi.netty.udp.tftp.common.coder.manager.Coder;
import org.lizishi.netty.udp.tftp.common.coder.manager.impl.*;
import org.lizishi.netty.udp.tftp.enums.PacketType;


/**
 * @author Lzs
 * @date 2021/6/21 12:53
 * @description coder注入
 */
public class CoderHandler {
    
    public static Coder getCoder(PacketType packetType) {
        switch (packetType) {
            case RRQ:
                return RRQCoder.getCoder();
            case WRQ:
                return WRQCoder.getCoder();
            case DATA:
                return DataCoder.getCoder();
            case ACK:
                return ACKCoder.getCoder();
            case ERROR:
                return ErrorCoder.getCoder();
            default:
                return null;
        }
    }
}