package org.lizishi.netty.udp.tftp.common.coder;

import org.lizishi.netty.udp.tftp.common.coder.manager.Coder;
import org.lizishi.netty.udp.tftp.common.coder.manager.impl.ACKCoder;
import org.lizishi.netty.udp.tftp.enums.PacketType;


/**
 * @author Lzs
 * @date 2021/6/21 12:53
 * @description coder注入
 */
public class CoderHandler {
    
    public static Coder getCoder(int packetTypeCode) {
        if(packetTypeCode == PacketType.ACK.getCode()) {
            return ACKCoder.getCoder();
        }
        //todo 2021/6/21 补充剩余策略
        
        throw new RuntimeException("包类型异常");
    }
}