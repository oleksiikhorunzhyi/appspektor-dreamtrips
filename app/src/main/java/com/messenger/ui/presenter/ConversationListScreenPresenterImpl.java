package com.messenger.ui.presenter;

import android.content.Context;
import android.database.Cursor;
import android.text.TextUtils;
import android.util.Pair;
import android.view.MenuItem;
import android.widget.Toast;

import com.messenger.analytics.ConversationSearchSelectedAction;
import com.messenger.analytics.ConversationTypeFilterSelectedAction;
import com.messenger.analytics.ConversationsCountAction;
import com.messenger.entities.DataConversation;
import com.messenger.messengerservers.constant.ConversationType;
import com.messenger.notification.MessengerNotificationFactory;
import com.messenger.storage.dao.ConversationsDAO;
import com.messenger.synchmechanism.SyncStatus;
import com.messenger.ui.view.add_member.NewChatPath;
import com.messenger.ui.view.chat.ChatPath;
import com.messenger.ui.view.conversation.ConversationListScreen;
import com.messenger.ui.view.conversation.ConversationsPath;
import com.messenger.ui.viewstate.ConversationListViewState;
import com.messenger.util.OpenedConversationTracker;
import com.worldventures.core.janet.Injector;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.rx.composer.DelayedComposer;
import com.worldventures.dreamtrips.modules.gcm.delegate.NotificationDelegate;

import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import flow.Flow;
import flow.History;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.subjects.BehaviorSubject;
import rx.subjects.PublishSubject;
import timber.log.Timber;

import static com.messenger.ui.presenter.ConversationListScreenPresenter.ChatTypeItem.ALL_CHATS;
import static com.messenger.ui.presenter.ConversationListScreenPresenter.ChatTypeItem.GROUP_CHATS;

public class ConversationListScreenPresenterImpl extends MessengerPresenterImpl<ConversationListScreen, ConversationListViewState> implements ConversationListScreenPresenter {

   private static final int SELECTED_CONVERSATION_DELAY = 400;

   @Inject ConversationsDAO conversationsDAO;
   @Inject NotificationDelegate notificationDelegate;
   @Inject OpenedConversationTracker openedConversationTracker;

   private PublishSubject<String> filterStream;
   private BehaviorSubject<String> typeStream;
   private PublishSubject<DataConversation> selectedConversationStream;
   private Subscription conversationSubscription;

   public ConversationListScreenPresenterImpl(Context context, Injector injector) {
      super(context, injector);
   }

   @Override
   public void onAttachedToWindow() {
      super.onAttachedToWindow();
      notificationDelegate.cancel(MessengerNotificationFactory.MESSENGER_TAG);
      getViewState().setLoadingState(ConversationListViewState.LoadingState.LOADING);
      applyViewState();
      connectData();
      trackConversations();
   }

   private void connectData() {
      connectFilterStream();
      connectTypeStream();
      connectToFilters();
      connectToOpenedConversation();
      connectToSelectedConversationStream();
   }

   private void connectToOpenedConversation() {
      openedConversationTracker.watchOpenedConversationId()
            .compose(bindViewIoToMainComposer())
            .subscribe(getView()::setSelectedConversationId);
   }

   private void connectFilterStream() {
      filterStream = PublishSubject.create();
      filterStream.doOnNext(getViewState()::setSearchFilter).compose(bindView()).subscribe();
   }

   private void connectTypeStream() {
      typeStream = BehaviorSubject.create();
      typeStream.doOnNext(getViewState()::setChatType).compose(bindView()).subscribe();
   }

   private void trackConversations() {
      conversationsDAO.conversationsCount().take(1).compose(bindView()).subscribe(count -> {
         if (count == 0) { waitForSyncAndTrack(); } else {
            analyticsInteractor.analyticsActionPipe().send(new ConversationsCountAction(count));
         }
      });
   }

   private void waitForSyncAndTrack() {
      connectionStatusStream.filter(status -> status == SyncStatus.CONNECTED)
            .flatMap(status -> conversationsDAO.conversationsCount())
            .take(1)
            .compose(bindView())
            .subscribe(count -> analyticsInteractor.analyticsActionPipe().send(new ConversationsCountAction(count)),
                  e -> Timber.e(e, "Failed to get conv count"));
   }

   private void connectToFilters() {
      Observable.combineLatest(typeStream.asObservable()
            .startWith(ALL_CHATS)
            .distinctUntilChanged(), filterStream.asObservable()
            .startWith(getViewState().getSearchFilter())
            .compose(new DelayedComposer<>(300L))
            .distinctUntilChanged(), Pair::new)
            .compose(bindView())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(filters -> {
               connectToConversations(TextUtils.equals(filters.first, GROUP_CHATS) ? ConversationType.GROUP : null, filters.second);
            }, throwable -> Timber.e(throwable, "Filter error"));
   }

   private void connectToSelectedConversationStream() {
      selectedConversationStream = PublishSubject.<DataConversation>create();
      selectedConversationStream.throttleLast(SELECTED_CONVERSATION_DELAY, TimeUnit.MILLISECONDS)
            .compose(bindViewIoToMainComposer())
            .subscribe(this::openConversation);
   }

   private void connectToConversations(@ConversationType.Type String type, String searchQuery) {
      if (conversationSubscription != null && !conversationSubscription.isUnsubscribed()) {
         conversationSubscription.unsubscribe();
      }
      conversationSubscription = conversationsDAO.selectConversationsList(type, searchQuery)
            .compose(bindViewIoToMainComposer())
            .subscribe(this::applyViewState, throwable -> Timber.e(throwable, "ConversationsDAO error"));
   }

   @Override
   public void onNewViewState() {
      state = new ConversationListViewState();
   }

   private void applyViewState(Cursor cursor) {
      state.setLoadingState(ConversationListViewState.LoadingState.CONTENT);
      getView().showConversations(cursor);
      applyViewState();
   }

   @Override
   public void applyViewState() {
      switch (getViewState().getLoadingState()) {
         case LOADING:
            getView().showLoading();
            break;
         case CONTENT:
            getView().showContent();
            break;
         case ERROR:
            getView().showError(getViewState().getError());
            break;
         default:
            break;
      }
   }

   ///////////////////////////////////////////////////////////////////////////
   // Interaction
   ///////////////////////////////////////////////////////////////////////////

   @Override
   public void onConversationSelected(DataConversation conversation) {
      selectedConversationStream.onNext(conversation);
   }

   public void openConversation(DataConversation conversation) {
      History.Builder historyBuilder = Flow.get(getContext()).getHistory().buildUpon();
      //
      Object oldPath = historyBuilder.pop();
      ChatPath chatPath = new ChatPath(conversation.getId());
      Flow.Direction direction;
      if (oldPath.equals(chatPath)) {
         //don't show message if it exists
         return;
      } else if (oldPath instanceof ConversationsPath) {
         historyBuilder.push(oldPath);
         direction = Flow.Direction.FORWARD;
      } else {
         direction = Flow.Direction.REPLACE;
      }
      historyBuilder.push(chatPath);
      //
      Flow.get(getContext()).setHistory(historyBuilder.build(), direction);
   }

   @Override
   public void onDeleteButtonPressed(DataConversation conversation) {
      getView().showConversationDeletionConfirmationDialog(conversation);
   }

   @Override
   public void onDeletionConfirmed(DataConversation conversation) {
      //not implemented
   }

   @Override
   public void onMoreOptionsButtonPressed(DataConversation conversation) {
      getView().showConversationMoreActionsDialog(conversation);
   }

   @Override
   public void onMarkAsUnreadButtonPressed(DataConversation conversation) {
      Toast.makeText(getContext(), "Mark as unread not yet implemented", Toast.LENGTH_SHORT).show();
   }

   @Override
   public void onTurnOffNotificationsButtonPressed(DataConversation conversation) {
      Toast.makeText(getContext(), "Turn of notifications not yet implemented", Toast.LENGTH_SHORT).show();
   }

   @Override
   public void onConversationsDropdownSelected(ChatTypeItem selectedItem) {
      String lastSelectedType = typeStream.getValue();
      typeStream.onNext(selectedItem.getType());
      if (lastSelectedType != null) {
         trackSelectedChatType(selectedItem);
      }
   }

   private void trackSelectedChatType(ChatTypeItem selectedItem) {
      ConversationTypeFilterSelectedAction action;
      if (TextUtils.equals(selectedItem.getType(), GROUP_CHATS)) {
         action = ConversationTypeFilterSelectedAction.groupChats();
      } else {
         action = ConversationTypeFilterSelectedAction.allChats();
      }
      analyticsInteractor.analyticsActionPipe().send(action);
   }

   @Override
   public void onConversationSearchSelected() {
      analyticsInteractor.analyticsActionPipe().send(new ConversationSearchSelectedAction());
   }

   @Override
   public void onConversationsSearchFilterSelected(String searchFilter) {
      filterStream.onNext(searchFilter);
   }

   ///////////////////////////////////////////////////////////////////////////
   // Menu
   ///////////////////////////////////////////////////////////////////////////

   @Override
   public int getToolbarMenuRes() {
      return R.menu.conversation_list;
   }

   @Override
   public boolean onToolbarMenuItemClick(MenuItem menuItem) {
      switch (menuItem.getItemId()) {
         case R.id.action_add:
            openRoster();
            return true;
         default:
            break;
      }
      return false;
   }

   private void openRoster() {
      History.Builder historyBuilder = Flow.get(getContext()).getHistory().buildUpon();
      //
      Object oldPath = historyBuilder.pop();

      if (oldPath instanceof NewChatPath) { return; }

      historyBuilder.push(oldPath);
      historyBuilder.push(new NewChatPath());

      Flow.get(getContext()).setHistory(historyBuilder.build(), Flow.Direction.FORWARD);
   }
}

