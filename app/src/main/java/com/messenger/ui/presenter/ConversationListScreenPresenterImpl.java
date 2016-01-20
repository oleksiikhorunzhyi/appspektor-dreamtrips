package com.messenger.ui.presenter;

import android.app.Activity;
import android.database.Cursor;
import android.util.Pair;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.messenger.delegate.ChatLeavingDelegate;
import com.messenger.messengerservers.entities.Conversation;
import com.messenger.messengerservers.entities.User;
import com.messenger.notification.MessengerNotificationFactory;
import com.messenger.storage.dao.ConversationsDAO;
import com.messenger.ui.activity.ChatActivity;
import com.messenger.ui.activity.NewChatMembersActivity;
import com.messenger.ui.helper.ConversationHelper;
import com.messenger.ui.view.ConversationListScreen;
import com.messenger.ui.viewstate.ConversationListViewState;
import com.techery.spares.module.Injector;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.api.DreamSpiceManager;
import com.worldventures.dreamtrips.core.rx.composer.DelayedComposer;
import com.worldventures.dreamtrips.core.rx.composer.IoToMainComposer;
import com.worldventures.dreamtrips.modules.gcm.delegate.NotificationDelegate;

import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import rx.Observable;
import rx.subjects.PublishSubject;

import static com.messenger.ui.presenter.ConversationListScreenPresenter.ChatTypeItem.ALL_CHATS;
import static com.messenger.ui.presenter.ConversationListScreenPresenter.ChatTypeItem.GROUP_CHATS;

public class ConversationListScreenPresenterImpl extends MessengerPresenterImpl<ConversationListScreen,
        ConversationListViewState> implements ConversationListScreenPresenter {

    @Inject
    User user;
    @Inject
    DreamSpiceManager dreamSpiceManager;
    @Inject
    ConversationsDAO conversationsDAO;
    @Inject
    NotificationDelegate notificationDelegate;

    private final Activity parentActivity;
    private final ChatLeavingDelegate chatLeavingDelegate;
    private final ConversationHelper conversationHelper;
    //
    private PublishSubject<String> filterStream;
    private PublishSubject<String> typeStream;

    public ConversationListScreenPresenterImpl(Activity activity) {
        this.parentActivity = activity;
        this.conversationHelper = new ConversationHelper();

        chatLeavingDelegate = new ChatLeavingDelegate((Injector) activity.getApplication(), null);
        ((Injector) activity.getApplicationContext()).inject(this);
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        notificationDelegate.cancel(MessengerNotificationFactory.MESSENGER_TAG);
        dreamSpiceManager.start(getView().getActivity());
        getViewState().setLoadingState(ConversationListViewState.LoadingState.LOADING);
        applyViewState();
        connectData();
    }

    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (dreamSpiceManager.isStarted()) {
            dreamSpiceManager.shouldStop();
        }
    }

    @Override
    public void onVisibilityChanged(int visibility) {
        super.onVisibilityChanged(visibility);
        if (visibility == View.VISIBLE) {
            chatLeavingDelegate.register();
        } else {
            chatLeavingDelegate.unregister();
        }
    }

    @Override
    public User getUser() {
        return user;
    }

    private void connectData() {
        connectFilterStream();
        connectTypeStream();
        connectCursor();
    }

    private void connectFilterStream() {
        filterStream = PublishSubject.create();
        filterStream
                .doOnNext(getViewState()::setSearchFilter)
                .compose(bindView()).subscribe();
    }

    private void connectTypeStream() {
        typeStream = PublishSubject.create();
        typeStream
                .doOnNext(getViewState()::setChatType)
                .compose(bindView()).subscribe();

    }

    private void connectCursor() {
        Observable<Cursor> cursorObs = typeStream.asObservable().startWith(ALL_CHATS).distinctUntilChanged()
                .flatMap(type -> {
                    String convType = type == GROUP_CHATS ? Conversation.Type.GROUP : null;
                    return conversationsDAO.selectConversationsList(convType)
                            .onBackpressureLatest()
                            .throttleLast(200l, TimeUnit.MILLISECONDS);
                });
        Observable<String> filterObs = filterStream.asObservable()
                .startWith(getViewState().getSearchFilter())
                .compose(new DelayedComposer<>(300L))
                .distinctUntilChanged();

        Observable.combineLatest(cursorObs, filterObs, (c, s) -> new Pair<>(c, s))
                .compose(new IoToMainComposer<>())
                .compose(bindView())
                .subscribe(pair -> {
                    state.setLoadingState(ConversationListViewState.LoadingState.CONTENT);
                    applyViewState(pair.first, pair.second);
                });
    }

    @Override
    public void onNewViewState() {
        state = new ConversationListViewState();
    }

    private void applyViewState(Cursor cursor, String filter) {
        getView().showConversations(cursor, filter);
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
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    // Interaction
    ///////////////////////////////////////////////////////////////////////////

    @Override
    public void onConversationSelected(Conversation conversation) {
        ChatActivity.startChat(parentActivity, conversation);
    }

    @Override
    public void onDeleteButtonPressed(Conversation conversation) {
        getView().showConversationDeletionConfirmationDialog(conversation);
    }

    @Override
    public void onDeletionConfirmed(Conversation conversation) {
        if (conversationHelper.isGroup(conversation)) {
            chatLeavingDelegate.leave(conversation);
        } else {
            Toast.makeText(parentActivity, "Delete not yet implemented", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onMoreOptionsButtonPressed(Conversation conversation) {
        getView().showConversationMoreActionsDialog(conversation);
    }

    @Override
    public void onMarkAsUnreadButtonPressed(Conversation conversation) {
        Toast.makeText(parentActivity, "Mark as unread not yet implemented",
                Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onTurnOffNotificationsButtonPressed(Conversation conversation) {
        Toast.makeText(parentActivity, "Turn of notifications not yet implemented",
                Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onConversationsDropdownSelected(ChatTypeItem selectedItem) {
        typeStream.onNext(selectedItem.getType());
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
                NewChatMembersActivity.startInNewChatMode(parentActivity);
                return true;
        }
        return false;
    }
}

