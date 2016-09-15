package com.messenger.delegate.chat;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.messenger.delegate.chat.command.LoadChatMessagesCommand;
import com.messenger.messengerservers.constant.MessageStatus;
import com.messenger.messengerservers.model.Message;
import com.messenger.messengerservers.paginations.PaginationResult;
import com.messenger.synchmechanism.SyncStatus;
import com.techery.spares.session.SessionHolder;
import com.worldventures.dreamtrips.core.session.UserSession;

import org.immutables.value.Value;

import java.util.List;
import java.util.ListIterator;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.inject.Inject;

import io.techery.janet.ActionPipe;
import rx.Observable;
import rx.subjects.PublishSubject;
import timber.log.Timber;

public class MessagesPaginationDelegate {

   private static final int MAX_MESSAGES_PER_PAGE = 50;

   private String conversationId;

   private long beforeMessageTimestamp;
   private int page;
   private final AtomicBoolean loading = new AtomicBoolean(false);
   private boolean hasMoreElements = true;
   private final PublishSubject<PaginationStatus> paginationStateObservable = PublishSubject.create();

   private final SessionHolder<UserSession> sessionHolder;
   private final ActionPipe<LoadChatMessagesCommand> loadMessagesPipe;

   @Inject
   public MessagesPaginationDelegate(SessionHolder<UserSession> sessionHolder, ChatExtensionInteractor chatExtensionInteractor) {
      this.sessionHolder = sessionHolder;
      this.loadMessagesPipe = chatExtensionInteractor.getLoadChatMessagesCommandActionPipe();
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

   public boolean isLoading() {
      return loading.get();
   }

   public void loadNextPage() {
      if (!hasMoreElements || loading.get()) return;

      loading.set(true);

      paginationStateObservable.onNext(ImmutablePaginationStatus.builder().status(Status.START).page(++page).build());

      loadMessagesPipe.createObservableResult(new LoadChatMessagesCommand(conversationId, page, MAX_MESSAGES_PER_PAGE, beforeMessageTimestamp))
            .subscribe(command -> paginationPageLoaded(command.getResult()), this::pageLoadFailed);
   }

   private void pageLoadFailed(Throwable throwable) {
      Timber.e(throwable, "pageLoadFailed");
      paginationStateObservable.onNext(ImmutablePaginationStatus.builder().status(Status.FAILED).page(page).build());
      page--;
      loading.set(false);
   }

   private void paginationPageLoaded(@NonNull PaginationResult<Message> paginationResult) {
      List<Message> messages = paginationResult.getResult();
      beforeMessageTimestamp = getLastMessageDate(messages);
      hasMoreElements = paginationResult.getLoadedCount() >= MAX_MESSAGES_PER_PAGE;
      loading.set(false);
      if (!isLastLoadedMessageRead(messages) && hasMoreElements) {
         loadNextPage();
      }

      paginationStateObservable.onNext(ImmutablePaginationStatus.builder()
            .status(Status.SUCCESS)
            .page(page)
            .loadedElementsCount(paginationResult.getLoadedCount())
            .build());
   }

   private long getLastMessageDate(@NonNull List<Message> messages) {
      int messageCount = messages.size();
      return messageCount > 0 ? messages.get(messageCount - 1).getDate() : 0;
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

   public Observable<PaginationStatus> bind(Observable<SyncStatus> connectionObservable, String conversationId) {
      this.conversationId = conversationId;
      connectionObservable.subscribe(this::handleConnectionState);
      return paginationStateObservable;
   }

   public void reset() {
      this.page = 0;
      this.beforeMessageTimestamp = 0;
      this.hasMoreElements = false;
   }

   private void handleConnectionState(SyncStatus state) {
      if (state == SyncStatus.CONNECTED && page == 0) loadNextPage();
   }

   public boolean hasMoreElements() {
      return hasMoreElements;
   }

   @Value.Immutable()
   public interface PaginationStatus {

      Status getStatus();

      Integer getPage();

      @Nullable
      Integer getLoadedElementsCount();
   }

   public enum Status {
      FAILED, SUCCESS, START
   }
}
