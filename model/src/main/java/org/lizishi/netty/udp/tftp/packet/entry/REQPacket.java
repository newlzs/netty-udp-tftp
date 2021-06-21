package org.lizishi.netty.udp.tftp.packet.entry;

import lombok.Data;
import org.lizishi.netty.udp.tftp.packet.BasePacket;

/**
 * @author Lzs
 * @date 2021/6/21 12:31
 * @description 读请求包
 */
@Data
public class REQPacket extends BasePacket {
    // 文件名
    String fileName;

    // 模式
    String model;
}