package org.lizishi.netty.udp.tftp.common.coder.manager.impl;

import cn.hutool.core.util.ObjectUtil;
import io.netty.util.CharsetUtil;
import lombok.extern.slf4j.Slf4j;
import org.lizishi.netty.udp.tftp.common.coder.manager.Coder;
import org.lizishi.netty.udp.tftp.packet.entry.REQPacket;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * @author Lzs
 * @date 2021/6/21 12:53
 * @description
 */
@Slf4j
public class REQCoder implements Coder<REQPacket> {
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
    public byte[] encoder(REQPacket packet) {
        log.info("REQCoder.encoder-> packet: {}", packet);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try {
            outputStream.write(Coder.getOpCode(packet.getOpcode()));
            outputStream.write(packet.getFileName().getBytes(CharsetUtil.US_ASCII));
            outputStream.write((byte)0);
            outputStream.write(packet.getModel().getBytes(CharsetUtil.US_ASCII));
            byte[] res = outputStream.toByteArray();
            outputStream.close();
            return res;
        } catch (IOException e) {
            log.error("REQCoder.encoder-> ", e);
            e.printStackTrace();
        }

        return new byte[0];
    }

    @Override
    public REQPacket decoder(byte[] bytes) {
        return null;
    }
}