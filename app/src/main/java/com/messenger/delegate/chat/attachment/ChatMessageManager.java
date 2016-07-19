package com.messenger.delegate.chat.attachment;

import android.location.Location;

import com.messenger.delegate.chat.message.ChatMessageInteractor;
import com.messenger.delegate.chat.message.ChatSendMessageCommand;
import com.messenger.delegate.chat.message.RetrySendMessageCommand;
import com.messenger.entities.DataAttachment;
import com.messenger.entities.DataConversation;
import com.messenger.entities.DataMessage;
import com.messenger.messengerservers.ConnectionStatus;
import com.messenger.messengerservers.constant.AttachmentType;
import com.messenger.messengerservers.constant.MessageStatus;
import com.messenger.storage.dao.AttachmentDAO;
import com.messenger.storage.dao.ConversationsDAO;
import com.messenger.storage.dao.MessageDAO;
import com.messenger.synchmechanism.MessengerConnector;
import com.messenger.ui.helper.ConversationHelper;
import com.messenger.util.ChatDateUtils;

import org.apache.commons.lang3.tuple.ImmutableTriple;

import java.util.List;

import javax.inject.Inject;

import rx.Observable;

public class ChatMessageManager {

    private final PhotoAttachmentDelegate photoAttachmentDelegate;
    private final LocationAttachmentDelegate locationAttachmentDelegate;
    private final ChatMessageInteractor chatMessageInteractor;

    private final MessengerConnector messengerConnector;
    private final AttachmentDAO attachmentDAO;
    private final MessageDAO messageDAO;
    private final ConversationsDAO conversationsDAO;

    @Inject
    public ChatMessageManager(MessengerConnector messengerConnector, ChatMessageInteractor chatMessageInteractor,
                              PhotoAttachmentDelegate photoAttachmentDelegate,
                              LocationAttachmentDelegate locationAttachmentDelegate,
                              ConversationsDAO conversationsDAO, MessageDAO messageDAO, AttachmentDAO attachmentDAO) {
        this.messengerConnector = messengerConnector;
        this.chatMessageInteractor = chatMessageInteractor;
        this.photoAttachmentDelegate = photoAttachmentDelegate;
        this.locationAttachmentDelegate = locationAttachmentDelegate;
        this.conversationsDAO = conversationsDAO;
        this.attachmentDAO = attachmentDAO;
        this.messageDAO = messageDAO;
    }

    public void sendMessage(String conversationId, String messageText) {
        chatMessageInteractor.getMessageActionPipe()
                .send(new ChatSendMessageCommand(conversationId, messageText));
    }

    public void sendImages(String conversationId, List<String> filePaths) {
        photoAttachmentDelegate.sendImages(conversationId, filePaths);
    }

    public void sendLocation(String conversationId, Location location) {
        locationAttachmentDelegate.send(conversationId, location);
    }

    public void retrySendMessage(String conversationId, DataMessage failedMessage) {
        Observable.combineLatest(
                attachmentDAO.getAttachmentByMessageId(failedMessage.getId()).take(1),
                conversationsDAO.getConversation(conversationId).take(1),
                messengerConnector.getAuthToServerStatus().take(1), ImmutableTriple::new)
                .subscribe(dataTriple -> retrySendMessage(dataTriple.middle, failedMessage, dataTriple.left, dataTriple.right));
    }

    public void retrySendMessage(DataConversation dataConversation, DataMessage failedMessage, DataAttachment dataAttachment, ConnectionStatus authStatus) {
        // todo move this logic to janet command
        if (authStatus != ConnectionStatus.CONNECTED || ConversationHelper.isAbandoned(dataConversation)) {
            messageDAO.updateStatus(failedMessage.getId(), MessageStatus.ERROR, ChatDateUtils.getErrorMessageDate());
            return;
        }

        if (dataAttachment != null) {
            retrySendAttachment(dataConversation.getId(), failedMessage, dataAttachment);
        } else chatMessageInteractor.getResendMessagePipe()
                .send(new RetrySendMessageCommand(failedMessage));
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
