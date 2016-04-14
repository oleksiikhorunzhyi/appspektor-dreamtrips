package com.messenger.delegate;

import android.text.TextUtils;

import com.messenger.entities.DataConversation;
import com.messenger.entities.DataMessage;
import com.messenger.messengerservers.chat.Chat;
import com.messenger.messengerservers.constant.MessageStatus;
import com.messenger.messengerservers.model.Message;
import com.messenger.storage.dao.ConversationsDAO;
import com.messenger.storage.dao.MessageDAO;
import com.messenger.synchmechanism.ConnectionStatus;
import com.techery.spares.session.SessionHolder;
import com.worldventures.dreamtrips.core.session.UserSession;

import java.util.List;
import java.util.ListIterator;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.inject.Inject;

import rx.Observable;
import rx.subjects.PublishSubject;
import timber.log.Timber;

public class ChatDelegate {
    private static final int MAX_MESSAGE_PER_PAGE = 50;

    private Observable<DataConversation> conversationObservable;
    private Observable<Chat> chatObservable;

    private long before;
    public int page;
    public final AtomicBoolean loading = new AtomicBoolean(false);
    public boolean haveMoreElements = true;

    private final MessageDAO messageDAO;
    private ConversationsDAO conversationsDAO;
    private final SessionHolder<UserSession> sessionHolder;
    private final PaginationDelegate paginationDelegate;
    private final PublishSubject<PaginationStatus> paginationStateObservable = PublishSubject.create();

    @Inject
    ChatDelegate(MessageDAO messageDAO, ConversationsDAO conversationsDAO, SessionHolder<UserSession> sessionHolder, PaginationDelegate paginationDelegate) {
        this.messageDAO = messageDAO;
        this.conversationsDAO = conversationsDAO;
        this.sessionHolder = sessionHolder;
        this.paginationDelegate = paginationDelegate;
        paginationDelegate.setPageSize(MAX_MESSAGE_PER_PAGE);
    }

    public Observable<PaginationStatus> bind(Observable<ConnectionStatus> connectionObservable,
                                             Observable<Chat> chatObservable, Observable<DataConversation> conversationObservable) {
        this.conversationObservable = conversationObservable.take(1).cacheWithInitialCapacity(1);
        this.chatObservable = chatObservable;
        connectToChatConnection(connectionObservable);

        return paginationStateObservable;
    }


    private void connectToChatConnection(Observable<ConnectionStatus> connectionObservable) {
        connectionObservable
                .subscribe(this::handleConnectionState);
    }

    private void handleConnectionState(ConnectionStatus state) {
        if (state == ConnectionStatus.CONNECTED && page == 0) loadNextPage();
    }

    public void tryMarkAsReadMessage(DataMessage lastMessage) {
        if (lastMessage.getStatus() == MessageStatus.READ || TextUtils.equals(lastMessage.getFromId(), getUsername())) {
            return;
        }

        chatObservable.take(1)
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

    public void loadNextPage() {
        if (!haveMoreElements || loading.get()) return;

        loading.set(true);
        paginationStateObservable.onNext(new PaginationStatus(Status.START));
        conversationObservable
                .subscribe(conversation -> {
                    paginationDelegate.loadConversationHistoryPage(conversation, ++page, before,
                            (loadedPage, loadedMessage) -> paginationPageLoaded(loadedMessage),
                            this::pageLoadFailed);
                }, e -> Timber.w("Unable to get conversation"));
    }

    private void pageLoadFailed() {
        page--;
        paginationStateObservable.onNext(new PaginationStatus(Status.FAILED));
    }

    private void paginationPageLoaded(List<Message> loadedMessages) {
        // pagination stops when we loaded nothing. In otherwise we can load not whole page cause localeName is present in some messages
        if (loadedMessages == null || loadedMessages.size() == 0) {
            loading.set(false);
            haveMoreElements = false;
            paginationStateObservable.onNext(new PaginationStatus(false));
            return;
        }

        int loadedCount = loadedMessages.size();
        Message lastMessage = loadedMessages.get(loadedCount - 1);
        before = lastMessage.getDate();

        if (!isLastLoadedMessageRead(loadedMessages)) {
            loadNextPage();
        }

        paginationStateObservable.onNext(new PaginationStatus());
    }

    // TODO: 4/13/16 LAST MESSAGE  remove iterator
    private boolean isLastLoadedMessageRead(List<Message> loadedMessages) {
        ListIterator<Message> iterator = loadedMessages.listIterator(loadedMessages.size());
        while (iterator.hasPrevious()) {
            Message message = iterator.previous();
            if (!TextUtils.equals(message.getFromId(), getUsername())) {
                return message.getStatus() == MessageStatus.READ;
            }
        }
        return true;
    }

    private String getUsername() {
        return sessionHolder.get().get().getUsername();
    }

    public static class PaginationStatus {
        public final Status status;
        public final boolean haveMoreElements;

        public PaginationStatus() {
            this(Status.SUCCESS);
        }

        public PaginationStatus(boolean haveMoreElements) {
            this(Status.SUCCESS, haveMoreElements);
        }

        public PaginationStatus(Status status) {
            this(status, true);
        }

        public PaginationStatus(Status status, boolean haveMoreElements) {
            this.status = status;
            this.haveMoreElements = haveMoreElements;
        }
    }

    public enum Status {
        FAILED, SUCCESS, START
    }
}
