package com.gow.aop.proxyfactory.advice.exception;

import com.gow.aop.proxyfactory.advice.FundsService;
import org.springframework.aop.framework.ProxyFactory;

/**
 * @author gow
 * @date 2021/7/22 0022
 */
public class ThrowsAdviceTest {

    public static void main(String[] args) {
        //代理工厂
        ProxyFactory proxyFactory = new ProxyFactory(new FundsService());
        //添加一个异常通知，发现异常之后发送消息给开发者尽快修复bug
        proxyFactory.addAdvice(new SendMsgThrowsAdvice());
        //通过代理工厂创建代理
        FundsService proxy = (FundsService) proxyFactory.getProxy();
        //调用代理的方法
        proxy.cashOut("路人", 2000);
    }
}
