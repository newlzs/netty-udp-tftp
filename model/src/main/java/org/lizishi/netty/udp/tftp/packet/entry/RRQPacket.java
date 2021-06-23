package org.lizishi.netty.udp.tftp.packet.entry;

import lombok.Data;
import org.lizishi.netty.udp.tftp.enums.ModelType;
import org.lizishi.netty.udp.tftp.enums.PacketType;
import org.lizishi.netty.udp.tftp.packet.BasePacket;

/**
 * @author Lzs
 * @date 2021/6/21 12:31
 * @description 读请求包
 */
@Data
public class RRQPacket extends BasePacket {
    // 文件名
    private String fileName;

    // 模式
    private ModelType model;

    public RRQPacket(){}

    public RRQPacket( String fileName, ModelType model) {
        super(PacketType.RRQ);
        this.fileName = fileName;
        this.model = model;
    }
}