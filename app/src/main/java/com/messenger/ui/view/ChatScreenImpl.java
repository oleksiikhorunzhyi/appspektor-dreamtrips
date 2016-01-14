package com.messenger.ui.view;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.messenger.messengerservers.entities.Conversation;
import com.messenger.messengerservers.entities.Message;
import com.messenger.messengerservers.entities.User;
import com.messenger.ui.adapter.MessagesCursorAdapter;
import com.messenger.ui.adapter.holder.MessageHolder;
import com.messenger.ui.helper.ConversationHelper;
import com.messenger.ui.presenter.ChatScreenPresenter;
import com.messenger.ui.presenter.ChatScreenPresenterImpl;
import com.messenger.ui.presenter.ToolbarPresenter;
import com.messenger.ui.widget.ChatUsersTypingView;
import com.messenger.ui.widget.UnreadMessagesView;
import com.raizlabs.android.dbflow.sql.SqlUtils;
import com.trello.rxlifecycle.RxLifecycle;
import com.worldventures.dreamtrips.R;

import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import butterknife.OnEditorAction;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class ChatScreenImpl extends MessengerLinearLayout<ChatScreen, ChatScreenPresenter>
        implements ChatScreen {

    private static final int THRESHOLD = 5;
    private static final int POST_DELAY_TIME = 2;

    @InjectView(R.id.chat_content_view)
    ViewGroup contentView;
    @InjectView(R.id.chat_loading_view)
    View loadingView;
    @InjectView(R.id.chat_error_view)
    View errorView;

    @InjectView(R.id.chat_toolbar)
    Toolbar toolbar;
    @InjectView(R.id.chat_toolbar_title)
    TextView title;
    @InjectView(R.id.chat_toolbar_subtitle)
    TextView subtitle;
    @InjectView(R.id.chat_recycler_view)
    RecyclerView recyclerView;
    @InjectView(R.id.chat_users_unread_messages_view)
    UnreadMessagesView unreadMessagesView;
    @InjectView(R.id.chat_users_typing_view)
    ChatUsersTypingView chatUsersTypingView;

    @InjectView(R.id.chat_message_edit_text)
    EditText messageEditText;

    private ToolbarPresenter toolbarPresenter;

    private MessagesCursorAdapter adapter;
    private LinearLayoutManager linearLayoutManager;
    private ConversationHelper conversationHelper;

    private final TextWatcher messageWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            getPresenter().messageTextChanged(s.length());
        }

        @Override
        public void afterTextChanged(Editable s) {
        }
    };

    public ChatScreenImpl(Context context) {
        super(context);
        init(context);
    }

    public ChatScreenImpl(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        setOrientation(LinearLayout.VERTICAL);
        ButterKnife.inject(this, LayoutInflater.from(context).inflate(R.layout.screen_chat, this, true));
        initUi();
    }

    @SuppressWarnings("Deprecated")
    private void initUi() {
        setBackgroundColor(ContextCompat.getColor(getContext(), R.color.main_background));
        toolbarPresenter = new ToolbarPresenter(toolbar, (AppCompatActivity) getContext());
        toolbarPresenter.enableUpNavigationButton();
        toolbarPresenter.setTitle("");
        toolbarPresenter.setSubtitle("");

        recyclerView.setSaveEnabled(true);
        linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        setPostDelayObservable(linearLayoutManager);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (dy > 0) return;
                int firstVisibleItem = linearLayoutManager.findFirstVisibleItemPosition();

                if (firstVisibleItem <= THRESHOLD) {
                    getPresenter().onNextPageReached();
                }
            }
        });
        recyclerView.setItemAnimator(null);

        unreadMessagesView.setCloseButtonClickListener((view -> unreadMessagesView.hide()));
        unreadMessagesView.setUnreadMessagesClickListener((view -> {
            getPresenter().onUnreadMessagesHeaderClicked();
        }));
    }

    private void setPostDelayObservable(LinearLayoutManager linearLayoutManager) {
        Observable.<Integer>create(subscriber ->
                recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                    @Override
                    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                        int visibleItemCount = linearLayoutManager.getChildCount();
                        int firstVisibleItem = linearLayoutManager.findFirstVisibleItemPosition();
                        subscriber.onNext(firstVisibleItem + visibleItemCount - 1); //cause first visible item is included to visibleItemCount
                    }
                }))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .compose(RxLifecycle.bindView(this))
                .map(pos -> {
                    Cursor cursor = adapter.getCursor();
                    // TODO: 12/31/15  cursor.moveToPosition(pos) -- is it safely? for example, cursor use a few threads
                    return new Pair<>(!cursor.isClosed() && cursor.moveToPosition(pos), cursor);
                })
                .filter(booleanCursorPair -> booleanCursorPair.first)
                .map(cursorPair -> SqlUtils.convertToModel(true, Message.class, cursorPair.second))
                .subscribe(message -> {
                    getPresenter().firstVisibleMessageChanged(message);
                });
    }

    @Override
    protected void onAttachedToWindow() {
        conversationHelper = new ConversationHelper();
        super.onAttachedToWindow();
        recyclerView.setAdapter(adapter = createAdapter());
        messageEditText.addTextChangedListener(messageWatcher);
    }

    protected MessagesCursorAdapter createAdapter() {
        MessagesCursorAdapter adapter = new MessagesCursorAdapter(getContext(), getPresenter().getUser(), null);
        ChatScreenPresenter presenter = getPresenter();
        adapter.setOnRepeatMessageSend(presenter::retrySendMessage);
        adapter.setAvatarClickListener(presenter::openUserProfile);
        //adapter.setMessageClickListener(message -> //do something);
        return adapter;
    }

    @OnEditorAction(R.id.chat_message_edit_text)
    protected boolean onEditorAction(int action) {
        if (action == EditorInfo.IME_ACTION_GO) {
            onSendMessage();
            return true;
        }
        return false;
    }

    @OnClick(R.id.chat_message_send_button)
    protected void onSendMessage() {
        if (getPresenter().sendMessage(messageEditText.getText().toString())) {
            messageEditText.getText().clear();
            recyclerView.smoothScrollToPosition(adapter.getItemCount());
        }
    }

    @Override
    public AppCompatActivity getActivity() {
        return (AppCompatActivity) getContext();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return getPresenter().onCreateOptionsMenu(menu);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        getPresenter().onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return getPresenter().onOptionsItemSelected(item);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        getPresenter().onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onDestroy() {
        getPresenter().onDestroy();
    }

    @NonNull
    @Override
    public ChatScreenPresenter createPresenter() {
        return new ChatScreenPresenterImpl(getContext(), getActivity().getIntent());
    }

    @Override
    public void showLoading() {
        loadingView.setVisibility(View.VISIBLE);
        errorView.setVisibility(View.GONE);
    }

    @Override
    public void showContent() {
        contentView.setVisibility(View.VISIBLE);
        loadingView.setVisibility(View.GONE);
        errorView.setVisibility(View.GONE);
    }

    @Override
    public void showError(Throwable e) {
        contentView.setVisibility(View.GONE);
        loadingView.setVisibility(View.GONE);
        errorView.setVisibility(View.VISIBLE);
    }

    @Override
    public void setTitle(Conversation conversation, List<User> members) {
        conversationHelper.setTitle(title, conversation, members, true);
        conversationHelper.setSubtitle(subtitle, conversation, members);
    }

    public void showUnreadMessageCount(int unreadMessagesCount) {
        if (unreadMessagesCount > 0) {
            unreadMessagesView.updateCount(unreadMessagesCount);
            unreadMessagesView.show();
        } else {
            unreadMessagesView.hide();
        }
    }

    @Override
    public void addTypingUser(User user) {
        chatUsersTypingView.addTypingUser(user);
    }

    @Override
    public void removeTypingUser(User user) {
        chatUsersTypingView.removeTypingUser(user);
    }

    @Override
    public void showMessages(Cursor cursor, Conversation conversation, boolean pendingScroll) {
        adapter.setConversation(conversation);

        int firstVisibleViewTop = 0;
        int cursorCountDiff = 0;
        View firstItemView = null;
        if (cursor != null && adapter.getCursor() != null) {
            int firstItem = linearLayoutManager.findFirstVisibleItemPosition();
            firstItemView = linearLayoutManager.findViewByPosition(firstItem);
            if (firstItemView != null) {
                firstVisibleViewTop = firstItemView.getTop();
            }
            cursorCountDiff = Math.max(0, cursor.getCount() - adapter.getCursor().getCount());
        }

        adapter.changeCursor(cursor);

        if (firstItemView != null && cursorCountDiff > 0) {
            int position = linearLayoutManager.findFirstVisibleItemPosition() + cursorCountDiff;

            // to calculate proper offset measure if first visible view will be different in height
            // (e.g. because of the missing date divider) when new cursor will is assigned
            MessageHolder messageHolder = adapter.onCreateViewHolder(recyclerView,
                    adapter.getItemViewType(position));
            adapter.onBindViewHolderCursor(messageHolder, cursor);
            messageHolder.itemView.measure(MeasureSpec.UNSPECIFIED, MeasureSpec.UNSPECIFIED);
            int diffHeight = firstItemView.getHeight() - messageHolder.itemView.getMeasuredHeight();
            int offset = firstVisibleViewTop + diffHeight;

            linearLayoutManager.scrollToPositionWithOffset(position, offset);
        } else if (cursor != null && cursor.getCount() == 1) {
            getPresenter().firstVisibleMessageChanged(SqlUtils.convertToModel(false, Message.class, cursor));
        }
    }

    @Override
    public void smoothScrollToPosition(int position) {
        recyclerView.smoothScrollToPosition(position);
    }

    @Override
    public int getFirstVisiblePosition() {
        if (linearLayoutManager == null) {
            return -1;
        }
        return linearLayoutManager.findFirstVisibleItemPosition();
    }

    @Override
    public int getLastVisiblePosition() {
        if (linearLayoutManager == null) {
            return -1;
        }
        return linearLayoutManager.findLastVisibleItemPosition();
    }

    @Override
    public Cursor getCurrentMessagesCursor() {
        if (adapter == null) {
            return null;
        }
        return adapter.getCursor();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        messageEditText.removeTextChangedListener(messageWatcher);
    }

    @Override
    public ViewGroup getContentView() {
        return contentView;
    }
}
