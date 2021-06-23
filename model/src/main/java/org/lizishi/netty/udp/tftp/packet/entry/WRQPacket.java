package org.lizishi.netty.udp.tftp.packet.entry;

import lombok.Data;
import org.lizishi.netty.udp.tftp.enums.ModelType;
import org.lizishi.netty.udp.tftp.enums.PacketType;
import org.lizishi.netty.udp.tftp.packet.BasePacket;

/**
 * @author Lzs
 * @date 2021/6/21 12:35
 * @description
 */
@Data
public class WRQPacket extends BasePacket {
    // 文件名
    private String fileName;

    // 模式
    private ModelType model;

    public WRQPacket(){}

    public WRQPacket(String fileName, ModelType model) {
        super(PacketType.WRQ);
        this.fileName = fileName;
        this.model = model;
    }}