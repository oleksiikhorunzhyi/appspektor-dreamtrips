package com.messenger.delegate.chat.message;

import javax.inject.Inject;

import io.techery.janet.ActionPipe;
import io.techery.janet.Janet;

public class ChatMessageInteractor {

    private final ActionPipe<ChatSendMessageCommand> sendMessagePipe;
    private final ActionPipe<RetrySendMessageCommand> resendMessagePipe;
    private final ActionPipe<MarkMessageAsReadCommand> markMessageAsReadPipe;

    @Inject
    public ChatMessageInteractor(Janet janet) {
        this.sendMessagePipe = janet.createPipe(ChatSendMessageCommand.class);
        this.resendMessagePipe = janet.createPipe(RetrySendMessageCommand.class);
        this.markMessageAsReadPipe = janet.createPipe(MarkMessageAsReadCommand.class);
    }

    public ActionPipe<ChatSendMessageCommand> getMessageActionPipe() {
        return sendMessagePipe;
    }

    public ActionPipe<RetrySendMessageCommand> getResendMessagePipe() {
        return resendMessagePipe;
    }

    public ActionPipe<MarkMessageAsReadCommand> getMarkMessageAsReadPipe() {
        return markMessageAsReadPipe;
    }
}
