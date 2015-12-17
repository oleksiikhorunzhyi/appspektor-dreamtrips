package com.messenger.ui.presenter;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.messenger.constant.CursorLoaderIds;
import com.messenger.delegate.LoaderDelegate;
import com.messenger.messengerservers.MessengerServerFacade;
import com.messenger.messengerservers.entities.Conversation;
import com.messenger.messengerservers.entities.User;
import com.messenger.messengerservers.listeners.AuthorizeListener;
import com.messenger.ui.activity.ChatActivity;
import com.messenger.ui.activity.NewChatActivity;
import com.messenger.ui.view.ConversationListScreen;
import com.messenger.ui.viewstate.ConversationListViewState;
import com.techery.spares.module.Injector;
import com.techery.spares.session.SessionHolder;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.session.UserSession;

import javax.inject.Inject;

public class ConversationListScreenPresenterImpl extends BaseViewStateMvpPresenter<ConversationListScreen,
        ConversationListViewState> implements ConversationListScreenPresenter {

    @Inject
    SessionHolder<UserSession> appSessionHolder;
    @Inject
    MessengerServerFacade messengerServerFacade;

    private Activity parentActivity;
    private User user;
    private LoaderDelegate loaderDelegate;
    private final CursorLoaderCallback allConversationLoaderCallback = new CursorLoaderCallback(true);
    private final CursorLoaderCallback groupConversationLoaderCallback = new CursorLoaderCallback(false);

    public ConversationListScreenPresenterImpl(Activity activity) {
        this.parentActivity = activity;

        ((Injector) activity.getApplicationContext()).inject(this);
        user = new User("techery_user2");
        loaderDelegate = new LoaderDelegate(activity, messengerServerFacade);
    }

    @Override
    public void onNewViewState() {
        state = new ConversationListViewState();
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        getView().showLoading();
        getViewState().setLoadingState(ConversationListViewState.LoadingState.LOADING);
        initialCursorLoader();

        if (messengerServerFacade.isAuthorized()){
            loadConversationList();
        } else {
            connect();
        }
    }

    @Override
    public void loadConversationList() {
        loaderDelegate.loadConversations();
    }

    private void initialCursorLoader() {
        LoaderManager loaderManager = ((AppCompatActivity) parentActivity).getSupportLoaderManager();
        Loader loader = loaderManager.getLoader(CursorLoaderIds.ALL_CONVERSATION_LOADER);
        allConversationLoaderCallback.cursorIsLoaded = false;
        groupConversationLoaderCallback.cursorIsLoaded = false;
        if (loader == null) {
            loaderManager.initLoader(CursorLoaderIds.ALL_CONVERSATION_LOADER, null, allConversationLoaderCallback);
            loaderManager.initLoader(CursorLoaderIds.GROUP_CONVERSATION_LOADER, null, groupConversationLoaderCallback);
        } else {
            loaderManager.restartLoader(CursorLoaderIds.ALL_CONVERSATION_LOADER, null, allConversationLoaderCallback);
            loaderManager.restartLoader(CursorLoaderIds.GROUP_CONVERSATION_LOADER, null, groupConversationLoaderCallback);
        }
    }

    private void connect() {
        messengerServerFacade.addAuthorizationListener(new AuthorizeListener() {
            @Override
            public void onSuccess() {
                Log.e("Xmpp server", "Authorized");
                loadConversationList();
            }
        });
        messengerServerFacade.authorizeAsync(user.getUserName(), user.getUserName());
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
                getView().showConversations(getViewState().getCursor());
                getView().showContent();
                break;
            case ERROR:
                getView().showError(getViewState().getError());
                break;
        }
    }

    @Override public void onConversationSelected(Conversation conversation) {
        if (conversation.getType().equals(Conversation.Type.GROUP)) {
            ChatActivity.startGroup(parentActivity, conversation.getId());
        } else {
            ChatActivity.start(parentActivity, conversation.getId(), null);
        }
    }

    @Override public void onConversationsDropdownSelected(boolean showOnlyGroupConversations) {
        getViewState().setShowOnlyGroupConversations(showOnlyGroupConversations);
        assignNeededCursorAndRefresh();
    }

    @Override
    public void onConversationsSearchFilterSelected(String searchFilter) {
        getViewState().setConversationsSearchFilter(searchFilter);
//        getView().setSearchFilter(searchFilter);
    }

    private void assignNeededCursorAndRefresh() {
        if (getView() == null) {
            return;
        }
        Cursor cursorToAssign = null;
        // Find corresponding all chats or only groups cursor
        boolean neededCursorLoaded = false;
        if (getViewState().isShowOnlyGroupConversations()) {
            if (groupConversationLoaderCallback.cursorIsLoaded) {
                cursorToAssign = groupConversationLoaderCallback.cursor;
                neededCursorLoaded = true;
            }
        } else {
            if (allConversationLoaderCallback.cursorIsLoaded) {
                cursorToAssign = allConversationLoaderCallback.cursor;
                neededCursorLoaded = true;
            }
        }
        // We cannot use null check here as cursor might be null and we still need to refresh
        // view
        if (neededCursorLoaded) {
            state.setLoadingState(ConversationListViewState.LoadingState.CONTENT);
            getViewState().setCursor(cursorToAssign);
            applyViewState();
        }
    }

    @Override
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        ((AppCompatActivity) parentActivity).getSupportLoaderManager().destroyLoader(CursorLoaderIds.ALL_CONVERSATION_LOADER);
        ((AppCompatActivity) parentActivity).getSupportLoaderManager().destroyLoader(CursorLoaderIds.GROUP_CONVERSATION_LOADER);
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
                Intent intent = new Intent(parentActivity, NewChatActivity.class);
                parentActivity.startActivity(intent);
                return true;
        }
        return false;
    }

    ///////////////////////////////////////////////////////////////////////////
    // Cursor Loader Callback
    ///////////////////////////////////////////////////////////////////////////

    private class CursorLoaderCallback implements LoaderManager.LoaderCallbacks<Cursor> {

        private boolean allConversation;
        private boolean cursorIsLoaded;
        private Cursor cursor;

        public CursorLoaderCallback(boolean allConversation) {
            this.allConversation = allConversation;
        }

        @Override
        public Loader<Cursor> onCreateLoader(int id, Bundle args) {
            String select = allConversation ? "" : String.format("type not like '%s'", Conversation.Type.CHAT);
            return new CursorLoader(parentActivity, Conversation.CONTENT_URI,
                    null, select, null, "Messages.date desc");
        }

        @Override
        public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
            showConversation(data);
        }

        @Override
        public void onLoaderReset(Loader<Cursor> loader) {
            showConversation(null);
        }

        private void showConversation(@NonNull Cursor cursor) {
            cursorIsLoaded = true;
            this.cursor = cursor;
            assignNeededCursorAndRefresh();
        }
    }
}

