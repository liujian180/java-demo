package com.gow.validate.pojo.common;

import lombok.experimental.Delegate;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

/**
 * @author wujt  2021/5/18
 */
public class ValidationList<E> implements List<E> {

    /**
     * 一定要加@Valid注解
     */
    @Delegate
    @Valid
    public List<E> list = new ArrayList<>();

    @Override
    public String toString() {
        return list.toString();
    }

    public static void main(String[] args) {
        ValidationList<String> objects = new ValidationList<>();
        objects.add("name");
    }
}
