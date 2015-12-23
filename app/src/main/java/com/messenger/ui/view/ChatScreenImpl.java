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
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.messenger.messengerservers.entities.Conversation;
import com.messenger.messengerservers.entities.User;
import com.messenger.ui.activity.ChatActivity;
import com.messenger.ui.adapter.MessagesCursorAdapter;
import com.messenger.ui.helper.ConversationHelper;
import com.messenger.ui.presenter.ChatGroupScreenPresenter;
import com.messenger.ui.presenter.ChatScreenPresenter;
import com.messenger.ui.presenter.ChatSingleScreenPresenter;
import com.messenger.ui.presenter.ToolbarPresenter;
import com.messenger.ui.widget.ChatUsersTypingView;
import com.worldventures.dreamtrips.R;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import butterknife.OnEditorAction;

public class ChatScreenImpl extends BaseViewStateLinearLayout<ChatScreen, ChatScreenPresenter>
        implements ChatScreen {

    @InjectView(R.id.chat_content_view)
    View contentView;
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
    @InjectView(R.id.chat_users_typing_view)
    ChatUsersTypingView chatUsersTypingView;

    @InjectView(R.id.chat_message_edit_text)
    EditText messageEditText;

    private ToolbarPresenter toolbarPresenter;

    private MessagesCursorAdapter adapter;
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
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (dy > 0) return;
                final int threshold = 5;
                int visibleItemCount = linearLayoutManager.getChildCount();
                int totalItemCount = linearLayoutManager.getItemCount();
                int firstVisibleItem = linearLayoutManager.findFirstVisibleItemPosition();

                if (firstVisibleItem <= threshold) {
                    getPresenter().onNextPageReached();
                }
            }
        });
    }

    @Override
    protected void onAttachedToWindow() {
        conversationHelper = new ConversationHelper();
        super.onAttachedToWindow();
        recyclerView.setAdapter(adapter = new MessagesCursorAdapter(getContext(), getPresenter().getUser(), null));
        messageEditText.addTextChangedListener(messageWatcher);
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
        if (getPresenter().onNewMessageFromUi(messageEditText.getText().toString())) {
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
        Context context = getContext();
        Intent startIntent = getActivity().getIntent();
        int chatType = startIntent.getIntExtra(ChatActivity.EXTRA_CHAT_TYPE, 0);

        if (chatType == ChatActivity.CHAT_TYPE_GROUP) {
            return new ChatGroupScreenPresenter(context, startIntent);
        } else if (chatType == ChatActivity.CHAT_TYPE_SINGLE) {
            return new ChatSingleScreenPresenter(context, startIntent);
        } else {
            throw new Error("Type doesn't exist");
        }
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
        conversationHelper.setTitle(title, conversation, members);
        conversationHelper.setSubtitle(subtitle, conversation, members);
    }

    @Override
    public void addTypingUser(User user) {
        post(() -> chatUsersTypingView.addTypingUser(user));
    }

    @Override
    public void removeTypingUser(User user) {
        post(() -> chatUsersTypingView.removeTypingUser(user));
    }

    @Override
    public void onConversationCursorLoaded(Cursor cursor, Conversation conversation, boolean pendingScroll) {
        adapter.setConversation(conversation);
        adapter.changeCursor(cursor);
        if (pendingScroll) recyclerView.smoothScrollToPosition(cursor.getCount());
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        messageEditText.removeTextChangedListener(messageWatcher);
    }
}
