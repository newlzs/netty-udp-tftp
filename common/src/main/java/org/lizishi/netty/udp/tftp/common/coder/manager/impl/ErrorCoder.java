package org.lizishi.netty.udp.tftp.common.coder.manager.impl;

import cn.hutool.core.util.ObjectUtil;
import io.netty.buffer.ByteBuf;
import org.lizishi.netty.udp.tftp.common.coder.manager.Coder;
import org.lizishi.netty.udp.tftp.packet.entry.ErrorPacket;

/**
 * @author Lzs
 * @date 2021/6/21 12:55
 * @description
 */
public class ErrorCoder implements Coder<ErrorPacket> {
    private static Coder coder;

    // 单例模式-懒汉
    public static Coder getCoder() {
        if(ObjectUtil.isNull(coder)) {
            synchronized (ErrorCoder.class) {
                if(ObjectUtil.isNull(coder)) {
                    coder = new ErrorCoder();
                }
            }
        }
        return coder;
    }

    @Override
    public ByteBuf encoder(ErrorPacket packet) {
        return null;
    }

    @Override
    public ErrorPacket decoder(ByteBuf buf) {
        return null;
    }
}