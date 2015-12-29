package com.messenger.ui.presenter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.badoo.mobile.util.WeakHandler;
import com.messenger.delegate.PaginationDelegate;
import com.messenger.delegate.ProfileCrosser;
import com.messenger.messengerservers.ChatManager;
import com.messenger.messengerservers.ChatState;
import com.messenger.messengerservers.ConnectionException;
import com.messenger.messengerservers.MessengerServerFacade;
import com.messenger.messengerservers.chat.Chat;
import com.messenger.messengerservers.entities.Conversation;
import com.messenger.messengerservers.entities.Message;
import com.messenger.messengerservers.entities.ParticipantsRelationship;
import com.messenger.messengerservers.entities.Status;
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
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.navigation.creator.ProfileRouteCreator;
import com.worldventures.dreamtrips.core.session.UserSession;

import java.util.Collections;
import java.util.List;
import java.util.Locale;

import javax.inject.Inject;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import timber.log.Timber;

public abstract class ChatScreenPresenterImpl extends BaseViewStateMvpPresenter<ChatScreen, ChatLayoutViewState>
        implements ChatScreenPresenter {

    private final static int MAX_MESSAGE_PER_PAGE = 20;
    private static final int UNREAD_DELAY = 2000;

    @Inject
    SessionHolder<UserSession> appSessionHolder;
    @Inject
    MessengerServerFacade messengerServerFacade;
    @Inject
    User user;

    private Activity activity;
    protected PaginationDelegate paginationDelegate;
    protected ProfileCrosser profileCrosser;
    protected WeakHandler handler;

    protected Conversation conversation;
    private Chat chat;

    protected int page = 0;
    protected long before = 0;
    protected boolean haveMoreElements = true;
    protected boolean isLoading = false;
    protected boolean pendingScroll = false;
    protected boolean typing;

    private List<User> participants;

    public ChatScreenPresenterImpl(Context context, Intent startIntent) {
        this.activity = (Activity) context;
        handler = new WeakHandler();

        ((Injector) context.getApplicationContext()).inject(this);
        String conversationId = startIntent.getStringExtra(ChatActivity.EXTRA_CHAT_CONVERSATION_ID);
        participants = Collections.emptyList();
        obtainConversation(conversationId);
        initializeChat();

        paginationDelegate = new PaginationDelegate(context, messengerServerFacade, MAX_MESSAGE_PER_PAGE);
        profileCrosser = new ProfileCrosser(context, new ProfileRouteCreator(appSessionHolder));
    }

    private void obtainConversation(String conversationId) {
        conversation = new Select()
                .from(Conversation.class)
                .byIds(conversationId)
                .querySingle();
    }

    private void initializeChat() {
        chat = createChat(messengerServerFacade.getChatManager(), conversation);
        chat.addOnChatStateListener((state1, userId) -> {
            ChatScreen view = getView();
            if (view == null) return;
            switch (state1) {
                case Composing:
                    handler.post(() -> view.addTypingUser(new Select().from(User.class).byIds(userId).querySingle()));
                    break;
                case Paused:
                    handler.post(() -> view.removeTypingUser(new Select().from(User.class).byIds(userId).querySingle()));
                    break;
            }
        });
    }

    private void reloadConversation() {
        if (conversation != null) obtainConversation(conversation.getId());
    }

    @Override
    public void messageTextChanged(int length) {
        if (!typing && length > 0) {
            typing = true;
            chat.setCurrentState(ChatState.Composing);
        } else if (length == 0) {
            typing = false;
            chat.setCurrentState(ChatState.Paused);
        }
    }

    @Override
    public void attachView(ChatScreen view) {
        super.attachView(view);
        view.setTitle(conversation, Collections.emptyList());
        view.showUnreadMessageCount(conversation.getUnreadMessageCount());
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        loadNextPage();
        connectMembers();
    }

    @Override
    public void onVisibilityChanged(int visibility) {
        super.onVisibilityChanged(visibility);
        if (visibility == View.VISIBLE) {
            reloadConversation();
            getView().setTitle(conversation, participants);
        }
    }

    protected void connectMembers() {
        RxContentResolver.Query q = new RxContentResolver.Query.Builder(null)
                .withSelection("SELECT * FROM Users u " +
                                "JOIN ParticipantsRelationship p " +
                                "ON p.userId = u._id " +
                                "WHERE p.conversationId = ?"
                ).withSelectionArgs(new String[]{conversation.getId()}).build();
        new RxContentResolver(activity.getContentResolver(), query -> {
            return FlowManager.getDatabaseForTable(User.class).getWritableDatabase().rawQuery(query.selection, query.selectionArgs);
        })
                .query(q, User.CONTENT_URI, ParticipantsRelationship.CONTENT_URI)
                .onBackpressureLatest()
                .map(c -> SqlUtils.convertToList(User.class, c))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .compose(bindVisibility())
                .subscribe(members -> {
                    participants = members;
                    getView().setTitle(conversation, participants);
                });
    }

    @Override
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        paginationDelegate.stopPaginate();
    }

    @Override
    public void firstVisibleMessageChanged(Message firstVisibleMessage) {
        if (firstVisibleMessage.isRead()) return;

        sendAndMarkChatEntities(firstVisibleMessage);
    }

    @Override
    public void openUserProfile(User user) {
        profileCrosser.crossToProfile(user);
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
                this::paginationPageLoaded,
                this::showContent);
    }

    private void paginationPageLoaded(int loadedPage, List<Message> loadedMessages) {
        getViewState().setLoadingState(ChatLayoutViewState.LoadingState.CONTENT);
        isLoading = false;
        if (loadedMessages == null || loadedMessages.size() == 0) {
            haveMoreElements = false;
            showContent();
            return;
        }

        int loadedCount = loadedMessages.size();
        haveMoreElements = loadedCount == MAX_MESSAGE_PER_PAGE;
        Message lastMessage = loadedMessages.get(loadedCount - 1);

        before = lastMessage.getDate().getTime();
        showContent();
    }

    private void sendAndMarkChatEntities(Message firstMessage) {
        chat.changeMessageStatus(firstMessage, Status.DISPLAYED);

        markMessagesAsRead(firstMessage);
        updateUnreadMessageCount(firstMessage);
        showUnreadMessageCount();
    }

    private void markMessagesAsRead(Message firstMessage){
        String clause = Message.COLUMN_CONVERSATION_ID + " = ? "
                + "AND " + Message.COLUMN_DATE + " <= ? "
                + "AND " + Message.COLUMN_READ + " = ? ";
        Cursor cursor = new Select()
                .from(Message.class)
                .where(clause, conversation.getId(), firstMessage.getDate().getTime(), 0)
                .query();

        if (!cursor.moveToFirst()) return;

        do {
            Message message = SqlUtils.convertToModel(true, Message.class, cursor);
            message.setRead(true);
            message.save();
        } while (cursor.moveToNext());

        cursor.close();
    }

    private void updateUnreadMessageCount(Message firstMessage) {
        String clause = Message.COLUMN_CONVERSATION_ID + " = ? "
                + "AND " + Message.COLUMN_DATE + " > ? "
                + "AND " + Message.COLUMN_READ + " = ? ";
        Cursor cursor = new Select()
                .count()
                .from(Message.class)
                .where(clause, conversation.getId(), firstMessage.getDate().getTime(), 0).query();

        int unreadCount = cursor.moveToFirst()? cursor.getInt(0) : 0;
        cursor.close();

        conversation.setUnreadMessageCount(unreadCount);
        conversation.save();
    }

    private void showUnreadMessageCount() {
        handler.postDelayed(() -> {
            ChatScreen view = getView();
            if (view != null) view.showUnreadMessageCount(conversation.getUnreadMessageCount());
        }, UNREAD_DELAY);
    }

    private void showContent() {
        ChatScreen screen = getView();
        if (screen == null) return;
        screen.getActivity().runOnUiThread(screen::showContent);
    }

    protected abstract Chat createChat(ChatManager chatManager, Conversation conversation);

    @Override
    public void onNewViewState() {
        state = new ChatLayoutViewState();
        state.setLoadingState(ChatLayoutViewState.LoadingState.CONTENT);
    }

    @Override
    public void applyViewState() {
        if (!isViewAttached()) return;

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
            Toast.makeText(activity, R.string.chat_message_toast_empty_message_error, Toast.LENGTH_SHORT).show();
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
        return user;
    }
    ///////////////////////////////////////////////////////////////////////////
    // Activity related
    ///////////////////////////////////////////////////////////////////////////

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        activity.getMenuInflater().inflate(R.menu.chat, menu);
        return true;
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        // hide button for adding user for not owners of group chats
        if (conversation.getType().equals(Conversation.Type.GROUP)) {
            Timber.i("user.getId(): " + user.getId() + ", get owner conv id: "
                    + conversation.getOwnerId() + ", conv id: " + conversation.getId());
            boolean owner = user.getId().equals(conversation.getOwnerId());
            if (!owner) {
                menu.findItem(R.id.action_add).setVisible(false);
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_add:
                NewChatMembersActivity.startInAddMembersMode(activity, conversation.getId());
                return true;
            case R.id.action_settings:
                if (conversation.getType().equals(Conversation.Type.CHAT)) {
                    ChatSettingsActivity.startSingleChatSettings(activity, conversation.getId());
                } else {
                    ChatSettingsActivity.startGroupChatSettings(activity, conversation.getId());
                }
                return true;
        }
        return false;
    }

}
