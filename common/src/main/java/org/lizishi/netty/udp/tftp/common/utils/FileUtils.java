package org.lizishi.netty.udp.tftp.common.utils;

import org.lizishi.netty.udp.tftp.enums.PacketType;
import org.lizishi.netty.udp.tftp.packet.entry.DataPacket;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Arrays;

import static org.lizishi.netty.udp.tftp.constant.PacketConstant.blockSize;

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

    /**
     * 从文件构建数据包
     * @param packet 数据包
     * @param raf   随机文件流
     * @param fileLength 文件总长度
     * @param blockNumber   快号
     * @param blockBuffer   数据块
     * @return
     * @throws IOException
     */
    public static boolean createDataPacket(DataPacket packet, RandomAccessFile raf, long fileLength, int blockNumber, byte[] blockBuffer) throws IOException {
        boolean readFinished = false;
        packet.setBlockNum(blockNumber);
        packet.setPacketType(PacketType.DATA);
        int readCount = raf.read(blockBuffer);
        // 当读不到内容时
        if (readCount == -1) {
            raf.close();
            readFinished = true;
            // 文件读取完毕后，还需检查文件大小是否等于blockSize的整数倍，
            // 若是，则需要再补发一个空包
            if (fileLength % blockSize == 0) {
                // 内容为空的数据包
                packet.setData(new byte[]{});
            }
        } else {
            // 当 readCount小于blockSize时，说明它是最后一个数据块
            if (readCount < blockSize) {
                raf.close();
                readFinished = true;
                byte[] lastBlockData = Arrays.copyOf(blockBuffer, readCount);
                packet.setData(lastBlockData);
            } else {
                packet.setData(blockBuffer);
            }
        }
        return readFinished;
    }
}