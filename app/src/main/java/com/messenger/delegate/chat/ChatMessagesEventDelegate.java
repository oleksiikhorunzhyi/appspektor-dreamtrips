package com.messenger.delegate.chat;

import com.messenger.delegate.LoadConversationDelegate;
import com.messenger.entities.DataConversation;
import com.messenger.entities.DataMessage;
import com.messenger.messengerservers.constant.MessageStatus;
import com.messenger.messengerservers.model.Message;
import com.messenger.storage.dao.ConversationsDAO;
import com.messenger.storage.dao.MessageDAO;
import com.messenger.util.ChatDateUtils;
import com.messenger.util.DecomposeMessagesHelper;
import com.techery.spares.module.Injector;
import com.techery.spares.module.qualifier.ForApplication;

import java.util.Collections;

import javax.inject.Inject;

import rx.Notification;
import rx.subjects.PublishSubject;
import timber.log.Timber;

public class ChatMessagesEventDelegate {

    @Inject
    ConversationsDAO conversationsDAO;
    @Inject
    MessageDAO messageDAO;
    //
    @Inject
    LoadConversationDelegate loadConversationDelegate;
    @Inject
    DecomposeMessagesHelper decomposeMessagesHelper;

    private PublishSubject<Notification<DataMessage>> receivedSavedMessageStream = PublishSubject.create();

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
        updateMessage(message, System.currentTimeMillis());
    }

    public void onErrorMessage(Message message) {
        updateMessage(message, ChatDateUtils.getErrorMessageDate());
    }

    private void updateMessage(Message message, long time){
        messageDAO.updateStatus(message.getId(), message.getStatus(), time);
        conversationsDAO.updateDate(message.getConversationId(), time);
    }

    private void trySaveReceivedMessage(Message message, DataConversation conversationFromBD) {
        if (conversationFromBD == null) {
            String conversationId = message.getConversationId();
            loadConversationDelegate.loadConversationFromNetwork(conversationId)
                    .subscribe(dataConversation -> saveReceivedMessage(message));
        } else {
            saveReceivedMessage(message);
        }
    }

    private void saveReceivedMessage(Message message) {
        saveMessage(message, MessageStatus.SENT);
        conversationsDAO.incrementUnreadField(message.getConversationId());
        messageDAO.getMessage(message.getId()).take(1).subscribe(dataMessage -> {
            receivedSavedMessageStream.onNext(Notification.createOnNext(dataMessage));
        }, e -> {
            Timber.e("Could not get previously processed and saved message");
            receivedSavedMessageStream.onNext(Notification.createOnError(e));
        });
    }

    private void saveMessage(Message message, @MessageStatus.Status int status) {
        long time = System.currentTimeMillis();
        message.setDate(time);
        message.setStatus(status);

        DecomposeMessagesHelper.Result result =
                decomposeMessagesHelper.decomposeMessages(Collections.singletonList((message)));

        result.messages.get(0).setSyncTime(time);

        decomposeMessagesHelper.saveDecomposeMessage(result);
        conversationsDAO.updateDate(message.getConversationId(), time);
    }

    public PublishSubject<Notification<DataMessage>> getReceivedSavedMessageStream() {
        return receivedSavedMessageStream;
    }
}
