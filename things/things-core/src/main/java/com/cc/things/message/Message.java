package com.cc.things.message;

import com.alibaba.fastjson.parser.ParserConfig;
import com.alibaba.fastjson.util.TypeUtils;
import org.apache.commons.collections4.MapUtils;

import javax.annotation.Nullable;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Supplier;

import static com.cc.things.message.MessageType.UNKNOWN;

/**
 * 消息
 * wcc 2022/6/4
 */
public interface Message {

    default MessageType getMessageType() {
        return UNKNOWN;
    }

    /**
     * 消息的唯一标识,用于在请求响应模式下对请求和响应进行关联.
     * <p>
     * 注意: 此消息ID为全系统唯一. 但是在很多情况下,设备可能不支持此类型的消息ID,
     * 此时需要在协议包中做好映射关系,比如使用:{@link java.util.concurrent.ConcurrentHashMap}进行消息绑定.
     *
     * @return 消息ID
     */
    String getMessageId();

    /**
     * @return 毫秒时间戳
     * @see System#currentTimeMillis()
     */
    long getTimestamp();

    /**
     * 消息头,用于自定义一些消息行为, 默认的一些消息头请看:{@link Headers}
     *
     * @return headers or null
     * @see Headers
     */
    @Nullable
    Map<String, Object> getHeaders();

    /**
     * 添加一个header
     *
     * @param header header
     * @param value  value
     * @return this
     * @see Headers
     */
    Message addHeader(String header, Object value);

    /**
     * 添加header,如果header已存在则放弃
     *
     * @param header header key
     * @param value  header 值
     * @return this
     */
    Message addHeaderIfAbsent(String header, Object value);


    /**
     * 删除一个header
     *
     * @param header header
     * @return this
     * @see Headers
     */
    Message removeHeader(String header);

    /**
     * @see Headers
     * @see Message#addHeader(String, Object)
     */
    default <T> Message addHeader(HeaderKey<T> header, T value) {
        return addHeader(header.getKey(), value);
    }

    /**
     * @see Headers
     * @see Message#addHeaderIfAbsent(String, Object)
     */
    default <T> Message addHeaderIfAbsent(HeaderKey<T> header, T value) {
        return addHeaderIfAbsent(header.getKey(), value);
    }

    default <T> T getOrAddHeader(HeaderKey<T> header, Supplier<T> value) {
        return this.computeHeader(header, (ignore, old) -> {
            if (old == null) {
                old = value.get();
            }
            return old;
        });
    }

    default <T> T getOrAddHeaderDefault(HeaderKey<T> header) {
        return getOrAddHeader(header, header::getDefaultValue);
    }

    @SuppressWarnings("all")
    default <T> Optional<T> getHeader(HeaderKey<T> key) {
        return Optional.ofNullable(getHeaderOrElse(key, null));
    }

    default <T> T getHeaderOrDefault(HeaderKey<T> key) {
        return getHeaderOrElse(key, key::getDefaultValue);
    }

    @SuppressWarnings("all")
    default <T> T getHeaderOrElse(HeaderKey<T> header, @Nullable Supplier<T> orElse) {
        Object val = getHeaderOrElse(header.getKey(), null);
        if (null == val) {
            return orElse == null ? null : orElse.get();
        }
        return TypeUtils.cast(val, header.getType(), ParserConfig.global);
    }

    default Object getHeaderOrElse(String header, @Nullable Supplier<Object> orElse) {
        Map<String, Object> headers = getHeaders();
        if (MapUtils.isEmpty(headers) || header == null) {
            return orElse == null ? null : orElse.get();
        }
        Object val = headers.get(header);
        if (val != null) {
            return val;
        }
        return orElse == null ? null : orElse.get();
    }

    default Optional<Object> getHeader(String header) {
        return Optional.ofNullable(getHeaderOrElse(header, null));
    }

    Object computeHeader(String key, BiFunction<String, Object, Object> computer);

    @SuppressWarnings("all")
    default <T> T computeHeader(HeaderKey<T> key, BiFunction<String, T, T> computer) {
        return (T) computeHeader(key.getKey(),
                (str, old) -> computer.apply(str, old == null ? null : TypeUtils.cast(old, key.getType(), ParserConfig.global)));
    }
}
