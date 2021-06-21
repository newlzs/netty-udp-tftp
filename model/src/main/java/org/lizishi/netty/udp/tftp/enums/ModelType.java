package org.lizishi.netty.udp.tftp.enums;

import lombok.Getter;

/**
 * @author Lzs
 * @date 2021/6/21 16:35
 * @description
 */
public enum ModelType {
    octet("octet", "文件"),
    ;
    @Getter
    String code;
    String des;

    ModelType(String code, String des) {
        this.code = code;
        this.des = des;
    }
}
