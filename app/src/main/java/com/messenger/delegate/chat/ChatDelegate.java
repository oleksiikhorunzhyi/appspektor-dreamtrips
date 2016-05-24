package com.messenger.delegate.chat;

import android.text.TextUtils;
import android.util.Pair;

import com.messenger.entities.DataConversation;
import com.messenger.entities.DataMessage;
import com.messenger.entities.DataUser;
import com.messenger.messengerservers.chat.ChatState;
import com.messenger.messengerservers.chat.Chat;
import com.messenger.messengerservers.constant.MessageStatus;
import com.messenger.messengerservers.model.Message;
import com.messenger.storage.dao.ConversationsDAO;
import com.messenger.storage.dao.MessageDAO;
import com.messenger.synchmechanism.SyncStatus;
import com.techery.spares.session.SessionHolder;
import com.worldventures.dreamtrips.core.session.UserSession;

import java.util.List;
import java.util.ListIterator;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.inject.Inject;

import rx.Observable;
import rx.schedulers.Schedulers;
import rx.subjects.PublishSubject;
import timber.log.Timber;

public class ChatDelegate {
    private static final int MAX_MESSAGE_PER_PAGE = 50;

    private Observable<DataConversation> conversationObservable;
    private Observable<Chat> chatObservable;

    private long beforeTimestamp;
    private int page;
    private final AtomicBoolean loading = new AtomicBoolean(false);
    private boolean haveMoreElements = true;

    private final MessageDAO messageDAO;
    private final ConversationsDAO conversationsDAO;
    private final SessionHolder<UserSession> sessionHolder;
    private final HistoryPaginationDelegate historyPaginationDelegate;
    private final CreateChatHelper createChatHelper;

    private final PublishSubject<PaginationStatus> paginationStateObservable = PublishSubject.create();

    @Inject
    ChatDelegate(CreateChatHelper createChatHelper, MessageDAO messageDAO,
                 ConversationsDAO conversationsDAO, SessionHolder<UserSession> sessionHolder,
                 HistoryPaginationDelegate historyPaginationDelegate) {
        this.createChatHelper = createChatHelper;
        this.messageDAO = messageDAO;
        this.conversationsDAO = conversationsDAO;
        this.sessionHolder = sessionHolder;
        this.historyPaginationDelegate = historyPaginationDelegate;
        historyPaginationDelegate.setPageSize(MAX_MESSAGE_PER_PAGE);

        historyPaginationDelegate.getPageObservable()
                .subscribe(this::paginationPageLoaded, throwable -> pageLoadFailed());
    }

    public Observable<PaginationStatus> bind(Observable<SyncStatus> connectionObservable, String conversationId) {
        this.conversationObservable = conversationsDAO.getConversation(conversationId)
                .take(1)
                .cacheWithInitialCapacity(1);
        this.chatObservable = createChatHelper.createChat(conversationId)
                .replay(1)
                .autoConnect();
        connectToChatConnection(connectionObservable);

        return paginationStateObservable;
    }

    private void connectToChatConnection(Observable<SyncStatus> connectionObservable) {
        connectionObservable
                .subscribe(this::handleConnectionState);
    }

    private void handleConnectionState(SyncStatus state) {
        if (state == SyncStatus.CONNECTED && page == 0) loadNextPage();
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

    public void loadFirstPage() {
        page = 0;
        beforeTimestamp = 0;
        loadNextPage();
    }

    public void loadNextPage() {
        if (!haveMoreElements || loading.get()) return;

        loading.set(true);
        paginationStateObservable.onNext(PaginationStatus.builder()
                .status(Status.START)
                .page(++page)
                .build()
        );
        conversationObservable
                .subscribe(conversation -> {
                    historyPaginationDelegate.loadConversationHistoryPage(conversation, page, beforeTimestamp);
                }, e -> Timber.w("Unable to get conversation"));
    }

    private void pageLoadFailed() {
        page--;
        paginationStateObservable.onNext(PaginationStatus.builder()
                .status(Status.FAILED)
                .page(page)
                .haveMoreElements(true)
                .build()
        );
    }

    private void paginationPageLoaded(List<Message> loadedMessages) {
        // pagination stops when we loaded nothing. In otherwise we can load not whole page cause localeName is present in some messages
        if (loadedMessages == null || loadedMessages.size() == 0) {
            loading.set(false);
            haveMoreElements = false;
            paginationStateObservable.onNext(PaginationStatus.builder()
                    .haveMoreElements(false)
                    .loadedElementsCount(0)
                    .page(page)
                    .status(Status.SUCCESS)
                    .build()
            );
            return;
        }

        int loadedCount = loadedMessages.size();
        Message lastMessage = loadedMessages.get(loadedCount - 1);
        beforeTimestamp = lastMessage.getDate();

        if (!isLastLoadedMessageRead(loadedMessages)) {
            loadNextPage();
        } else {
            loading.set(false);
        }

        paginationStateObservable.onNext(PaginationStatus.builder()
                .haveMoreElements(true)
                .status(Status.SUCCESS)
                .page(page)
                .loadedElementsCount(loadedCount)
                .build()
        );
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
        public final int page;
        public final int loadedElementsCount;

        private PaginationStatus(Builder builder) {
            status = builder.status;
            page = builder.page;
            haveMoreElements = builder.haveMoreElements;
            loadedElementsCount = builder.loadedElementsCount;
        }

        private static Builder builder() {
            return new Builder();
        }

        private static class Builder {
            private Status status;
            private boolean haveMoreElements;
            private int page;
            private int loadedElementsCount = 0;

            public Builder status(Status val) {
                this.status = val;
                return this;
            }

            public Builder haveMoreElements(boolean var) {
                this.haveMoreElements = var;
                return this;
            }

            public Builder page(int var) {
                this.page = var;
                return this;
            }

            public Builder loadedElementsCount(int var) {
                this.loadedElementsCount = var;
                return this;
            }

            public PaginationStatus build() {
                return new PaginationStatus(this);
            }
        }
    }

    public enum Status {
        FAILED, SUCCESS, START
    }
}
