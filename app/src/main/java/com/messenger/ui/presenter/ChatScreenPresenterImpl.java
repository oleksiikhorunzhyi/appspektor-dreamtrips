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
import android.widget.Toast;

import com.messenger.constant.CursorLoaderIds;
import com.messenger.delegate.PaginationDelegate;
import com.messenger.loader.MessageLoader;
import com.messenger.messengerservers.ChatManager;
import com.messenger.messengerservers.ConnectionException;
import com.messenger.messengerservers.MessengerServerFacade;
import com.messenger.messengerservers.chat.Chat;
import com.messenger.messengerservers.entities.Conversation;
import com.messenger.messengerservers.entities.Message;
import com.messenger.messengerservers.entities.User;
import com.messenger.ui.activity.ChatActivity;
import com.messenger.ui.view.ChatScreen;
import com.messenger.ui.viewstate.ChatLayoutViewState;
import com.raizlabs.android.dbflow.sql.language.Select;
import com.techery.spares.module.Injector;
import com.techery.spares.session.SessionHolder;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.session.UserSession;

import java.util.Locale;

import javax.inject.Inject;

public abstract class ChatScreenPresenterImpl extends BaseViewStateMvpPresenter<ChatScreen, ChatLayoutViewState>
        implements ChatScreenPresenter {

    @Inject SessionHolder<UserSession> appSessionHolder;
    @Inject MessengerServerFacade messengerServerFacade;
    @Inject User user;

    private Chat chat;
    protected Intent startIntent;

    private LoaderManager loaderManager;
    protected PaginationDelegate paginationDelegate;

    protected Conversation conversation;
    protected int page = 0;
    protected int before = 0;
    protected boolean haveMoreElements = true;
    protected boolean isLoading;
    protected boolean pendingScroll;

    public ChatScreenPresenterImpl(Context context, Intent startIntent) {
        this.startIntent = startIntent;
        ((Injector)context.getApplicationContext()).inject(this);
        paginationDelegate = new PaginationDelegate(context, messengerServerFacade, 20);

        String conversationId = startIntent.getStringExtra(ChatActivity.EXTRA_CHAT_CONVERSATION_ID);
        conversation = new Select()
                .from(Conversation.class)
                .byIds(conversationId)
                .querySingle();
    }

    private LoaderManager.LoaderCallbacks<Cursor> loaderCallback = new LoaderManager.LoaderCallbacks<Cursor>() {
        @Override
        public Loader<Cursor> onCreateLoader(int id, Bundle args) {
            return new MessageLoader(getContext(), startIntent.getStringExtra(ChatActivity.EXTRA_CHAT_CONVERSATION_ID));
        }

        @Override
        public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
            //noinspection all
            getView().onConversationCursorLoaded(data, conversation, pendingScroll);
            pendingScroll = false;
        }

        @Override
        public void onLoaderReset(Loader<Cursor> loader) {
        }
    };

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        loadNextPage();
        loaderManager = getActivity().getSupportLoaderManager();
        loaderManager.initLoader(CursorLoaderIds.CONVERSATION_LOADER, null, loaderCallback);
        chat = createChat(messengerServerFacade.getChatManager(), conversation);
    }

    @Override
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        loaderManager.destroyLoader(CursorLoaderIds.CONVERSATION_LOADER);
        paginationDelegate.stopPaginate();
    }

    @Override
    public void onNextPageReached() {
        if (!isLoading) loadNextPage();
    }

    private void loadNextPage() {
        isLoading = true;
        ChatLayoutViewState viewState = getViewState();
        if (!haveMoreElements || viewState.getLoadingState() == ChatLayoutViewState.LoadingState.LOADING)
            return;

        getView().showLoading();
        viewState.setLoadingState(ChatLayoutViewState.LoadingState.LOADING);
        paginationDelegate.loadConversationHistoryPage(conversation, ++page, before,
                (loadedPage, haveMoreElements, lastMessage) -> {
                    viewState.setLoadingState(ChatLayoutViewState.LoadingState.CONTENT);
                    this.haveMoreElements = haveMoreElements;

                    if (lastMessage != null){
                        ChatScreenPresenterImpl.this.before = (int)(lastMessage.getDate().getTime() / 1000);
                    }

                    showContent();
                    isLoading = false;
                }, () -> showContent());
    }

    private void showContent(){
        ChatScreen screen = getView();
        if (screen == null) return;
        screen.getActivity().runOnUiThread(() -> screen.showContent());
    }

    protected abstract Chat createChat(ChatManager chatManager, Conversation conversation);

    @Override
    public void onNewViewState() {
        state = new ChatLayoutViewState();
        state.setLoadingState(ChatLayoutViewState.LoadingState.CONTENT);
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
                getView().showContent();
                break;
            case ERROR:
                getView().showError(getViewState().getError());
                break;
        }
    }

    @Override
    public boolean onNewMessageFromUi(String message) {
        if (TextUtils.getTrimmedLength(message) == 0) {
            Toast.makeText(getContext(), R.string.chat_message_toast_empty_message_error, Toast.LENGTH_SHORT).show();
            return false;
        }

        try {
            pendingScroll = true;
            chat.sendMessage(new Message.Builder()
                    .locale(Locale.getDefault())
                    .text(message)
                    .from(user)
                    .build());
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
        return user;
    }
    ///////////////////////////////////////////////////////////////////////////
    // Activity related
    ///////////////////////////////////////////////////////////////////////////

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = ((AppCompatActivity) getContext()).getMenuInflater();
        inflater.inflate(R.menu.chat, menu);
        return true;
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
