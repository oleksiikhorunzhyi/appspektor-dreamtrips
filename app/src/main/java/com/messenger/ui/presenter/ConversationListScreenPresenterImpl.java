package com.messenger.ui.presenter;

import android.app.Activity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

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
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.api.DreamSpiceManager;

import javax.inject.Inject;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class ConversationListScreenPresenterImpl extends BaseViewStateMvpPresenter<ConversationListScreen,
        ConversationListViewState> implements ConversationListScreenPresenter {

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

    @Override
    public void onVisibilityChanged(int visibility) {
        super.onVisibilityChanged(visibility);
        if (visibility == View.VISIBLE){
            initialCursorLoader();
        } else {
            contactSubscription.unsubscribe();
        }
    }

    private void initialCursorLoader() {
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
                .query(queryBuilder.build(), Conversation.CONTENT_URI, Message.CONTENT_URI)
                .onBackpressureLatest()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .compose(bindVisibility())
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
        ChatActivity.startChat(parentActivity, conversation);
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

