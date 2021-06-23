package org.lizishi.netty.udp.tftp.handler;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.DatagramPacket;
import lombok.extern.slf4j.Slf4j;
import org.lizishi.netty.udp.tftp.common.utils.DatagramUtils;
import org.lizishi.netty.udp.tftp.common.utils.FileUtils;
import org.lizishi.netty.udp.tftp.common.utils.PacketUtils;
import org.lizishi.netty.udp.tftp.enums.PacketType;
import org.lizishi.netty.udp.tftp.packet.BasePacket;
import org.lizishi.netty.udp.tftp.packet.entry.ACKPacket;
import org.lizishi.netty.udp.tftp.packet.entry.DataPacket;
import org.lizishi.netty.udp.tftp.service.ClientService;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.RandomAccessFile;

import static org.lizishi.netty.udp.tftp.constant.PacketConstant.blockSize;


/**
 * @author Lzs
 * @date 2021/6/23 19:51
 * @description 处理客户端发送的写请求
 */
@Slf4j
public class ClientWriteHandler extends SimpleChannelInboundHandler<DatagramPacket> {
    private RandomAccessFile raf;

    // 写请求, 服务端其实确认包为0
    private int blockNumber = 0;

    private byte[] blockBuffer;

    private long fileLength;

    private boolean readFinished;

    private ClientService clientService;

    public ClientWriteHandler(){}

    public ClientWriteHandler(ClientService clientService) {
        this.clientService = clientService;
    }

    @Override
    protected void messageReceived(ChannelHandlerContext ctx, DatagramPacket datagramPacket) throws Exception {
        ByteBuf buf = datagramPacket.content();
        BasePacket packet = PacketUtils.create(buf);
        packet.setRemoteAddress(datagramPacket.sender());
        if(packet.getPacketType() == PacketType.ACK) {
            handleAckPacket(ctx, (ACKPacket) packet);
            // 引用加一, 否则会重复释放报错
            datagramPacket.retain();
        }
    }

    /**
     * 处理ACK包
     * @param ctx
     * @param ackPacket
     */
    private void handleAckPacket(ChannelHandlerContext ctx, ACKPacket ackPacket) {
        // 当ack的blockNumber和上一个blockNumber一样时，则认为应答正常。
        if (ackPacket.getBlockNum() == blockNumber) {
            // 如果是第一个ACK的包, 初始化文件
            if(ackPacket.getBlockNum() == 0) {
                if(preWriteHandler() == null) {
                    log.error("ClientWriteHandler.handleAckPacket-> fileName:{}, 不存在", clientService.fileName);
                    //todo 2021/6/22 发送错误报文, 重置
                }
            }

            // 若读取完毕，则
            if (readFinished) {
                log.info("ServerReadHandler.handleAckPacket-> fileName:{}, 读取完毕.", clientService.fileName);
                //todo 2021/6/23 重置
                return;
            }

            // 块号加1
            blockNumber++;
            try {
                DataPacket dataPacket = new DataPacket();
                readFinished = FileUtils.createDataPacket(dataPacket, raf, fileLength, blockNumber, blockBuffer);
                if (dataPacket != null) {
                    log.info("ClientWriteHandler.handleAckPacket-> fileName:{}, data:{}",clientService.fileName, dataPacket);
                    DatagramPacket datagramPacket = DatagramUtils.buildDatagramPacket(dataPacket, ackPacket.getRemoteAddress());
                    ctx.writeAndFlush(datagramPacket);
                }
            } catch (Exception exp) {
                log.info("ServerReadHandler.handleAckPacket-> fileName:{}, 写入文件失败", clientService.fileName, exp);
                // todo 2021/6/23 发送错误报文
            }
        }
        // 如果应答不正常，就重传上一个包。
        else {
            log.warn("ClientWriteHandler.handleAckPacket-> ack包不正常，重传上一个data包");
            DataPacket dataPacket = new DataPacket(blockNumber, blockBuffer);
            DatagramPacket datagramPacket = DatagramUtils.buildDatagramPacket(dataPacket, ackPacket.getRemoteAddress());
            log.info("ClientWriteHandler.handleAckPacket-> fileName:{}, data:{}",clientService.fileName, dataPacket);
            ctx.writeAndFlush(datagramPacket);
        }
    }

    /**
     * 写文件预处理
     */
    public File preWriteHandler() {
        // 块大小选项
        this.blockBuffer = new byte[blockSize];
        // 初始化文件读取器
        File file = new File(ClientService.rootPath, clientService.fileName);
        try {
            raf = new RandomAccessFile(file, "r");
        } catch (FileNotFoundException exp) {
            return null;
        }
        fileLength = file.length();
        log.info("ClientWriteHandler.preWriteHandler-> 向服务器写, 文件：{} , 大小：{}B, 块大小：{}B, 分{}次传输",
                file, fileLength, blockSize, (fileLength / blockSize) + 1);
        return file;
    }

}