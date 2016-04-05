package com.messenger.delegate;

import com.messenger.entities.DataAttachment;
import com.messenger.entities.DataConversation;
import com.messenger.entities.DataMessage;
import com.messenger.messengerservers.constant.MessageStatus;
import com.messenger.messengerservers.model.Message;
import com.messenger.storage.dao.AttachmentDAO;
import com.messenger.storage.dao.ConversationsDAO;
import com.messenger.storage.dao.MessageDAO;
import com.techery.spares.module.Injector;
import com.techery.spares.module.qualifier.ForApplication;
import com.techery.spares.session.SessionHolder;
import com.worldventures.dreamtrips.core.session.UserSession;

import java.util.Calendar;
import java.util.List;

import javax.inject.Inject;

import dagger.Lazy;
import timber.log.Timber;

public class ChatMessagesEventDelegate {

    @Inject
    ConversationsDAO conversationsDAO;
    @Inject
    MessageDAO messageDAO;
    @Inject
    AttachmentDAO attachmentDAO;
    @Inject
    SessionHolder<UserSession> currentUserSession;
    //
    @Inject
    LoaderDelegate loaderDelegate;
    @Inject
    Lazy<ChatDelegate> chatDelegate;

    private final int maximumYear = Calendar.getInstance().getMaximum(Calendar.YEAR);

    @Inject
    public ChatMessagesEventDelegate(@ForApplication Injector injector) {
        injector.inject(this);
    }

    public void onReceivedMessage(Message message){
        conversationsDAO
                .getConversation(message.getConversationId())
                .take(1)
                .subscribe(conversation -> trySaveReceivedMessage(message, conversation));
    }

    public void onPreSendMessage(Message message){
        saveMessage(message, MessageStatus.SENDING);
    }

    public void onSendMessage(Message message) {
        long time;
        if (message.getStatus() == MessageStatus.ERROR) {
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.YEAR, maximumYear);
            time = calendar.getTimeInMillis();
        } else {
            time = System.currentTimeMillis();
        }
        messageDAO.updateStatus(message.getId(), message.getStatus(), time);
        conversationsDAO.updateDate(message.getConversationId(), time);
    }

    private void trySaveReceivedMessage(Message message, DataConversation conversationFromBD) {
        if (conversationFromBD == null) {
            String conversationId = message.getConversationId();
            String currentUserId = currentUserSession.get().get().getUser().getUsername();
            chatDelegate.get()
                    .createConversation(conversationId, currentUserId)
                    .flatMap(conversation -> {
                        conversationsDAO.save(conversation);
                        saveReceivedMessage(message);
                        return loaderDelegate.loadParticipants(conversationId);
                    }).subscribe(dataUsers -> {
            }, error -> Timber.d(error, ""));
        } else {
            saveReceivedMessage(message);
        }
    }

    private void saveReceivedMessage(Message message) {
        saveMessage(message, MessageStatus.SENT);
        conversationsDAO.incrementUnreadField(message.getConversationId());
    }

    private void saveMessage(Message message, @MessageStatus.Status int status) {
        long time = System.currentTimeMillis();
        message.setDate(time);
        message.setStatus(status);

        DataMessage dataMessage = new DataMessage(message);
        dataMessage.setSyncTime(time);

        List<DataAttachment> attachments = DataAttachment.fromMessage(message);
        if (!attachments.isEmpty()) attachmentDAO.save(attachments);

        messageDAO.save(dataMessage);
        conversationsDAO.updateDate(message.getConversationId(), time);
    }

}
