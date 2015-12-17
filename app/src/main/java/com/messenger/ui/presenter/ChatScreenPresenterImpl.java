package com.messenger.ui.presenter;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.messenger.loader.MessageLoader;
import com.messenger.messengerservers.ChatManager;
import com.messenger.messengerservers.ConnectionException;
import com.messenger.messengerservers.MessengerServerFacade;
import com.messenger.messengerservers.chat.Chat;
import com.messenger.messengerservers.entities.Message;
import com.messenger.messengerservers.entities.User;
import com.messenger.ui.activity.ChatActivity;
import com.messenger.ui.view.ChatScreen;
import com.messenger.ui.viewstate.ChatLayoutViewState;
import com.techery.spares.session.SessionHolder;
import com.worldventures.dreamtrips.App;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.session.UserSession;

import java.util.Locale;

import javax.inject.Inject;

public abstract class ChatScreenPresenterImpl extends BaseViewStateMvpPresenter<ChatScreen, ChatLayoutViewState>
        implements ChatScreenPresenter {
//    private ChatConversation chatConversation;
    private LoaderManager loaderManager;

    public static final int CONVERSATION_LOADER_ID = 0x3311;

    @Inject SessionHolder<UserSession> appSessionHolder;
    @Inject MessengerServerFacade messengerServerFacade;

    private Chat chat;
    protected Intent startIntent;

    public ChatScreenPresenterImpl(Intent startIntent) {
        this.startIntent = startIntent;
    }

    private LoaderManager.LoaderCallbacks<Cursor> loaderCallback = new LoaderManager.LoaderCallbacks<Cursor>() {
        @Override
        public Loader<Cursor> onCreateLoader(int id, Bundle args) {
//            getView().showLoading();
//            getViewState().setLoadingState(ChatLayoutViewState.LoadingState.LOADING);
            return new MessageLoader(getContext(), startIntent.getStringExtra(ChatActivity.EXTRA_CHAT_CONVERSATION_ID));
        }

        @Override
        public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
            //noinspection all
            getView().onConversationCursorLoaded(data);
        }

        @Override
        public void onLoaderReset(Loader<Cursor> loader) {
        }
    };

    @Override
    public void attachView(ChatScreen view) {
        super.attachView(view);
        loaderManager = getActivity().getSupportLoaderManager();
        loaderManager.initLoader(CONVERSATION_LOADER_ID, null, loaderCallback);
        ((App) view.getContext().getApplicationContext()).getObjectGraph().inject(this);
        chat = createChat(messengerServerFacade.getChatManager());
    }


    protected abstract Chat createChat(ChatManager chatManager);

//    @Override public void loadChatConversation() {
//        getActivity().getSupportLoaderManager()
//                .initLoader(CONVERSATION_LOADER_ID, null, loaderCallback);
        // create new or load existing conversation
//        SimpleLoader<ChatConversation> loader = LoaderModule
//                .getChatConversationLoader(getViewState().getData());
//        loader.loadData(new SimpleLoader.LoadListener<ChatConversation>() {
//            @Override public void onLoadSuccess(ChatConversation data) {
//                if (isViewAttached()) {
//                    getViewState().setData(data);
//                    getViewState().setLoadingState(ChatLayoutViewState.LoadingState.CONTENT);
//                    getView().setChatConversation(data);
//                    getView().showContent();
//                }
//            }
//
//            @Override public void onError(Throwable error) {
//                if (isViewAttached()) {
//                    getView().showError(error);
//                    getViewState().setLoadingState(ChatLayoutViewState.LoadingState.ERROR);
//                }
//            }
//        });
//    }

    @Override public void onNewViewState() {
        state = new ChatLayoutViewState();
//        getViewState().setData(chatConversation);
//        loadChatConversation();
    }

    @Override public ChatLayoutViewState getViewState() {
        return state;
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

    @Override public boolean onNewMessageFromUi(String message) {
        if (TextUtils.getTrimmedLength(message) == 0) {
            Toast.makeText(getContext(), R.string.chat_message_toast_empty_message_error, Toast.LENGTH_SHORT).show();
            return false;
        }

        try {
            chat.sendMessage(new Message.Builder()
                            .locale(Locale.getDefault())
                            .text(message)
                            .build()
            );
        } catch (ConnectionException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

//    public void onEvent(ChatMessageEvent event) {
//        getViewState().getData().getMessages().add(event.chatMessage);
//        applyViewState();
//    }
//
//    public void onEvent(ChatUsersTypingEvent usersTypingEvent) {
//        getViewState().getData().setTypingUsers(usersTypingEvent.typingUsers);
//        applyViewState();
//    }

//    @Override public void setChatConversation(ChatConversation chatConversation) {
//        this.chatConversation = chatConversation;
//    }

    @Override
    public User getUser() {
        // TODO: 12/15/15  
        return messengerServerFacade.getOwner();
    }

    @Override
    public void detachView(boolean retainInstance) {
        super.detachView(retainInstance);
        loaderManager.destroyLoader(CONVERSATION_LOADER_ID);
    }

    ///////////////////////////////////////////////////////////////////////////
    // Activity related
    ///////////////////////////////////////////////////////////////////////////

    @Override public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = ((AppCompatActivity) getContext()).getMenuInflater();
        inflater.inflate(R.menu.chat, menu);
        return true;
    }

    @Override public boolean onOptionsItemSelected(MenuItem item) {
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
}
