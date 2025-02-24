package com.gow.aop.proxyfactory.advice.exception;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.stream.Collectors;
import org.springframework.aop.ThrowsAdvice;

/**
 * @author gow
 * @date 2021/7/22 0022
 */
public class SendMsgThrowsAdvice implements ThrowsAdvice {
    //注意方法名称必须为afterThrowing
    public void afterThrowing(Method method, Object[] args, Object target, RuntimeException e) {
        //监控到异常后发送消息通知开发者
        System.out.println("异常警报：");
        System.out.println(String.format("method:[%s]，args:[%s]", method.toGenericString(),
                Arrays.stream(args).collect(Collectors.toList())));
        System.out.println(e.getMessage());
        System.out.println("请尽快修复bug！");
    }
}