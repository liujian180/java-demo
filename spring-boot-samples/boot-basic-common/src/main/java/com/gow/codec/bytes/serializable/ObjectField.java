package com.gow.codec.bytes.serializable;

import com.gow.codec.bytes.DataType;

import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.TYPE;

@Target({FIELD, TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface ObjectField {

    // 数据类型
    DataType dataType() default DataType.OBJECT;

    // 动态根据属性获取属性的类型,此方法为无参方法,使用场景为对象属性动态由其他属性决定
    String classMethod() default "";

    // 反序列化：List、数组的长度字段，利用反射调用
    // 序列化时：标识长度字段为field，不在进行loop处理
    String loopFieldName() default "";

    // 反序列化：loopFieldName为空时，负数表示变长模式，整数和0表示定长模式
    // 序列化时：整数和0表示定长模式；负数表示变长模式，需要先计算长度字节
    int loop() default -1;

    // String:动态字符串长度属性
    int length() default 0;

}
