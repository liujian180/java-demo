package com.cc.netwok.message;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.nio.charset.StandardCharsets;

/**
 * 已编码的消息,通常为来自设备或者发向设备的原始报文.
 * wcc 2022/6/29
 */
public interface EncodedMessage {
    /**
     * 获取原始报文
     *
     * @return ByteBuf
     */
    @Nonnull
    ByteBuf getPayload();

    default String payloadAsString() {
        return getPayload().toString(StandardCharsets.UTF_8);
    }

    default JSONObject payloadAsJson() {
        return (JSONObject) JSON.parse(payloadAsBytes());
    }

    default JSONArray payloadAsJsonArray() {
        return (JSONArray) JSON.parse(payloadAsBytes());
    }

    default byte[] payloadAsBytes() {
        return ByteBufUtil.getBytes(getPayload());
    }

    @Deprecated
    default byte[] getBytes() {
        return ByteBufUtil.getBytes(getPayload());
    }

    default byte[] getBytes(int offset, int len) {
        return ByteBufUtil.getBytes(getPayload(), offset, len);
    }

    @Nullable
    @Deprecated
    default MessagePayloadType getPayloadType() {
        return MessagePayloadType.JSON;
    }

    static EmptyMessage empty() {
        return EmptyMessage.INSTANCE;
    }

    static EncodedMessage simple(ByteBuf data) {
        return simple(data, MessagePayloadType.BINARY);
    }

    static EncodedMessage simple(ByteBuf data, MessagePayloadType payloadType) {
        return SimpleEncodedMessage.of(data, payloadType);
    }

}
