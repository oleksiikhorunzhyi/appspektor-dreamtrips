package com.messenger.delegate;

import com.messenger.delegate.command.SendImageAttachmentCommand;
import com.messenger.entities.DataAttachment;
import com.messenger.entities.DataConversation;
import com.messenger.entities.DataMessage;
import com.messenger.messengerservers.constant.AttachmentType;
import com.messenger.messengerservers.constant.MessageStatus;
import com.techery.spares.session.SessionHolder;
import com.worldventures.dreamtrips.core.session.UserSession;

import java.util.Date;
import java.util.UUID;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.techery.janet.ActionPipe;
import io.techery.janet.Janet;
import io.techery.janet.ReadOnlyActionPipe;

@Singleton
public class AttachmentDelegate {
    private final ActionPipe<SendImageAttachmentCommand> sendImagePipe;
    private final ReadOnlyActionPipe<SendImageAttachmentCommand> readSendImagePipe;

    private final SessionHolder<UserSession> sessionHolder;

    @Inject
    public AttachmentDelegate(SessionHolder<UserSession> sessionHolder, Janet janet) {
        this.sessionHolder = sessionHolder;

        this.sendImagePipe = janet.createPipe(SendImageAttachmentCommand.class);
        this.readSendImagePipe = sendImagePipe.asReadOnly();
    }

    public void retry(DataConversation conversation, DataMessage message, DataAttachment attachment) {
        sendImagePipe.send(new SendImageAttachmentCommand(conversation, attachment.getUrl(), message, attachment));
    }

    public void send(DataConversation conversation, String filePath) {
        DataMessage emptyMessage = createEmptyMessage(conversation.getId());
        sendImagePipe.send(new SendImageAttachmentCommand(conversation, filePath, emptyMessage, createDataAttachment(emptyMessage)));
    }

    public ReadOnlyActionPipe<SendImageAttachmentCommand> getReadSendImagePipe() {
        return readSendImagePipe;
    }

    private DataMessage createEmptyMessage(String conversationId) {
        return new DataMessage.Builder()
                .conversationId(conversationId)
                .from(sessionHolder.get().get().getUsername())
                .id(UUID.randomUUID().toString())
                .date(new Date(System.currentTimeMillis()))
                .status(MessageStatus.SENDING)
                .syncTime(System.currentTimeMillis())
                .build();
    }

    private DataAttachment createDataAttachment(DataMessage message) {
        return new DataAttachment.Builder()
                .conversationId(message.getConversationId())
                .messageId(message.getId())
                .type(AttachmentType.IMAGE)
                .build();
    }
}
