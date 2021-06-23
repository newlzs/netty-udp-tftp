package org.lizishi.netty.udp.tftp.common.utils;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.UnpooledByteBufAllocator;
import org.junit.Test;

/**
 * @author Lzs
 * @date 2021/6/22 9:59
 * @description
 */
public class PacketUtilsTest {

    @Test
    public void shortTest() {
        ByteBuf buf = new UnpooledByteBufAllocator(true).buffer();
        byte[] a = new byte[3];
        a[1] = (byte) 1;
//        a[1] = (byte) 1;
//        a[2] = (byte) 1;

        buf.writeBytes(a);
        int c = buf.readUnsignedShort();
        System.out.println(c);

    }
    @Test
    public void create() {
    }
}
