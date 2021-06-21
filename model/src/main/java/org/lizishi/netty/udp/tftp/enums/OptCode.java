package org.lizishi.netty.udp.tftp.enums;

/**
 * @author Lzs
 * @date 2021/6/21 9:40
 * @description
 */
public enum OptCode {
    REQ(1, "读请求包"),
    WRQ(2, "读请求包"),
    DATA(3, "数据包"),
    ACK(4, "确认包"),
    ERROR(5, "错误包"),
    ;
    int code;
    String des;

    OptCode(int code, String des) {
        this.code = code;
        this.des = des;
    }
}
