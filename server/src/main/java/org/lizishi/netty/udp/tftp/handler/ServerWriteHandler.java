package org.lizishi.netty.udp.tftp.handler;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.DatagramPacket;
import lombok.extern.slf4j.Slf4j;
import org.lizishi.netty.udp.tftp.common.utils.DatagramUtils;
import org.lizishi.netty.udp.tftp.common.utils.PacketUtils;
import org.lizishi.netty.udp.tftp.constant.PacketConstant;
import org.lizishi.netty.udp.tftp.enums.PacketType;
import org.lizishi.netty.udp.tftp.packet.BasePacket;
import org.lizishi.netty.udp.tftp.packet.entry.ACKPacket;
import org.lizishi.netty.udp.tftp.packet.entry.DataPacket;
import org.lizishi.netty.udp.tftp.packet.entry.WRQPacket;
import org.lizishi.netty.udp.tftp.service.TFTPservice;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;


/**
 * @author Lzs
 * @date 2021/6/23 20:43
 * @description
 */
@Slf4j
public class ServerWriteHandler extends SimpleChannelInboundHandler<DatagramPacket> {
    private RandomAccessFile raf;

    private int blockNumber;

    private String fileName;

    public ServerWriteHandler(){}

    @Override
    protected void messageReceived(ChannelHandlerContext ctx, DatagramPacket datagramPacket) {
        ByteBuf buf = datagramPacket.content();
        BasePacket packet = PacketUtils.create(buf);
        packet.setRemoteAddress(datagramPacket.sender());

        if(packet.getPacketType() == PacketType.DATA) {
            handleDataPacket(ctx, packet);
            // 引用加一, 否则会重复释放报错
            datagramPacket.retain();
        }
    }

    /**
     * 处理读请求, 返回第一个ack block=0
     * @param ctx
     * @param wrqPacket
     */
    public void handleWriteRequestPacket(Channel ctx, WRQPacket wrqPacket) {
        this.blockNumber = 0;
        this.fileName = wrqPacket.getFileName();
        // 初始化文件
        if(preHandleWriteRequest() == null) {
            //todo 2021/6/23 文件创建失败, 返回错误报文, 关闭端口
        }
        ACKPacket ackPacket = new ACKPacket(blockNumber);
        DatagramPacket datagramPacket = DatagramUtils.buildDatagramPacket(ackPacket, wrqPacket.getRemoteAddress());
        log.info("ServerWriteHandler.handleDataPacket-> 发送Ack报文：{}", ackPacket);
        this.blockNumber ++;
        ctx.writeAndFlush(datagramPacket);
    }

    /**
     * 写文件
     * @param ctx
     * @param packet
     */
    private void handleDataPacket(ChannelHandlerContext ctx, BasePacket packet) {
        DataPacket dataPacket = (DataPacket) packet;
        // 如果包号和我们应该收到一样则正常;
        if (dataPacket.getBlockNum() == blockNumber) {
            try {
                // 读取包数据，写入文件
                byte[] bytes = dataPacket.getData();
                raf.write(bytes);
                if (bytes.length < PacketConstant.blockSize) {
                    raf.close();
                    log.info("ServerWriteHandler.handleDataPacket-> fileName:{}, 写入完毕", fileName);
                    // 重置原始handler
                    //todo 2021/6/22
                }
            } catch (Exception exp) {
                log.error("ServerWriteHandler.handleDataPacket-> fileName:{}, 写入出错", fileName, exp);
                //todo 2021/6/23 发送错误信息
                return;
            }
            ACKPacket ackPacket = new ACKPacket(dataPacket.getBlockNum());
            ByteBuf buf = PacketUtils.toByteBuf(ackPacket);
            io.netty.channel.socket.DatagramPacket datagramPacket = new io.netty.channel.socket.DatagramPacket(buf, dataPacket.getRemoteAddress());
            log.info("ServerWriteHandler.handleDataPacket-> 发送Ack报文：{}", ackPacket);
            ctx.writeAndFlush(datagramPacket);
            // 块号加1
            blockNumber++;
        }
    }

    /**
     * 提前创建文件句柄
     */
    private File preHandleWriteRequest() {
        File file = new File(TFTPservice.rootPath + this.fileName);
        // 若文件不存在，则创建
        if (!file.exists()) {
            try {
                boolean created = file.createNewFile();
                if (created) {
                    log.info("ServerWriteHandler.preHandleWriteRequest-> 文件不存在，新建文件");
                }
            } catch (IOException exp) {
                log.info("ServerWriteHandler.preHandleWriteRequest-> 创建文件失败", exp);
                return null;
            }
        }
        try {
            raf = new RandomAccessFile(file, "rw");
        } catch (FileNotFoundException exp) {
            log.info("ClientReadHandler.preHandleWriteRequest-> 文件不存在");
            //todo 2021/6/23 错误相应
            return null;
        }
        return file;
    }
}