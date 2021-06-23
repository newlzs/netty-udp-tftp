package org.lizishi.netty.udp.tftp.packet.entry;

import lombok.Data;
import org.lizishi.netty.udp.tftp.enums.PacketType;
import org.lizishi.netty.udp.tftp.packet.BasePacket;

/**
 * @author Lzs
 * @date 2021/6/21 12:39
 * @description ack包
 */
@Data
public class ACKPacket extends BasePacket {
    // 确认块号
    private int blockNum;

    public ACKPacket(int blockNum) {
        super(PacketType.ACK);
        this.blockNum = blockNum;
    }

    public ACKPacket() {
    }
}