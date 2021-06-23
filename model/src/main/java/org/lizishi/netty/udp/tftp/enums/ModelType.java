package org.lizishi.netty.udp.tftp.enums;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Lzs
 * @date 2021/6/21 16:35
 * @description
 */
@Slf4j
public enum ModelType {
    octet("octet", "文件"),
    ;

    public static ModelType getByCode(String code) {
        log.info("ModelType.getByCode-> code: {}", code);
        for(ModelType modelType: ModelType.values()) {
            if(modelType.code.equals(code)) {
                return modelType;
            }
        }
        log.error("ModelType.getByCode-> 读取模式不存在");
        return null;
    }

    @Getter
    String code;
    String des;

    ModelType(String code, String des) {
        this.code = code;
        this.des = des;
    }
}
