package org.lizishi.netty.udp.tftp.common.coder.manager;

import io.netty.buffer.ByteBuf;
import org.lizishi.netty.udp.tftp.packet.BasePacket;

/**
 * @author Lzs
 * @date 2021/6/21 13:01
 * @description
 */
public interface Coder<T extends BasePacket> {
    // 编码
    ByteBuf encoder(T packet);
    // 解码
    T decoder(ByteBuf buf);
}
