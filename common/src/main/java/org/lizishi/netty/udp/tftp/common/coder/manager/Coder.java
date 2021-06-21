package org.lizishi.netty.udp.tftp.common.coder.manager;

import org.lizishi.netty.udp.tftp.packet.BasePacket;

import java.io.IOException;

/**
 * @author Lzs
 * @date 2021/6/21 13:01
 * @description
 */
public interface Coder<T extends BasePacket> {
    // 编码
    byte[] encoder(T packet) throws IOException;
    // 解码
    T decoder(byte[] bytes);

    public static byte[] getOpCode(int opCode) {
        byte[] result = new byte[2];
        result[0] = (byte) (opCode & 0xFF);
        result[1] = (byte) ((opCode >> 8) & 0xFF);

        return result;
    }
}
