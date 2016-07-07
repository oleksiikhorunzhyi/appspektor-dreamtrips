package com.messenger.delegate.chat;

import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.messenger.delegate.chat.command.LoadChatMessagesCommand;
import com.messenger.messengerservers.constant.MessageStatus;
import com.messenger.messengerservers.model.Message;
import com.messenger.synchmechanism.SyncStatus;
import com.techery.spares.session.SessionHolder;
import com.worldventures.dreamtrips.core.session.UserSession;

import org.immutables.value.Value;

import java.util.List;
import java.util.ListIterator;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.inject.Inject;

import io.techery.janet.ActionPipe;
import io.techery.janet.Command;
import io.techery.janet.Janet;
import rx.Observable;
import rx.schedulers.Schedulers;
import rx.subjects.PublishSubject;

public class MessagesPaginationDelegate {

    public static final int MAX_MESSAGES_PER_PAGE = 50;

    private String conversationId;

    private long beforeMessageTimestamp;
    private int page;
    private final AtomicBoolean loading = new AtomicBoolean(false);
    private boolean hasMoreElements = true;
    private final PublishSubject<PaginationStatus> paginationStateObservable = PublishSubject.create();

    private final SessionHolder<UserSession> sessionHolder;
    private final ActionPipe<LoadChatMessagesCommand> loadMessagesPipe;

    @Inject
    public MessagesPaginationDelegate(SessionHolder<UserSession> sessionHolder, Janet janet) {
        this.sessionHolder = sessionHolder;
        this.loadMessagesPipe = janet.createPipe(LoadChatMessagesCommand.class, Schedulers.io());
    }

    public void loadFirstPage() {
        page = 0;
        beforeMessageTimestamp = 0;
        hasMoreElements = true;
        loadNextPage();
    }

    public void forceLoadNextPage() {
        hasMoreElements = true;
        loadNextPage();
    }

    public void loadNextPage() {
        if (!hasMoreElements || loading.get()) return;

        loading.set(true);

        paginationStateObservable.onNext(ImmutablePaginationStatus.builder()
                .status(Status.START)
                .page(++page)
                .build()
        );

        loadMessagesPipe.createObservableResult(new LoadChatMessagesCommand(conversationId,
                page, MAX_MESSAGES_PER_PAGE, beforeMessageTimestamp))
                .map(Command::getResult)
                .subscribe(this::paginationPageLoaded, throwable -> pageLoadFailed());
    }

    private void pageLoadFailed() {
        paginationStateObservable.onNext(ImmutablePaginationStatus.builder()
                .status(Status.FAILED)
                .page(page)
                .build()
        );
        page--;
        loading.set(false);
    }

    private void paginationPageLoaded(List<Message> loadedMessages) {
        // pagination stops when we loaded nothing.
        // In otherwise we can load not whole page cause localeName is present in some messages
        if (loadedMessages == null || loadedMessages.size() == 0) {
            loading.set(false);
            hasMoreElements = false;
            paginationStateObservable.onNext(ImmutablePaginationStatus.builder()
                    .loadedElementsCount(0)
                    .page(page)
                    .status(Status.SUCCESS)
                    .build()
            );
            return;
        }

        int loadedCount = loadedMessages.size();
        Message lastMessage = loadedMessages.get(loadedCount - 1);
        beforeMessageTimestamp = lastMessage.getDate();

        if (!isLastLoadedMessageRead(loadedMessages)) {
            loadNextPage();
        } else {
            loading.set(false);
        }

        paginationStateObservable.onNext(ImmutablePaginationStatus.builder()
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
            String username = sessionHolder.get().get().getUsername();
            if (!TextUtils.equals(message.getFromId(), username)) {
                return message.getStatus() == MessageStatus.READ;
            }
        }
        return true;
    }

    public Observable<PaginationStatus> bind(Observable<SyncStatus> connectionObservable,
                                             String conversationId) {
        this.conversationId = conversationId;
        connectionObservable.subscribe(this::handleConnectionState);
        return paginationStateObservable;
    }

    private void handleConnectionState(SyncStatus state) {
        if (state == SyncStatus.CONNECTED && page == 0) loadNextPage();
    }

    @Value.Immutable()
    public interface PaginationStatus {

        Status getStatus();

        Integer getPage();

        @Nullable Integer getLoadedElementsCount();
    }

    public enum Status {
        FAILED, SUCCESS, START
    }
}
