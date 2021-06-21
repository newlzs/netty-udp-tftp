package org.lizishi.netty.udp.tftp.packet;

import lombok.Data;

import java.net.InetSocketAddress;

/**
 * @author Lzs
 * @date 2021/6/21 9:39
 * @description
 */
@Data
public abstract class BasePacket {
    // 发自
    InetSocketAddress remoteAddress;
    // 类型
    int Opcode;
}