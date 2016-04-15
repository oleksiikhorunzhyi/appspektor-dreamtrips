package com.messenger.delegate;

import com.messenger.entities.DataConversation;
import com.messenger.entities.DataMessage;
import com.messenger.messengerservers.constant.MessageStatus;
import com.messenger.messengerservers.model.Message;
import com.messenger.storage.dao.ConversationsDAO;
import com.messenger.storage.dao.MessageDAO;
import com.messenger.util.DecomposeMessagesHelper;
import com.techery.spares.module.Injector;
import com.techery.spares.module.qualifier.ForApplication;
import com.techery.spares.session.SessionHolder;
import com.worldventures.dreamtrips.core.session.UserSession;

import java.util.Calendar;
import java.util.Collections;

import javax.inject.Inject;

import dagger.Lazy;
import rx.Notification;
import rx.subjects.PublishSubject;
import timber.log.Timber;

public class ChatMessagesEventDelegate {

    @Inject
    ConversationsDAO conversationsDAO;
    @Inject
    MessageDAO messageDAO;
    @Inject
    SessionHolder<UserSession> currentUserSession;
    //
    @Inject
    LoaderDelegate loaderDelegate;
    @Inject

    DecomposeMessagesHelper decomposeMessagesHelper;

    Lazy<CreateConversationHelper> createConversationHelperLazy;

    private final int maximumYear = Calendar.getInstance().getMaximum(Calendar.YEAR);

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
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, maximumYear);
        updateMessage(message, calendar.getTimeInMillis());
    }

    private void updateMessage(Message message, long time){
        messageDAO.updateStatus(message.getId(), message.getStatus(), time);
        conversationsDAO.updateDate(message.getConversationId(), time);
    }

    private void trySaveReceivedMessage(Message message, DataConversation conversationFromBD) {
        if (conversationFromBD == null) {
            String conversationId = message.getConversationId();
            String currentUserId = currentUserSession.get().get().getUser().getUsername();
            createConversationHelperLazy.get()
                    .createConversation(conversationId, currentUserId)
                    .flatMap(conversation -> {
                        conversationsDAO.save(conversation);
                        saveReceivedMessage(message);
                        return loaderDelegate.loadParticipants(conversationId);
                    }).subscribe(dataUsers -> {},
                    error -> Timber.d(error, ""));
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
