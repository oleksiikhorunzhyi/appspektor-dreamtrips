package com.messenger.ui.view;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import butterknife.InjectView;
import butterknife.ButterKnife;

import com.messenger.ui.presenter.ChatScreenPresenterImpl;
import com.worldventures.dreamtrips.R;
import com.messenger.model.ChatConversation;
import com.messenger.ui.adapter.ChatConversationAdapter;
import com.messenger.ui.presenter.ChatScreenPresenter;
import com.messenger.ui.presenter.ToolbarPresenter;

public class ChatScreenImpl extends BaseViewStateLinearLayout<ChatScreen, ChatScreenPresenter>
        implements ChatScreen {

    @InjectView(R.id.chat_content_view) View contentView;
    @InjectView(R.id.chat_loading_view) View loadingView;
    @InjectView(R.id.chat_error_view) View errorView;

    @InjectView(R.id.chat_toolbar)
    Toolbar toolbar;
    @InjectView(R.id.chat_recycler_view) RecyclerView recyclerView;

    @InjectView(R.id.chat_message_edit_text) EditText messageEditText;

    private ToolbarPresenter toolbarPresenter;

    private ChatConversationAdapter adapter;

    private ChatConversation chatConversation;

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
        LayoutInflater.from(context).inflate(R.layout.screen_chat, this, true);
        ButterKnife.inject(this, this);
        initUi();
    }

    @SuppressWarnings("Deprecated")
    private void initUi() {
        setBackgroundColor(getResources().getColor(R.color.white));
        toolbarPresenter = new ToolbarPresenter(toolbar, (AppCompatActivity) getContext());
        toolbarPresenter.enableUpNavigationButton();

        recyclerView.setSaveEnabled(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter = new ChatConversationAdapter(getContext()));

        messageEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (i == EditorInfo.IME_ACTION_GO) {
                    if (getPresenter().onNewMessageFromUi(messageEditText.getText().toString())) {
                        messageEditText.setText("");
                        recyclerView.smoothScrollToPosition(adapter.getItemCount());
                        return true;
                    }
                }
                return false;
            }
        });
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

    @Override public ChatScreenPresenter createPresenter() {
        ChatScreenPresenter presenter = new ChatScreenPresenterImpl();
        presenter.setChatConversation(this.chatConversation);
        return presenter;
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

    @Override public void setChatConversation(ChatConversation chatConversation) {
        this.chatConversation = chatConversation;
        adapter.setChatConversation(chatConversation);
        adapter.notifyDataSetChanged();
        toolbarPresenter.setTitle(TextUtils.isEmpty(chatConversation.getConversationName()) ? "<No Name Given>" :
                chatConversation.getConversationName());
    }
}
