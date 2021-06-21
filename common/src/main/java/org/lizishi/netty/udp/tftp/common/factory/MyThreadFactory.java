package org.lizishi.netty.udp.tftp.common.factory;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Lzs
 * @date 2021/6/21 20:48
 * @description 线程工厂,自定义线程名
 */
public class MyThreadFactory implements ThreadFactory {
    private static AtomicInteger threadId = new AtomicInteger(0);

    @Override
    public Thread newThread(Runnable runnable) {
        Thread thread = new Thread(runnable);
        thread.setName("tftp-thread-" + threadId.getAndIncrement());

        return thread;
    }
}