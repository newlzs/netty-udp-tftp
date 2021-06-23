package org.lizishi.netty.udp.tftp.common.utils;

/**
 * @author Lzs
 * @date 2021/6/22 21:41
 * @description
 */
public class FileUtils {

    public static String getFileName(String path) {
        String[] parts = path.split("/");
        return parts[parts.length - 1];
    }
}