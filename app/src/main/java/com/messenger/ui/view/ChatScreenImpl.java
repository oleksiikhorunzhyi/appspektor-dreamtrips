package com.messenger.ui.view;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.messenger.ui.activity.ChatActivity;
import com.messenger.ui.adapter.ChatConversationCursorAdapter;
import com.messenger.ui.presenter.ChatGroupScreenPresenter;
import com.messenger.ui.presenter.ChatScreenPresenter;
import com.messenger.ui.presenter.ChatSingleScreenPresenter;
import com.messenger.ui.presenter.ToolbarPresenter;
import com.messenger.ui.widget.ChatUsersTypingView;
import com.worldventures.dreamtrips.R;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import butterknife.OnEditorAction;

public class ChatScreenImpl extends BaseViewStateLinearLayout<ChatScreen, ChatScreenPresenter>
        implements ChatScreen {

    @InjectView(R.id.chat_content_view) View contentView;
    @InjectView(R.id.chat_loading_view) View loadingView;
    @InjectView(R.id.chat_error_view) View errorView;

    @InjectView(R.id.chat_toolbar) Toolbar toolbar;
    @InjectView(R.id.chat_recycler_view) RecyclerView recyclerView;
//    @InjectView(R.id.chat_users_typing_view) ChatUsersTypingView chatUsersTypingView;

    @InjectView(R.id.chat_message_edit_text) EditText messageEditText;

    private ToolbarPresenter toolbarPresenter;

    private ChatConversationCursorAdapter adapter;

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

        recyclerView.setSaveEnabled(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        recyclerView.setAdapter(adapter = new ChatConversationCursorAdapter(getContext(), getPresenter().getUser(), null));
    }

    @OnEditorAction(R.id.chat_message_edit_text)
    protected boolean onEditorAction(int action) {
        if (action == EditorInfo.IME_ACTION_GO) {
            onSendMessage();
            return true;
        }
        return false;
    }

    @OnClick(R.id.chat_message_add_button)
    protected void onSendMessage() {
        if (getPresenter().onNewMessageFromUi(messageEditText.getText().toString())) {
            messageEditText.getText().clear();
            recyclerView.smoothScrollToPosition(adapter.getItemCount());
        }
    }

    @Override public AppCompatActivity getActivity() {
        return (AppCompatActivity) getContext();
    }

    @Override public boolean onCreateOptionsMenu(Menu menu) {
        return getPresenter().onCreateOptionsMenu(menu);
    }

    @Override public boolean onOptionsItemSelected(MenuItem item) {
        return getPresenter().onOptionsItemSelected(item);
    }

    @Override public void onActivityResult(int requestCode, int resultCode, Intent data) {
        getPresenter().onActivityResult(requestCode, resultCode, data);
    }

    @Override public void onDestroy() {
        getPresenter().onDestroy();
    }

    @NonNull
    @Override public ChatScreenPresenter createPresenter() {
        Intent startIntent = ((Activity) getContext()).getIntent();
        int chatType = startIntent.getIntExtra(ChatActivity.EXTRA_CHAT_TYPE, 0);

        if (chatType == ChatActivity.CHAT_TYPE_GROUP) {
            return new ChatGroupScreenPresenter(startIntent);
        } else if (chatType == ChatActivity.CHAT_TYPE_SINGLE) {
            return new ChatSingleScreenPresenter(startIntent);
        } else {
            throw new Error("Type doesn't exist");
        }
    }

    @Override public void showLoading() {
        contentView.setVisibility(View.GONE);
        loadingView.setVisibility(View.VISIBLE);
        errorView.setVisibility(View.GONE);
    }

    @Override public void showContent() {
        contentView.setVisibility(View.VISIBLE);
        loadingView.setVisibility(View.GONE);
        errorView.setVisibility(View.GONE);
    }

    @Override public void showError(Throwable e) {
        contentView.setVisibility(View.GONE);
        loadingView.setVisibility(View.GONE);
        errorView.setVisibility(View.VISIBLE);
    }

    @Override
    public void setSubject(String subject) {
        toolbarPresenter.setTitle(subject);
    }

//    @Override public void setChatConversation(ChatConversation chatConversation) {
//        this.chatConversation = chatConversation;
//        adapter.setChatConversation(chatConversation);
//        adapter.notifyDataSetChanged();
//
//        if (chatConversation.isGroupConversation()) {
//            toolbarPresenter.setTitle(TextUtils.isEmpty(chatConversation.getConversationName()) ? "" :
//                    chatConversation.getConversationName());
//        } else {
//            toolbarPresenter.setTitle(chatConversation.getChatUsers().get(1).getName());
//        }
//
//        int onlineUserCount = 0;
//        for (ChatUser user : chatConversation.getChatUsers()) {
//            if (user.isOnline()) {
//                onlineUserCount++;
//            }
//        }
//        if (chatConversation.isGroupConversation()) {
//            if (onlineUserCount > 0) {
//                toolbarPresenter.setSubtitle(String.format(getContext()
//                                .getString(R.string.chat_subtitle_format_group_chat_format),
//                                chatConversation.getOnlineUsers().size()));
//            } else {
//                toolbarPresenter.setSubtitle(getContext().getString(R.string.chat_subtitle_format_group_chat_offline));
//            }
//        } else {
//            if (onlineUserCount > 0) {
//                toolbarPresenter.setSubtitle(getContext().getString(R.string.chat_subtitle_format_single_chat_online));
//            } else {
//                toolbarPresenter.setSubtitle(getContext().getString(R.string.chat_subtitle_format_single_chat_offline));
//            }
//        }
//
//        chatUsersTypingView.updateUsersTyping(chatConversation.getTypingUsers());
//    }

    @Override
    public void onConversationCursorLoaded(Cursor cursor) {
        adapter.changeCursor(cursor);
    }
}
