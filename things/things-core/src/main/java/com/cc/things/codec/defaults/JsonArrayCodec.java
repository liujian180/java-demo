package com.cc.things.codec.defaults;

import com.alibaba.fastjson.JSON;
import com.cc.things.codec.Codec;
import com.cc.things.codec.Payload;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.function.Function;
/**
 * list 类型编解码器
 * wcc 2022/6/4
 */
public class JsonArrayCodec<T, R> implements Codec<R> {

    private final Class<T> type;

    private final Class<R> resultType;

    private final Function<List<T>, R> mapper;

    private JsonArrayCodec(Class<T> type, Class<R> resultType, Function<List<T>, R> mapper) {
        this.type = type;
        this.resultType = resultType;
        this.mapper = mapper;
    }

    @SuppressWarnings("all")
    public static <T> JsonArrayCodec<T, List<T>> of(Class<T> type) {
        return JsonArrayCodec.of(type, (Class) List.class, Function.identity());
    }

    public static <T, R> JsonArrayCodec<T, R> of(Class<T> type, Class<R> resultType, Function<List<T>, R> function) {
        return new JsonArrayCodec<>(type, resultType, function);
    }

    @Override
    public Class<R> forType() {
        return resultType;
    }

    @Override
    public R decode(@Nonnull Payload payload) {
        return mapper.apply(JSON.parseArray(payload.bodyToString(false), type));
    }

    @Override
    public Payload encode(R body) {
        return Payload.of(JSON.toJSONBytes(body));
    }


}
