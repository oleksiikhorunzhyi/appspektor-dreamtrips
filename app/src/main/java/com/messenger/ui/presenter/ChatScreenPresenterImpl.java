package com.messenger.ui.presenter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
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
import com.messenger.messengerservers.entities.ParticipantsRelationship;
import com.messenger.messengerservers.entities.User;
import com.messenger.ui.activity.ChatActivity;
import com.messenger.ui.activity.ChatSettingsActivity;
import com.messenger.ui.activity.NewChatMembersActivity;
import com.messenger.ui.view.ChatScreen;
import com.messenger.ui.viewstate.ChatLayoutViewState;
import com.messenger.util.RxContentResolver;
import com.raizlabs.android.dbflow.config.FlowManager;
import com.raizlabs.android.dbflow.sql.SqlUtils;
import com.raizlabs.android.dbflow.sql.language.Select;
import com.techery.spares.module.Injector;
import com.techery.spares.session.SessionHolder;
import com.trello.rxlifecycle.RxLifecycle;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.session.UserSession;

import java.util.Locale;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public abstract class ChatScreenPresenterImpl extends BaseViewStateMvpPresenter<ChatScreen, ChatLayoutViewState>
        implements ChatScreenPresenter {

    private static final int REQUEST_CODE_ADD_USER = 33;

    @Inject SessionHolder<UserSession> appSessionHolder;
    @Inject MessengerServerFacade messengerServerFacade;
    @Inject User user;

    private Activity activity;

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
        this.activity = (Activity) context;
        this.startIntent = startIntent;
        ((Injector)context.getApplicationContext()).inject(this);
        String conversationId = startIntent.getStringExtra(ChatActivity.EXTRA_CHAT_CONVERSATION_ID);
        init(conversationId);
    }

    private void init(String conversationId) {
        paginationDelegate = new PaginationDelegate(activity, messengerServerFacade, 20);

        conversation = new Select()
                .from(Conversation.class)
                .byIds(conversationId)
                .querySingle();

        page = 0;
        before = 0;
        haveMoreElements = true;
        isLoading = false;
        pendingScroll = false;
    }

    private void initLoadersAndCreateChat() {
        loadNextPage();
        loaderManager = getActivity().getSupportLoaderManager();
        loaderManager.initLoader(CursorLoaderIds.CONVERSATION_LOADER, null, loaderCallback);
        chat = createChat(messengerServerFacade.getChatManager(), conversation);
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
        initLoadersAndCreateChat();
        connectMembers();
    }

    protected void connectMembers() {
        RxContentResolver.Query q = new RxContentResolver.Query.Builder(null)
                .withSelection("SELECT * FROM Users u " +
                        "JOIN ParticipantsRelationship p " +
                        "ON p.userId = u._id " +
                        "WHERE p.conversationId = ?"
                ).withSelectionArgs(new String[]{conversation.getId()}).build();
        new RxContentResolver(getContext().getContentResolver(), query -> {
            return FlowManager.getDatabaseForTable(User.class).getWritableDatabase().rawQuery(query.selection, query.selectionArgs);
        })
                .query(q, User.CONTENT_URI, ParticipantsRelationship.CONTENT_URI)
                .throttleLast(500, TimeUnit.MILLISECONDS)
                .map(c -> SqlUtils.convertToList(User.class, c))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .compose(RxLifecycle.bindView(((View) getView())))
                .subscribe(members -> {
                    getView().setTitle(conversation, members);
                });
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

    private void showContent() {
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
                    .from(user.getId())
                    .build());
        } catch (ConnectionException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

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

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        // hide button for adding user for not owners of group chats
        if (conversation.getType().equals(Conversation.Type.GROUP)) {
            Log.d("SMACK", "user.getId(): " + user.getId() + ", get owner conv id: "
                    + conversation.getOwnerId() + ", conv id: " + conversation.getId());
            boolean isOwner = user.getId().equals(conversation.getOwnerId());
            if (!isOwner) {
                menu.findItem(R.id.action_add).setVisible(false);
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_add:
                NewChatMembersActivity.startInAddMembersMode(getActivity(), conversation.getId(),
                        REQUEST_CODE_ADD_USER);
                return true;
            case R.id.action_settings:
                if (conversation.getType().equals(Conversation.Type.CHAT)) {
                    ChatSettingsActivity.startSingleChatSettings(getContext(), conversation.getId());
                } else {
                    ChatSettingsActivity.startGroupChatSettings(getContext(), conversation.getId());
                }
                return true;
        }
        return false;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_CODE_ADD_USER:
                if (resultCode == Activity.RESULT_OK) {
                    String conversationId = data
                            .getStringExtra(NewChatMembersActivity.EXTRA_CONVERSATION_ID);
                    // New group chat was created instead of single one
                    if (!conversation.getId().equals(conversationId)) {
                        init(conversationId);
                        initLoadersAndCreateChat();
                    }
                }
                break;
        }
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
