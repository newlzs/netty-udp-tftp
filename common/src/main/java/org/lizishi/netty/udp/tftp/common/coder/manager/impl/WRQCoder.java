package org.lizishi.netty.udp.tftp.common.coder.manager.impl;

import cn.hutool.core.util.ObjectUtil;
import io.netty.buffer.ByteBuf;
import org.lizishi.netty.udp.tftp.common.coder.manager.Coder;
import org.lizishi.netty.udp.tftp.packet.entry.WRQPacket;

/**
 * @author Lzs
 * @date 2021/6/21 12:56
 * @description
 */
public class WRQCoder implements Coder<WRQPacket> {
    private static Coder coder;

    // 单例模式-懒汉
    public static Coder getCoder() {
        if(ObjectUtil.isNull(coder)) {
            synchronized (coder) {
                if(ObjectUtil.isNull(WRQCoder.class)) {
                    coder = new WRQCoder();
                }
            }
        }
        return coder;
    }

    @Override
    public ByteBuf encoder(WRQPacket packet) {
        return null;
    }

    @Override
    public WRQPacket decoder(ByteBuf buf) {
        return null;
    }
}