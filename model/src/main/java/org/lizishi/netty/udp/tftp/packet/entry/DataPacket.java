package org.lizishi.netty.udp.tftp.packet.entry;

import lombok.Data;
import org.lizishi.netty.udp.tftp.enums.PacketType;
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

    public DataPacket() {
    }

    public DataPacket(int blockNum, byte[] data) {
        super(PacketType.DATA);
        this.blockNum = blockNum;
        this.data = data;
    }
}