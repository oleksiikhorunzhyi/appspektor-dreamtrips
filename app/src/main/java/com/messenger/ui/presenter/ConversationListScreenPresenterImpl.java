package com.messenger.ui.presenter;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.messenger.constant.CursorLoaderIds;
import com.messenger.messengerservers.entities.Conversation;
import com.messenger.messengerservers.entities.Message;
import com.messenger.messengerservers.entities.User;
import com.messenger.ui.activity.ChatActivity;
import com.messenger.ui.activity.NewChatMembersActivity;
import com.messenger.ui.view.ConversationListScreen;
import com.messenger.ui.viewstate.ConversationListViewState;
import com.messenger.util.RxContentResolver;
import com.raizlabs.android.dbflow.config.FlowManager;
import com.techery.spares.module.Injector;
import com.trello.rxlifecycle.RxLifecycle;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.api.DreamSpiceManager;

import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class ConversationListScreenPresenterImpl extends BaseViewStateMvpPresenter<ConversationListScreen,
        ConversationListViewState> implements ConversationListScreenPresenter {

    private static final int REQUEST_CODE_OPEN_CHAT = 33;
    private static final int REQUEST_CODE_CREATE_CHAT = 34;

    private final RxContentResolver contentResolver;
    private Subscription contactSubscription;

    @Inject
    User user;
    @Inject
    DreamSpiceManager dreamSpiceManager;

    private Activity parentActivity;

    public ConversationListScreenPresenterImpl(Activity activity) {
        this.parentActivity = activity;

        ((Injector) activity.getApplicationContext()).inject(this);
        contentResolver = new RxContentResolver(activity.getContentResolver(),
                query -> FlowManager.getDatabaseForTable(User.class).getWritableDatabase()
                        .rawQuery(query.selection, query.selectionArgs));
    }

    @Override
    public void onNewViewState() {
        state = new ConversationListViewState();
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        dreamSpiceManager.start(getView().getContext());
        getView().showLoading();
        getViewState().setLoadingState(ConversationListViewState.LoadingState.LOADING);

        initialCursorLoader();
    }

    private void initialCursorLoader() {
        if (contactSubscription != null && !contactSubscription.isUnsubscribed()) {
            contactSubscription.unsubscribe();
        }
        StringBuilder query = new StringBuilder("SELECT c.*, m." + Message.COLUMN_TEXT + " as " + Message.COLUMN_TEXT + ", " +
                "m." + Message.COLUMN_FROM + " as " + Message.COLUMN_FROM + ", " +
                "m." + Message.COLUMN_DATE + " as " + Message.COLUMN_DATE + ", " +
                "u." + User.COLUMN_NAME + " as " + User.COLUMN_NAME + " " +
                "FROM " + Conversation.TABLE_NAME + " c " +
                "LEFT JOIN " + Message.TABLE_NAME + " m " +
                "ON m." + Message._ID + "=(" +
                "SELECT " + Message._ID + " FROM " + Message.TABLE_NAME + " mm " +
                "WHERE mm." + Message.COLUMN_CONVERSATION_ID + "=c." + Conversation.COLUMN_ID +
                " ORDER BY mm." + Message.COLUMN_DATE + " DESC LIMIT 1) " +
                "LEFT JOIN " + User.TABLE_NAME + " u " +
                "ON m." + Message.COLUMN_FROM + "=u." + User.COLUMN_ID
        );


        if (getViewState().isShowOnlyGroupConversations()) {
            query.append(" WHERE c.type not like ?");
        }
        query.append(" ORDER BY m." + Message.COLUMN_DATE + " DESC");

        RxContentResolver.Query.Builder queryBuilder = new RxContentResolver.Query.Builder(null)
                .withSelection(query.toString());
        if (getViewState().isShowOnlyGroupConversations()) {
            queryBuilder.withSelectionArgs(new String[]{Conversation.Type.CHAT});
        }
        contactSubscription = contentResolver
                .query(queryBuilder.build(), User.CONTENT_URI, Conversation.CONTENT_URI, Message.CONTENT_URI)
                .throttleLast(500, TimeUnit.MILLISECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .compose(RxLifecycle.bindView((View) getView()))
                .subscribe(cursor -> {
                    state.setLoadingState(ConversationListViewState.LoadingState.CONTENT);
                    getViewState().setCursor(cursor);
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
                getView().showConversations(getViewState().getCursor(),
                        getViewState().getConversationsSearchFilter());
                getView().showContent();
                break;
            case ERROR:
                getView().showError(getViewState().getError());
                break;
        }
    }

    @Override
    public void onConversationSelected(Conversation conversation) {
        if (conversation.getType().equals(Conversation.Type.GROUP)) {
            ChatActivity.startGroupChat(parentActivity, conversation.getId(), REQUEST_CODE_OPEN_CHAT);
        } else {
            ChatActivity.startSingleChat(parentActivity, conversation.getId(), REQUEST_CODE_OPEN_CHAT);
        }
    }

    @Override
    public void onDeleteButtonPressed(Conversation conversation) {
        getView().showConversationDeletionConfirmationDialog(conversation);
    }

    @Override
    public void onDeletionConfirmed(Conversation conversation) {
        Toast.makeText(parentActivity, "Delete not yet implemented",
                Toast.LENGTH_SHORT).show();
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
    public void onConversationsDropdownSelected(boolean showOnlyGroupConversations) {
        getViewState().setShowOnlyGroupConversations(showOnlyGroupConversations);
        initialCursorLoader();
    }

    @Override
    public void onConversationsSearchFilterSelected(String searchFilter) {
        getViewState().setConversationsSearchFilter(searchFilter);
        applyViewState();
    }

    @Override
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        dreamSpiceManager.shouldStop();
        ((AppCompatActivity) parentActivity).getSupportLoaderManager().destroyLoader(CursorLoaderIds.ALL_CONVERSATION_LOADER);
        ((AppCompatActivity) parentActivity).getSupportLoaderManager().destroyLoader(CursorLoaderIds.GROUP_CONVERSATION_LOADER);
    }

    ///////////////////////////////////////////////////////////////////////////
    // Activity related
    ///////////////////////////////////////////////////////////////////////////


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_CODE_OPEN_CHAT:
            case REQUEST_CODE_CREATE_CHAT:
                if (resultCode == Activity.RESULT_OK) {
                    String conversationId = data.getStringExtra(Extra.CONVERSATION_ID);
                    String conversationType = data.getStringExtra(Extra.CONVERSATION_TYPE);
                    if (conversationType.equals(Conversation.Type.GROUP)) {
                        ChatActivity.startGroupChat(parentActivity,
                                conversationId, REQUEST_CODE_OPEN_CHAT);
                    } else {
                        ChatActivity.startSingleChat(parentActivity,
                                conversationId, REQUEST_CODE_OPEN_CHAT);
                    }
                }
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        parentActivity.getMenuInflater().inflate(R.menu.conversation_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_add:
                NewChatMembersActivity.startInNewChatMode(parentActivity, REQUEST_CODE_CREATE_CHAT);
                return true;
        }
        return false;
    }
}

