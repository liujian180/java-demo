package com.cc.netwok.message;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.nio.charset.StandardCharsets;

/**
 * wcc 2022/6/29
 */
@Data
@AllArgsConstructor
public class SimpleEncodedMessage implements EncodedMessage {

    private final ByteBuf payload;

    private final MessagePayloadType payloadType;

    public static SimpleEncodedMessage of(ByteBuf byteBuf, MessagePayloadType payloadType) {
        return new SimpleEncodedMessage(byteBuf, payloadType);
    }

    @Override
    public String toString() {

        StringBuilder builder = new StringBuilder();

        if (ByteBufUtil.isText(payload, StandardCharsets.UTF_8)) {
            builder.append(payload.toString(StandardCharsets.UTF_8));
        } else {
            ByteBufUtil.appendPrettyHexDump(builder, payload);
        }
        return builder.toString();
    }
}
