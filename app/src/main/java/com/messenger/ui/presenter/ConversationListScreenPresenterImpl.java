package com.messenger.ui.presenter;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
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
import com.messenger.model.ChatConversation;
import com.messenger.ui.activity.ChatActivity;
import com.messenger.ui.activity.NewChatActivity;
import com.messenger.ui.view.ConversationListScreen;
import com.messenger.ui.viewstate.ConversationListViewState;
import com.techery.spares.module.Injector;
import com.techery.spares.session.SessionHolder;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.session.UserSession;

import java.util.Arrays;

import javax.inject.Inject;

public class ConversationListScreenPresenterImpl extends BaseViewStateMvpPresenter<ConversationListScreen,
        ConversationListViewState> implements ConversationListScreenPresenter {

    @Inject
    SessionHolder<UserSession> appSessionHolder;
    @Inject
    MessengerServerFacade messengerServerFacade;

    private User user;
    private LoaderDelegate loaderDelegate;
    private final CursorLoaderCallback allConversationLoaderCallback = new CursorLoaderCallback(true);
    private final CursorLoaderCallback groupConversationLoaderCallback = new CursorLoaderCallback(false);

    @Override
    public void attachView(ConversationListScreen view) {
        super.attachView(view);
        ((Injector)view.getActivity().getApplication()).inject(this);
        loaderDelegate = new LoaderDelegate(getContext(), messengerServerFacade);
    }

    @Override public void loadConversationList() {
        loaderDelegate.loadConversations();
    }

    @Override public void onNewViewState() {
        state = new ConversationListViewState();
        initialCursorLoader();
        if (messengerServerFacade.isAuthorized()) {
            loadConversationList();
        } else {
            getView().showInputUserDialog();
        }
    }

    private void initialCursorLoader(){
        LoaderManager loaderManager = getActivity().getSupportLoaderManager();
        Loader loader = loaderManager.getLoader(CursorLoaderIds.ALL_CONVERSATION_LOADER);
        if (loader == null){
            loaderManager.initLoader(CursorLoaderIds.ALL_CONVERSATION_LOADER, null, allConversationLoaderCallback);
            loaderManager.initLoader(CursorLoaderIds.GROUP_CONVERSATION_LOADER, null, groupConversationLoaderCallback);
        } else {
            loaderManager.restartLoader(CursorLoaderIds.ALL_CONVERSATION_LOADER, null, allConversationLoaderCallback);
            loaderManager.restartLoader(CursorLoaderIds.GROUP_CONVERSATION_LOADER, null, groupConversationLoaderCallback);
        }
    }

    @Override
    public void newUserSelected(String userName) {
        user = new User(userName);
        Log.e("NEW USER", userName);
        connect();
    }

    private void connect(){
        messengerServerFacade.addAuthorizationListener(new AuthorizeListener() {
            @Override
            public void onSuccess() {
                Log.e("Xmpp server", "Authorized");
                loadConversationList();
            }
        });
        messengerServerFacade.authorizeAsync(user.getUserName(), user.getUserName());
    }

    @Override public void applyViewState() {
        if (!isViewAttached()) {
            return;
        }
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

    @Override public void onConversationSelected(Conversation conversation) {
        Intent intent = new Intent(getContext(), ChatActivity.class);
       // intent.putExtra(ChatScreenPresenter.EXTRA_CHAT_CONVERSATION, conversation);
        getActivity().startActivity(intent);
    }

    ///////////////////////////////////////////////////////////////////////////
    // Activity related
    ///////////////////////////////////////////////////////////////////////////

    @Override public boolean onCreateOptionsMenu(Menu menu) {
        getActivity().getMenuInflater().inflate(R.menu.conversation_list, menu);
        return true;
    }

    @Override public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_add:
                Intent intent = new Intent(getContext(), NewChatActivity.class);
                getActivity().startActivity(intent);
                return true;
        }
        return false;
    }

    @Override public void onActivityResult(int requestCode, int resultCode, Intent data) {

    }

    @Override public void onDestroy() {

    }

    ///////////////////////////////////////////////////////////////////////////
    // Helpers
    ///////////////////////////////////////////////////////////////////////////

    protected Context getContext() {
        return getView().getContext();
    }

    protected AppCompatActivity getActivity() {
        return getView().getActivity();
    }

    ///////////////////////////////////////////////////////////////////////////
    // Cursor Loader Callback
    ///////////////////////////////////////////////////////////////////////////

    private class CursorLoaderCallback implements LoaderManager.LoaderCallbacks<Cursor>{

        private boolean allConversation;

        public CursorLoaderCallback(boolean allConversation) {
            this.allConversation = allConversation;
        }

        @Override
        public Loader<Cursor> onCreateLoader(int id, Bundle args) {
            String select = allConversation ? "" : String.format("type not like '%s'", Conversation.Type.CHAT);
            return new CursorLoader(getActivity(), Conversation.CONTENT_URI,
                    null, select, null, "Messages.date ask");
        }

        @Override
        public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
            showConversation(data);
        }

        @Override
        public void onLoaderReset(Loader<Cursor> loader) {
            showConversation(null);
        }

        private void showConversation(Cursor cursor){
            Log.e("Loaded conversation", cursor.getCount()+"");
            ConversationListScreen screen = getView();
            if (screen == null) return;

            if (allConversation){
                screen.showAllConversation(cursor);
            } else {
                screen.showGroupConversation(cursor);
            }
        }
    }
}

