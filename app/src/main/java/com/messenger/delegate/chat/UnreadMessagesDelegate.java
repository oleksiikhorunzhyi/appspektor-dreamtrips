package com.messenger.delegate.chat;

import android.text.TextUtils;

import com.messenger.entities.DataConversation;
import com.messenger.entities.DataMessage;
import com.messenger.messengerservers.chat.Chat;
import com.messenger.messengerservers.constant.MessageStatus;
import com.messenger.storage.dao.ConversationsDAO;
import com.messenger.storage.dao.MessageDAO;
import com.techery.spares.session.SessionHolder;
import com.worldventures.dreamtrips.core.session.UserSession;

import javax.inject.Inject;

import rx.Observable;
import timber.log.Timber;

public class UnreadMessagesDelegate {

    private final MessageDAO messageDAO;
    private final ConversationsDAO conversationsDAO;
    private final SessionHolder<UserSession> sessionHolder;
    private final CreateChatHelper createChatHelper;
    private Observable<DataConversation> conversationObservable;
    private Observable<Chat> chatObservable;

    @Inject
    UnreadMessagesDelegate(CreateChatHelper createChatHelper, MessageDAO messageDAO,
                           ConversationsDAO conversationsDAO, SessionHolder<UserSession> sessionHolder) {
        this.createChatHelper = createChatHelper;
        this.messageDAO = messageDAO;
        this.conversationsDAO = conversationsDAO;
        this.sessionHolder = sessionHolder;
    }

    private String getUsername() {
        return sessionHolder.get().get().getUsername();
    }

    public void bind(String conversationId) {
        this.conversationObservable = conversationsDAO.getConversation(conversationId)
                .take(1)
                .cacheWithInitialCapacity(1);
        this.chatObservable = createChatHelper.createChat(conversationId)
                .replay(1)
                .autoConnect();
    }

    public void tryMarkAsReadMessage(DataMessage lastMessage) {
        if (lastMessage.getStatus() == MessageStatus.READ || TextUtils.equals(lastMessage.getFromId(), getUsername())) {
            return;
        }

        chatObservable
                .flatMap(chat -> chat.sendReadStatus(lastMessage.getId()))
                .flatMap(msgId -> markMessagesAsRead(lastMessage))
                .flatMap(this::changeUnreadCounter)
                .subscribe(count -> Timber.d("%s messages was marked"),
                        throwable -> Timber.e(throwable, "Error while marking message as read"));
    }

    private Observable<Integer> changeUnreadCounter(int markCount) {
        return conversationObservable
                .doOnNext(conversation -> {
                    conversation.setUnreadMessageCount(0);
                    conversationsDAO.save(conversation);
                })
                .map(conversation -> markCount);
    }

    private Observable<Integer> markMessagesAsRead(DataMessage sinceMessage) {
        return conversationObservable
                .flatMap(conversation -> messageDAO
                        .markMessagesAsRead(conversation.getId(), getUsername(), sinceMessage.getDate().getTime())
                );
    }
}
