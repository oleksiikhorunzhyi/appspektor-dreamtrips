package com.messenger.delegate.chat.attachment;

import android.location.Location;

import com.messenger.delegate.chat.attachment.LocationAttachmentDelegate;
import com.messenger.delegate.chat.attachment.PhotoAttachmentDelegate;
import com.messenger.delegate.chat.message.ChatMessageDelegate;
import com.messenger.entities.DataAttachment;
import com.messenger.entities.DataMessage;
import com.messenger.messengerservers.constant.AttachmentType;
import com.messenger.storage.dao.AttachmentDAO;
import com.messenger.storage.dao.LocationDAO;
import com.messenger.storage.dao.PhotoDAO;

import java.util.List;

import javax.inject.Inject;

import timber.log.Timber;

public class ChatMessageManager {

    private final PhotoAttachmentDelegate photoAttachmentDelegate;
    private final LocationAttachmentDelegate locationAttachmentDelegate;
    private final ChatMessageDelegate chatMessageDelegate;

    private final AttachmentDAO attachmentDAO;

    @Inject
    public ChatMessageManager(ChatMessageDelegate chatMessageDelegate,
                              PhotoAttachmentDelegate photoAttachmentDelegate,
                              LocationAttachmentDelegate locationAttachmentDelegate,
                              AttachmentDAO attachmentDAO) {
        this.chatMessageDelegate = chatMessageDelegate;
        this.photoAttachmentDelegate = photoAttachmentDelegate;
        this.locationAttachmentDelegate = locationAttachmentDelegate;
        this.attachmentDAO = attachmentDAO;
    }

    public void sendMessage(String conversationId, String messageText) {
        chatMessageDelegate.sendMessage(conversationId, messageText);
    }

    public void sendImages(String conversationId, List<String> filePaths) {
        photoAttachmentDelegate.sendImages(conversationId, filePaths);
    }

    public void sendLocation(String conversationId, Location location) {
        locationAttachmentDelegate.send(conversationId, location);
    }

    public void retrySendMessage(String conversationId, DataMessage dataMessage) {
        attachmentDAO.getAttachmentByMessageId(dataMessage.getId())
                .take(1)
                .subscribe(dataAttachment -> {
                    if (dataAttachment != null) {
                        retrySendAttachment(conversationId, dataMessage, dataAttachment);
                    } else chatMessageDelegate.retrySendMessage(conversationId, dataMessage);
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
