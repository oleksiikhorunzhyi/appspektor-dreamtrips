package com.messenger.ui.presenter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.worldventures.dreamtrips.R;
import com.messenger.app.Environment;
import com.messenger.event.ChatMessageEvent;
import com.messenger.event.ChatUsersTypingEvent;
import com.messenger.loader.LoaderModule;
import com.messenger.loader.SimpleLoader;
import com.messenger.model.ChatConversation;
import com.messenger.model.ChatMessage;
import com.messenger.model.ChatUser;
import com.messenger.ui.view.ChatScreen;
import com.messenger.ui.viewstate.ChatLayoutViewState;

import java.util.Date;

import de.greenrobot.event.EventBus;


public class ChatScreenPresenterImpl extends BaseViewStateMvpPresenter<ChatScreen, ChatLayoutViewState>
        implements ChatScreenPresenter {
    private ChatConversation chatConversation;

    public ChatScreenPresenterImpl() {
        EventBus.getDefault().register(this);
    }

    @Override public void loadChatConversation() {
        getView().showLoading();
        getViewState().setLoadingState(ChatLayoutViewState.LoadingState.LOADING);

        // create new or load existing conversation
        SimpleLoader<ChatConversation> loader = LoaderModule
                .getChatConversationLoader(getViewState().getData());
        loader.loadData(new SimpleLoader.LoadListener<ChatConversation>() {
            @Override public void onLoadSuccess(ChatConversation data) {
                if (isViewAttached()) {
                    getViewState().setData(data);
                    getViewState().setLoadingState(ChatLayoutViewState.LoadingState.CONTENT);
                    getView().setChatConversation(data);
                    getView().showContent();
                }
            }

            @Override public void onError(Throwable error) {
                if (isViewAttached()) {
                    getView().showError(error);
                    getViewState().setLoadingState(ChatLayoutViewState.LoadingState.ERROR);
                }
            }
        });
    }

    @Override public void onNewViewState() {
        state = new ChatLayoutViewState();
        getViewState().setData(chatConversation);
        loadChatConversation();
    }

    @Override public ChatLayoutViewState getViewState() {
        return (ChatLayoutViewState) state;
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
        if (getViewState().getData() != null) {
            getView().setChatConversation(getViewState().getData());
        }
    }

    @Override public boolean onNewMessageFromUi(String message) {
        if (TextUtils.isEmpty(message)) {
            Toast.makeText(getContext(), R.string.chat_message_toast_empty_message_error, Toast.LENGTH_SHORT).show();
            return false;
        }
        final ChatUser conversationOwner = getViewState().getData().getConversationOwner();
        ChatMessage chatMessage = Environment.newChatMessage();
        chatMessage.setUser(conversationOwner);
        chatMessage.setMessage(message);
        chatMessage.setDate(new Date());

        EventBus.getDefault().post(new ChatMessageEvent(chatMessage));
        return true;
    }

    public void onEvent(ChatMessageEvent event) {
        getViewState().getData().getMessages().add(event.chatMessage);
        applyViewState();
    }

    public void onEvent(ChatUsersTypingEvent usersTypingEvent) {
        getViewState().getData().setTypingUsers(usersTypingEvent.typingUsers);
        applyViewState();
    }

    @Override public void setChatConversation(ChatConversation chatConversation) {
        this.chatConversation = chatConversation;
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
        EventBus.getDefault().unregister(this);
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
