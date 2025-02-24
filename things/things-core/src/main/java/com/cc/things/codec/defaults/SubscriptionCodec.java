package com.cc.things.codec.defaults;

import com.cc.bus.event.Subscription;
import com.cc.things.codec.Codec;
import com.cc.things.codec.Payload;
import com.cc.util.EnumDict;
import com.gow.codec.bytes.BytesUtils;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.nio.charset.StandardCharsets;

public class SubscriptionCodec implements Codec<Subscription> {

    public static final SubscriptionCodec INSTANCE = new SubscriptionCodec();

    @Override
    public Class<Subscription> forType() {
        return Subscription.class;
    }

    @Nullable
    @Override
    public Subscription decode(@Nonnull Payload payload) {
        ByteBuf body = payload.getBody();

        byte[] subscriberLenArr = new byte[4];
        body.getBytes(0, subscriberLenArr);
        int subscriberLen = BytesUtils.beToInt(subscriberLenArr);

        byte[] subscriber = new byte[subscriberLen];
        body.getBytes(4, subscriber);
        String subscriberStr = new String(subscriber);

        byte[] featureBytes = new byte[8];
        body.getBytes(4 + subscriberLen, featureBytes);
        Subscription.Feature[] features = EnumDict
                .getByMask(Subscription.Feature.class, BytesUtils.beToLong(featureBytes, 0, featureBytes.length))
                .toArray(new Subscription.Feature[0]);

        int headerLen = 12 + subscriberLen;
        body.resetReaderIndex();
        return Subscription.of(subscriberStr, body.slice(headerLen, body.readableBytes() - headerLen)
                .toString(StandardCharsets.UTF_8)
                .split("[\t]"), features);
    }

    @Override
    public Payload encode(Subscription body) {

        // bytes 4
        byte[] subscriber = body.getSubscriber().getBytes();
        byte[] subscriberLen = BytesUtils.intToBe(subscriber.length);

        // bytes 8
        long features = EnumDict.toMask(body.getFeatures());
        byte[] featureBytes = BytesUtils.longToBe(features);

        byte[] topics = String.join("\t", body.getTopics()).getBytes();

        return Payload.of(Unpooled
                .buffer(subscriberLen.length + subscriber.length + featureBytes.length + topics.length)
                .writeBytes(subscriberLen)
                .writeBytes(subscriber)
                .writeBytes(featureBytes)
                .writeBytes(topics));
    }
}
