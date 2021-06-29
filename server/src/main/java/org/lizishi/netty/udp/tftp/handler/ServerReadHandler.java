package org.lizishi.netty.udp.tftp.handler;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.DatagramPacket;
import lombok.extern.slf4j.Slf4j;
import org.lizishi.netty.udp.tftp.common.utils.ChannelUtils;
import org.lizishi.netty.udp.tftp.common.utils.DatagramUtils;
import org.lizishi.netty.udp.tftp.common.utils.ErrorUtils;
import org.lizishi.netty.udp.tftp.common.utils.PacketUtils;
import org.lizishi.netty.udp.tftp.enums.Error;
import org.lizishi.netty.udp.tftp.enums.ModelType;
import org.lizishi.netty.udp.tftp.enums.PacketType;
import org.lizishi.netty.udp.tftp.packet.BasePacket;
import org.lizishi.netty.udp.tftp.packet.entry.ACKPacket;
import org.lizishi.netty.udp.tftp.packet.entry.DataPacket;
import org.lizishi.netty.udp.tftp.packet.entry.RRQPacket;
import org.lizishi.netty.udp.tftp.service.TFTPservice;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Arrays;
import java.util.Objects;

import static org.lizishi.netty.udp.tftp.constant.PacketConstant.blockSize;

/**
 * @author Lzs
 * @date 2021/6/22 11:08
 * @description
 */
@Slf4j
public class ServerReadHandler extends SimpleChannelInboundHandler<DatagramPacket> {

    private RandomAccessFile raf;

    private long fileLength;

    private byte[] blockBuffer;

    private String fileName;

    private int blockNumber;

    private boolean readFinished = false;

    @Override
    protected void messageReceived(ChannelHandlerContext ctx, DatagramPacket datagramPacket) throws Exception {
        ByteBuf buf = datagramPacket.content();
        BasePacket packet = PacketUtils.create(buf);
        packet.setRemoteAddress(datagramPacket.sender());
        // 重置
        buf.readerIndex(0);
        if(packet.getPacketType() == PacketType.ACK) {
            handleAckPacket(ctx, (ACKPacket) packet);
        }
    }

    /**
     * 处理读请求
     * @param ctx
     * @param readPacket
     */
    public void handleReadRequestPacket(Channel ctx, RRQPacket readPacket) {
        this.fileName = readPacket.getFileName();
        // 读请求预处理
        File file = preHandleReadRequest(ctx, readPacket);
        if (file == null) {
            ErrorUtils.sendErrorAndCloseChannel(ctx, Error.FILE_NOT_EXIST.getCode(),
                    String.format(Error.FILE_NOT_EXIST.getMsg(), readPacket.getFileName()),
                    readPacket.getRemoteAddress());
            return ;
        }
        // 块大小选项
        blockBuffer = new byte[blockSize];
        fileLength = file.length();
        log.info("读请求, 文件：{} , 大小：{}B, 块大小：{}B, 分{}次传输.",
                file, fileLength, blockSize, (fileLength / blockSize) + 1);
         // 传输第1块
        blockNumber = 1;
        try {
            DataPacket dataPacket = createDataPacket(blockNumber);
            if (dataPacket != null) {
               log.info("ReadFileHandler.handleReadRequestPacket-> send file:{}, block:{}", fileName, blockNumber);
                ByteBuf buf = PacketUtils.toByteBuf(dataPacket);
                DatagramPacket datagramPacket = new DatagramPacket(buf, readPacket.getRemoteAddress());

               ctx.writeAndFlush(datagramPacket);
            }
        } catch (IOException exp) {
            log.error("读取文件失败", exp);
            //todo 2021/6/22 发送错误报文
        }
    }

    /**
     * 读请求预处理
     *
     * @param channel
     * @param readPacket
     * @return
     */
    private File preHandleReadRequest(Channel channel, RRQPacket readPacket) {
        // 模式处理，仅支持octet模式
        ModelType mode = readPacket.getModel();
        log.info("ReadFileHandler.preHandleReadRequest-> model:{}", mode);        
        if (!Objects.equals(mode, ModelType.octet)) {
            log.error("不支持此模式, mode:{}", mode);
            return null;
        }

        // 初始化文件读取器
        File file = new File(TFTPservice.rootPath, readPacket.getFileName());
        try {
            raf = new RandomAccessFile(file, "r");
        } catch (FileNotFoundException exp) {
            log.error("文件不存在", exp);
            return null;
        }
        return file;
    }

    /**
     * 处理ACK包
     * @param ctx
     * @param ackPacket
     */
    private void handleAckPacket(ChannelHandlerContext ctx, ACKPacket ackPacket) {
        // 当ack的blockNumber和上一个blockNumber一样时，则认为应答正常。
        if (ackPacket.getBlockNum() == blockNumber) {
            // 若读取完毕，则
            if (readFinished) {
                log.info("ServerReadHandler.handleAckPacket-> fileName:{}, 读取完毕.", fileName);
                // 关闭连接
                ChannelUtils.removeAndCloseByChannel(ctx.channel());
                return;
            }
            // 块号加1
            blockNumber++;
            try {
                DataPacket dataPacket = createDataPacket(blockNumber);
                if (dataPacket != null) {
                    log.info("ServerReadHandler.handleAckPacket-> fileName:{}, data:{}",fileName, dataPacket);
                    DatagramPacket datagramPacket = DatagramUtils.buildDatagramPacket(dataPacket, ackPacket.getRemoteAddress());
                    ctx.writeAndFlush(datagramPacket);
                }
            } catch (Exception exp) {
                log.info("ServerReadHandler.handleAckPacket-> fileName:{}, 写入文件失败", fileName);
                // todo 2021/6/23 发送错误报文
            }
        }
        // 如果应答不正常，就重传上一个包。
        else {
            log.warn("ServerReadHandler.handleAckPacket-> ack包不正常" +
                    "，重传上一个data包");
            DataPacket dataPacket = new DataPacket(blockNumber, blockBuffer);
            DatagramPacket datagramPacket = DatagramUtils.buildDatagramPacket(dataPacket, ackPacket.getRemoteAddress());
            log.info("ServerReadHandler.handleAckPacket-> fileName:{}, data:{}",fileName, dataPacket);
            ctx.writeAndFlush(datagramPacket);
        }
    }

    /**
     * 构建数据包
     * @param blockNumber
     * @return
     * @throws IOException
     */
    private DataPacket createDataPacket(int blockNumber) throws IOException {
        DataPacket packet;
        int readCount = raf.read(blockBuffer);
        // 当读不到内容时
        if (readCount == -1) {
            raf.close();
            readFinished = true;
            // 文件读取完毕后，还需检查文件大小是否等于blockSize的整数倍，
            // 若是，则需要再补发一个空包
            if (fileLength % blockSize == 0) {
                // 内容为空的数据包
                packet = new DataPacket(blockNumber, new byte[]{});
            } else {
                return null;
            }
        } else {
            // 当 readCount小于blockSize时，说明它是最后一个数据块
            if (readCount < blockSize) {
                raf.close();
                readFinished = true;
                //
                byte[] lastBlockData = Arrays.copyOf(blockBuffer, readCount);
                packet = new DataPacket(blockNumber, lastBlockData);
            } else {
                packet = new DataPacket(blockNumber, blockBuffer);
            }
        }
        return packet;
    }

}