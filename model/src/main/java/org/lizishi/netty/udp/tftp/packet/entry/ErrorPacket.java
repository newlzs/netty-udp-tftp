package org.lizishi.netty.udp.tftp.packet.entry;

import lombok.Data;
import org.lizishi.netty.udp.tftp.packet.BasePacket;

/**
 * @author Lzs
 * @date 2021/6/21 12:46
 * @description 错误包
 */
@Data
public class ErrorPacket extends BasePacket {
    // 错误代码
    int errorCode;
    // 错误信息
    String errorMsg;
}