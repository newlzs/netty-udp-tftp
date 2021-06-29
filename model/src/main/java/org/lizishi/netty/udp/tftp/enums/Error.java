package org.lizishi.netty.udp.tftp.enums;

import lombok.Getter;

/**
 * @author Lzs
 * @date 2021/6/28 21:26
 * @description 错误类型枚举
 */
@Getter
public enum  Error {
    FILE_NOT_EXIST(0, "%s file not exist"),
    ;

    int code;
    String msg;

    Error(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }
}