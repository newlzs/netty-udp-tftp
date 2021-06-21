package org.lizishi.netty.udp.tftp.packet.entry;

import io.netty.buffer.ByteBuf;
import lombok.Data;
import org.lizishi.netty.udp.tftp.packet.BasePacket;

/**
 * @author Lzs
 * @date 2021/6/21 12:36
 * @description 数据包
 */
@Data
public class DataPacket extends BasePacket {
    // 数据块号
    int blockNum;

    // 数据
    byte[] data;
}