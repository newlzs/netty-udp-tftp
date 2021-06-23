package org.lizishi.netty.udp.tftp.handler;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.DatagramPacket;
import lombok.extern.slf4j.Slf4j;
import org.lizishi.netty.udp.tftp.common.utils.PacketUtils;
import org.lizishi.netty.udp.tftp.constant.PacketConstant;
import org.lizishi.netty.udp.tftp.enums.PacketType;
import org.lizishi.netty.udp.tftp.packet.BasePacket;
import org.lizishi.netty.udp.tftp.packet.entry.ACKPacket;
import org.lizishi.netty.udp.tftp.packet.entry.DataPacket;
import org.lizishi.netty.udp.tftp.service.ClientService;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;


/**
 * @author Lzs
 * @date 2021/6/22 21:23
 * @description
 */
@Slf4j
public class ClientReadHandler extends SimpleChannelInboundHandler<DatagramPacket> {

    private RandomAccessFile raf;

    private int blockNumber = 1;

    private ClientService clientService;

    public ClientReadHandler(){}

    public ClientReadHandler(ClientService clientService) {
        this.clientService = clientService;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, DatagramPacket datagramPacket) throws Exception {
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
     * 写文件
     * @param ctx
     * @param packet
     */
    private void handleDataPacket(ChannelHandlerContext ctx, BasePacket packet) {
        DataPacket dataPacket = (DataPacket) packet;
        // 如果确认包号和我们应该收到一样则正常;
        if (dataPacket.getBlockNum() == blockNumber) {
            // 如果收到的是第一个包,怎进行文件初始化
            if(blockNumber == 1) {
                preHandleWriteRequest();
            }
            try {
                // 读取包数据，写入文件
                byte[] bytes = dataPacket.getData();
                raf.write(bytes);
                if (bytes.length < PacketConstant.blockSize) {
                    raf.close();
                    log.info("写入完毕");
                    // 重置原始handler
                    //todo 2021/6/22
                }
            } catch (Exception exp) {
                log.error("写入文件失败", exp);
                //todo 2021/6/23 发送错误信息
                return;
            }
            ACKPacket ackPacket = new ACKPacket(dataPacket.getBlockNum());
            ByteBuf buf = PacketUtils.toByteBuf(ackPacket);
            DatagramPacket datagramPacket = new DatagramPacket(buf, dataPacket.getRemoteAddress());
            log.info("ClientReadHandler.handleDataPacket-> 发送Ack报文：{}", ackPacket);
            ctx.writeAndFlush(datagramPacket);
            // 块号加1
            blockNumber++;
        }
    }

    /**
     * 提前创建文件句柄
     */
    private File preHandleWriteRequest() {

        File file = new File("./" + this.clientService.fileName);
        // 若文件不存在，则创建
        if (!file.exists()) {
            try {
                boolean created = file.createNewFile();
                if (created) {
                    log.info("ClientReadHandler.preHandleWriteRequest-> 文件不存在，新建文件");
                }
            } catch (IOException exp) {
                log.info("ClientReadHandler.preHandleWriteRequest-> 创建文件失败", exp);
                // 发送错误报文
                // 重置通道
                //todo 2021/6/23
                return null;
            }
        }
        //
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