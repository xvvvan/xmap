package com.xvvvan.global;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.*;

public class VirtualThreadPool {
    public static ExecutorService executorService = Executors.newVirtualThreadPerTaskExecutor();
    public static Semaphore limit = new Semaphore(108);
    private static final Logger logger = LoggerFactory.getLogger(VirtualThreadPool.class);

    public static <T> Future<T> submit(Callable<T> task){
        try{
            limit.acquire(); // 尝试通过信号量获取执行许可
        } catch(InterruptedException e){
            logger.error(e.getMessage());
            // 执行许可获取失败的异常处理
        }

        try {
            // 获取到执行许可，这里是使用虚拟线程执行任务的逻辑
            return executorService.submit(task);
        } finally {
            // 释放信号量
            limit.release();
        }
    }



    public static void execute(Runnable task) {
        try{
            limit.acquire(); // 尝试通过信号量获取执行许可
        } catch(InterruptedException e){
            logger.error(e.getMessage());
            // 执行许可获取失败的异常处理
        }

        try {
            // 获取到执行许可，这里是使用虚拟线程执行任务的逻辑
            executorService.execute(task);
        } finally {
            // 释放信号量
            limit.release();
        }
    }
}
