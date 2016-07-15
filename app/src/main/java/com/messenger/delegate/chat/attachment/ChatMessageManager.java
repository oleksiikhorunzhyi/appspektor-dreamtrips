package com.messenger.delegate.chat.attachment;

import android.location.Location;

import com.messenger.delegate.chat.message.ChatMessageInteractor;
import com.messenger.delegate.chat.message.ChatSendMessageCommand;
import com.messenger.delegate.chat.message.MarkMessageAsReadCommand;
import com.messenger.delegate.chat.message.RetrySendMessageCommand;
import com.messenger.entities.DataAttachment;
import com.messenger.entities.DataMessage;
import com.messenger.messengerservers.constant.AttachmentType;
import com.messenger.storage.dao.AttachmentDAO;

import java.util.List;

import javax.inject.Inject;

public class ChatMessageManager {

    private final PhotoAttachmentDelegate photoAttachmentDelegate;
    private final LocationAttachmentDelegate locationAttachmentDelegate;
    private final ChatMessageInteractor chatMessageInteractor;

    private final AttachmentDAO attachmentDAO;

    @Inject
    public ChatMessageManager(ChatMessageInteractor chatMessageInteractor,
                              PhotoAttachmentDelegate photoAttachmentDelegate,
                              LocationAttachmentDelegate locationAttachmentDelegate,
                              AttachmentDAO attachmentDAO) {
        this.chatMessageInteractor = chatMessageInteractor;
        this.photoAttachmentDelegate = photoAttachmentDelegate;
        this.locationAttachmentDelegate = locationAttachmentDelegate;
        this.attachmentDAO = attachmentDAO;
    }

    public void sendMessage(String conversationId, String messageText) {
        chatMessageInteractor.getMessageActionPipe()
                .send(new ChatSendMessageCommand(conversationId, messageText));
    }

    public void markMessagesAsRead(DataMessage lastSeenMessage, String conversationId) {
        chatMessageInteractor.getMarkMessageAsReadPipe()
                .send(new MarkMessageAsReadCommand(lastSeenMessage, conversationId));
    }

    public void sendImages(String conversationId, List<String> filePaths) {
        photoAttachmentDelegate.sendImages(conversationId, filePaths);
    }

    public void sendLocation(String conversationId, Location location) {
        locationAttachmentDelegate.send(conversationId, location);
    }

    public void retrySendMessage(String conversationId, DataMessage failedMessage) {
        attachmentDAO.getAttachmentByMessageId(failedMessage.getId())
                .take(1)
                .subscribe(dataAttachment -> {
                    if (dataAttachment != null) {
                        retrySendAttachment(conversationId, failedMessage, dataAttachment);
                    } else chatMessageInteractor.getResendMessagePipe()
                            .send(new RetrySendMessageCommand(failedMessage));
                });
    }

    public void retrySendAttachment(String conversationId, DataMessage dataMessage, DataAttachment attachment) {
        switch (attachment.getType()) {
            case AttachmentType.IMAGE:
                retrySendPhotoAttachment(conversationId, dataMessage, attachment);
                break;
            case AttachmentType.LOCATION:
                retrySendLocationAttachment(conversationId, dataMessage, attachment);
                break;
        }
    }

    private void retrySendPhotoAttachment(String conversationId, DataMessage dataMessage, DataAttachment dataAttachment) {
        photoAttachmentDelegate.retry(conversationId, dataMessage, dataAttachment);
    }

    private void retrySendLocationAttachment(String conversationId, DataMessage dataMessage, DataAttachment dataAttachment) {
        locationAttachmentDelegate.retry(conversationId, dataMessage, dataAttachment);
    }

}
