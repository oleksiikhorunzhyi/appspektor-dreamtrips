package com.messenger.ui.presenter;

import android.app.Activity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.messenger.delegate.ChatLeavingDelegate;
import com.messenger.messengerservers.entities.Conversation;
import com.messenger.messengerservers.entities.User;
import com.messenger.storege.dao.ConversationsDAO;
import com.messenger.ui.activity.ChatActivity;
import com.messenger.ui.activity.NewChatMembersActivity;
import com.messenger.ui.helper.ConversationHelper;
import com.messenger.ui.view.ConversationListScreen;
import com.messenger.ui.viewstate.ConversationListViewState;
import com.techery.spares.module.Injector;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.api.DreamSpiceManager;

import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class ConversationListScreenPresenterImpl extends MessengerPresenterImpl<ConversationListScreen,
        ConversationListViewState> implements ConversationListScreenPresenter {

    private final ConversationHelper conversationHelper;
    private Subscription contactSubscription;

    @Inject
    User user;
    @Inject
    DreamSpiceManager dreamSpiceManager;
    @Inject
    ConversationsDAO conversationsDAO;

    private Activity parentActivity;
    private final ChatLeavingDelegate chatLeavingDelegate;


    public ConversationListScreenPresenterImpl(Activity activity) {
        this.parentActivity = activity;
        this.conversationHelper = new ConversationHelper();

        chatLeavingDelegate = new ChatLeavingDelegate((Injector) activity.getApplication(), null);
        ((Injector) activity.getApplicationContext()).inject(this);
    }

    @Override
    public void onNewViewState() {
        state = new ConversationListViewState();
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        dreamSpiceManager.start(getView().getContext());
        getViewState().setLoadingState(ConversationListViewState.LoadingState.LOADING);
        getView().showLoading();
        connectCursor();
    }

    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (dreamSpiceManager.isStarted()) {
            dreamSpiceManager.shouldStop();
        }
        contactSubscription.unsubscribe();
        getViewState().setCursor(null);
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

    private void connectCursor() {
        if (contactSubscription != null && !contactSubscription.isUnsubscribed()) {
            contactSubscription.unsubscribe();
        }
        contactSubscription = conversationsDAO.selectConversationsList(
                getViewState().isShowOnlyGroupConversations() ? Conversation.Type.GROUP : null)
                .throttleLast(1, TimeUnit.MILLISECONDS)
                .onBackpressureLatest()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(cursor -> {
                    state.setLoadingState(ConversationListViewState.LoadingState.CONTENT);
                    state.setCursor(cursor);
                    applyViewState();
                });
    }

    @Override
    public User getUser() {
        return user;
    }

    @Override
    public void applyViewState() {
        if (!isViewAttached()) {
            return;
        }
        switch (getViewState().getLoadingState()) {
            case LOADING:
                getView().showLoading();
                break;
            case CONTENT:
                getView().showConversations(getViewState().getCursor(), getViewState().getConversationsSearchFilter());
                getView().showContent();
                break;
            case ERROR:
                getView().showError(getViewState().getError());
                break;
        }
    }

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
        getViewState().setShowOnlyGroupConversations(selectedItem.getType().equals(ChatTypeItem.GROUP_CHATS));
        connectCursor();
    }

    @Override
    public void onConversationsSearchFilterSelected(String searchFilter) {
        getViewState().setConversationsSearchFilter(searchFilter);
        applyViewState();
    }

    ///////////////////////////////////////////////////////////////////////////
    // Activity related
    ///////////////////////////////////////////////////////////////////////////

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        parentActivity.getMenuInflater().inflate(R.menu.conversation_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_add:
                NewChatMembersActivity.startInNewChatMode(parentActivity);
                return true;
        }
        return false;
    }
}

