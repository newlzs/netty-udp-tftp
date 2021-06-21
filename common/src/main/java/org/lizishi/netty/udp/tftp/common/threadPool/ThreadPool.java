package org.lizishi.netty.udp.tftp.common.threadPool;

import cn.hutool.core.util.ObjectUtil;
import org.lizishi.netty.udp.tftp.common.factory.MyThreadFactory;

import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author Lzs
 * @date 2021/6/21 20:32
 * @description
 */
public class ThreadPool {
    private static int processor = Runtime.getRuntime().availableProcessors();
    private static ThreadPoolExecutor executor;

    private static int corePoolSize = processor * 2;
    private static int maximumPoolSize = processor * 4;
    private static Long keepAliveTime = 10L;
    private static int blockQueueSize = 10;

    public synchronized static ThreadPoolExecutor getInstance() {
        if(ObjectUtil.isNull(executor)) {
            executor = new ThreadPoolExecutor(corePoolSize, maximumPoolSize, keepAliveTime, TimeUnit.MINUTES, new LinkedBlockingDeque<>(blockQueueSize), new MyThreadFactory());
        }

        return executor;
    }
}