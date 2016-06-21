package com.messenger.delegate.chat;

import android.util.Pair;

import com.messenger.delegate.conversation.LoadConversationDelegate;
import com.messenger.entities.DataConversation;
import com.messenger.entities.DataMessage;
import com.messenger.messengerservers.constant.MessageStatus;
import com.messenger.messengerservers.model.DeletedMessage;
import com.messenger.messengerservers.model.Message;
import com.messenger.storage.dao.ConversationsDAO;
import com.messenger.storage.dao.MessageDAO;
import com.messenger.ui.helper.ConversationHelper;
import com.messenger.util.ChatDateUtils;
import com.messenger.util.DecomposeMessagesHelper;
import com.worldventures.dreamtrips.core.rx.composer.NonNullFilter;

import java.util.Collections;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import rx.Notification;
import rx.Observable;
import rx.observables.ConnectableObservable;
import rx.schedulers.Schedulers;
import rx.subjects.PublishSubject;
import timber.log.Timber;

@Singleton
public class ChatMessagesEventDelegate {

    private ConversationsDAO conversationsDAO;
    private MessageDAO messageDAO;
    private LoadConversationDelegate loadConversationDelegate;
    private DecomposeMessagesHelper decomposeMessagesHelper;

    private PublishSubject<Notification<DataMessage>> receivedSavedMessageStream = PublishSubject.create();

    @Inject
    public ChatMessagesEventDelegate(ConversationsDAO conversationsDAO, MessageDAO messageDAO,
                                     LoadConversationDelegate loadConversationDelegate,
                                     DecomposeMessagesHelper decomposeMessagesHelper) {
        this.conversationsDAO = conversationsDAO;
        this.messageDAO = messageDAO;
        this.loadConversationDelegate = loadConversationDelegate;
        this.decomposeMessagesHelper = decomposeMessagesHelper;
    }

    public void onReceivedMessage(Message message) {
        conversationsDAO
                .getConversation(message.getConversationId())
                .take(1)
                .subscribe(conversation -> trySaveReceivedMessage(message, conversation));
    }

    public void onPreSendMessage(Message message) {
        conversationsDAO.getConversation(message.getConversationId()).take(1)
                // to be on par with iOS app do not set status
                // SENDING to messages being resent to from abandoned conversations
                // so that "Not Delivered" would not disappear
                .map(conversation -> ConversationHelper.isAbandoned(conversation)
                        ? MessageStatus.ERROR : MessageStatus.SENDING)
                .subscribe(status -> saveMessage(message, status));
    }

    public void onSendMessage(Message message) {
        updateMessage(message, System.currentTimeMillis());
    }

    public void onErrorMessage(Message message) {
        updateMessage(message, ChatDateUtils.getErrorMessageDate());
    }

    public Observable<List<String>> onMessagesDeleted(List<DeletedMessage> deletedMessages) {
        ConnectableObservable<List<String>> observable = Observable.from(deletedMessages)
                .map(DeletedMessage::messageId)
                .toList()
                .doOnNext(messageDAO::deleteMessageByIds)
                .subscribeOn(Schedulers.io())
                .publish();
        observable.subscribe(deletedMessageIds -> {},
                e -> Timber.e(e, "Something went wrong while messages were deleting"));
        observable.connect();
        return observable;
    }

    private void updateMessage(Message message, long time) {
        messageDAO.updateStatus(message.getId(), message.getStatus(), time);
        conversationsDAO.updateDate(message.getConversationId(), time);
    }

    private void trySaveReceivedMessage(Message message, DataConversation conversationFromBD) {
        Observable.just(conversationFromBD)
                .compose(new NonNullFilter<>())
                .switchIfEmpty(loadConversationDelegate
                        .loadConversationFromNetworkAndRefreshFromDb(message.getConversationId()))
                .map(conversation -> message)
                .subscribe(this::saveReceivedMessage);
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
