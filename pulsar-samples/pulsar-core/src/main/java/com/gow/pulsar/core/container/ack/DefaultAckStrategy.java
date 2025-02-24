package com.gow.pulsar.core.container.ack;

import org.apache.pulsar.client.api.MessageId;

/**
 * @author gow
 * @date 2021/7/26
 */
public class DefaultAckStrategy extends BaseAckStrategy {
    @Override
    public void processCommits(MessageId messageId) {
        updateMessageId(messageId);
        commitIndividual();
    }

    @Override
    public void finalCommit() {

    }


    @Override
    void updateMessageId(MessageId messageId) {
        this.latestMessageId = messageId;
    }
}
