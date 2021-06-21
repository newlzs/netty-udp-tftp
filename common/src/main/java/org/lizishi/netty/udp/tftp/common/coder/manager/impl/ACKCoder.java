package org.lizishi.netty.udp.tftp.common.coder.manager.impl;

import cn.hutool.core.util.ObjectUtil;
import org.lizishi.netty.udp.tftp.common.coder.manager.Coder;
import org.lizishi.netty.udp.tftp.packet.entry.ACKPacket;

/**
 * @author Lzs
 * @date 2021/6/21 12:56
 * @description ack编解码
 */
public class ACKCoder implements Coder<ACKPacket>{
    private static Coder coder;

    // 单例模式-懒汉
    public static Coder getCoder() {
        if(ObjectUtil.isNull(coder)) {
            synchronized (coder) {
                if(ObjectUtil.isNull(coder)) {
                    coder = new ACKCoder();
                }
            }
        }
        return coder;
    }

    @Override
    public byte[] encoder(ACKPacket packet) {
        return new byte[0];
    }

    @Override
    public ACKPacket decoder(byte[] bytes) {
        return null;
    }
}