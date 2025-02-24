package com.wujt.并发编程.threadlocal;

import com.alibaba.ttl.TransmittableThreadLocal;
import com.alibaba.ttl.threadpool.TtlExecutors;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author wujt
 */
public class TransmittableThreadLocalDemo {

    /**
     * 模拟Control任务
     */
    static class ControlThread1 implements Runnable {
        private int i;

        public ControlThread1(int i) {
            this.i = i;
        }

        @Override
        public void run() {
            System.out.println(Thread.currentThread().getName() + ":" + i);
            transmittableThreadLocal.set(i);
            //使用线程池异步处理任务
            ttlExecutorService.submit(new BusinessTask1(Thread.currentThread().getName()));
        }
    }

    /**
     * 业务任务，主要是模拟在Control控制层，提交任务到线程池执行
     */
    static class BusinessTask1 implements Runnable {
        private String parentThreadName;

        public BusinessTask1(String parentThreadName) {
            this.parentThreadName = parentThreadName;
        }

        @Override
        public void run() {
            //如果与上面的能对应上来，则说明正确，否则失败
            System.out.println("parentThreadName:" + parentThreadName + ":" + transmittableThreadLocal.get());
        }
    }

    /**
     * 模拟tomcat线程池
     */
    private static ExecutorService tomcatExecutors = Executors.newFixedThreadPool(10);
    /**
     * 业务线程池，默认Control中异步任务执行线程池
     */
    private static ExecutorService ttlExecutorService = TtlExecutors.getTtlExecutorService(Executors.newFixedThreadPool(4)); // 使用ttl线程池，该框架的使用，请查阅官方文档。


    /**
     * 线程上下文环境，模拟在Control这一层，设置环境变量，然后在这里提交一个异步任务，模拟在子线程中，是否可以访问到刚设置的环境变量值。
     */
    private static TransmittableThreadLocal<Integer> transmittableThreadLocal = new TransmittableThreadLocal<>();

    public static void main(String[] args) {
        /**
         * TransmittableThreadLocal
         * 原理： 在线程池中对线程重用中；显示对线程环境进行备份；然后再进行拷贝使用；使用完后又进行恢复
         */
        System.out.println("start TransmittableThreadLocal:");
        transmittableThreadLocalTest();
    }

    private static void transmittableThreadLocalTest() {
        for (int i = 0; i < 10; i++) {  // 模式10个请求，每个请求执行ControlThread的逻辑，其具体实现就是，先输出父线程的名称，
            //  然后设置本地环境变量，并将父线程名称传入到子线程中，在子线程中尝试获取在父线程中的设置的环境变量
            tomcatExecutors.submit(new ControlThread1(i));
        }

        //简单粗暴的关闭线程池
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        ttlExecutorService.shutdown();
        tomcatExecutors.shutdown();
    }

}
