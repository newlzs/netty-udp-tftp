package org.lizishi.netty.udp.tftp.packet;

import lombok.Data;

/**
 * @author Lzs
 * @date 2021/6/21 9:39
 * @description
 */
@Data
public abstract class BasePacket {
    int Opcode;
}