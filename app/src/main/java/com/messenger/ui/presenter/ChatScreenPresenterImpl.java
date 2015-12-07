package com.messenger.ui.presenter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.messenger.event.ChatMessageEvent;
import com.messenger.messengerservers.ConnectionException;
import com.messenger.messengerservers.chat.Chat;
import com.messenger.messengerservers.entities.Message;
import com.messenger.messengerservers.entities.User;
import com.messenger.messengerservers.listeners.ChatMessageListener;
import com.messenger.model.ChatConversation;
import com.messenger.ui.activity.ChatActivity;
import com.messenger.ui.view.ChatScreen;
import com.messenger.ui.viewstate.ChatLayoutViewState;

import java.util.Locale;


public class ChatScreenPresenterImpl extends BaseViewStateMvpPresenter<ChatScreen>
        implements ChatScreenPresenter {
    private Chat chat;

    private ChatMessageListener chatMessageListener = this::handleMessage;
    private ChatConversation chatConversation;

    private void handleMessage(Message message, User user) {
        //noinspection all
        getView().onReceiveMessage(message);
    }

    @Override
    public void attachView(ChatScreen view) {
        super.attachView(view);
        chat = ((ChatActivity) getActivity()).getChat();
        chat.addOnChatMessageListener(chatMessageListener);
    }

    @Override
    public void detachView(boolean retainInstance) {
        super.detachView(retainInstance);
        chat.removeOnChatMessageListener(chatMessageListener);
    }

    @Override public void loadChatConversation() {
//        final ChatScreen view = getView();
//        assert view != null;
//
//        view.showLoading();
//        getViewState().setLoadingState(ChatLayoutViewState.LoadingState.LOADING);
//
//        // create new or load existing conversation
//        SimpleLoader<ChatConversation> loader = LoaderModule
//                .getChatConversationLoader(getViewState().getChatConversation());
//        loader.loadData(new SimpleLoader.LoadListener<ChatConversation>() {
//            @Override public void onLoadSuccess(ChatConversation data) {
//                if (isViewAttached()) {
//                    getViewState().setChatConversation(data);
//                    getViewState().setLoadingState(ChatLayoutViewState.LoadingState.CONTENT);
//                    view.setChatConversation(data);
//                    view.showContent();
//                }
//            }
//
//            @Override public void onError(Throwable error) {
//                if (isViewAttached()) {
//                    view.showError(error);
//                    getViewState().setLoadingState(ChatLayoutViewState.LoadingState.ERROR);
//                }
//            }
//        });
    }

    @Override public void onNewViewState() {
//        state = new ChatLayoutViewState();
//        getViewState().setChatConversation(chatConversation);
//        loadChatConversation();
//        EventBus.getDefault().register(this);
    }

    @Override public ChatLayoutViewState getViewState() {
        return (ChatLayoutViewState) state;
    }

    @Override public void applyViewState() {
        switch (getViewState().getLoadingState()) {
            case LOADING:
//                getView().showLoading();
                break;
            case CONTENT:
                getView().showContent();
                break;
            case ERROR:
                getView().showError(getViewState().getError());
                break;
        }
        if (getViewState().getChatConversation() != null) {
            getView().setChatConversation(getViewState().getChatConversation());
        }
    }

    @Override public boolean onNewMessageFromUi(String message) {
        if (TextUtils.isEmpty(message)) {
            Toast.makeText(getContext(), "Provide some message", Toast.LENGTH_SHORT).show();
            return false;
        }

        try {
            Message msg;
            chat.sendMessage(msg = new Message.Builder()
                    .locale(Locale.getDefault())
                    .from((User) chatConversation.getChatUsers().get(0))
                    .text(message)
                    .build());

            //noinspection all
            getView().onSendMessage(msg);
        } catch (ConnectionException e) {
            Log.d("TEST", "send message", e);
        }

//        final ChatUser conversationOwner = getViewState().getChatConversation().getConversationOwner();
//        ChatMessage chatMessage = Environment.newChatMessage();
//        chatMessage.setUser(conversationOwner);
//        chatMessage.setMessage(message);
//        chatMessage.setDate(new Date());
//
//        EventBus.getDefault().post(new ChatMessageEvent(chatMessage));
        return true;
    }

    public void onEvent(ChatMessageEvent event) {
        getViewState().getChatConversation().getMessages().add(event.chatMessage);
        applyViewState();
    }

    @Override public void setChatConversation(ChatConversation chatConversation) {
        this.chatConversation = chatConversation;
    }

    ///////////////////////////////////////////////////////////////////////////
    // Action Bar
    ///////////////////////////////////////////////////////////////////////////

    @Override public boolean onCreateOptionsMenu(Menu menu) {
//        MenuInflater inflater = ((AppCompatActivity)getContext()).getMenuInflater();
//        inflater.inflate(R.menu.new_chat, menu);
        return false;
    }

    @Override public boolean onOptionsItemSelected(MenuItem item) {
        return false;
    }

    @Override public void onActivityResult(int requestCode, int resultCode, Intent data) {

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
