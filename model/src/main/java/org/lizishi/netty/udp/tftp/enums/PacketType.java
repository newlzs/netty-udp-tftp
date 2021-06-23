package org.lizishi.netty.udp.tftp.enums;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Lzs
 * @date 2021/6/21 9:40
 * @description
 */
@Slf4j
public enum PacketType {
    RRQ(1, "读请求包"),
    WRQ(2, "读请求包"),
    DATA(3, "数据包"),
    ACK(4, "确认包"),
    ERROR(5, "错误包"),
    ;

    public static PacketType getByCode(int code) {
        for (PacketType packetType: PacketType.values()) {
            if (packetType.code == code) {
                return packetType;
            }
        }
        log.info("PacketType.getByCode-> code:{} 包类型不存在", code);
        throw new IllegalArgumentException("包类型代码错误");
    }

    public byte[] toByteArray() {
        return new byte[]{0, (byte) code};
    }
    @Getter
    int code;
    String des;

    PacketType(int code, String des) {
        this.code = code;
        this.des = des;
    }
}
