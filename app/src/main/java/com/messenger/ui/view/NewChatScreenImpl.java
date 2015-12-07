package com.messenger.ui.view;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import butterknife.InjectView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.worldventures.dreamtrips.R;
import com.messenger.model.ChatUser;
import com.messenger.ui.adapter.ChatContactsAdapter;
import com.messenger.ui.presenter.NewChatLayoutPresenter;
import com.messenger.ui.presenter.NewChatLayoutPresenterImpl;
import com.messenger.ui.presenter.ToolbarPresenter;

public class NewChatScreenImpl extends BaseViewStateLinearLayout<NewChatScreen, NewChatLayoutPresenter>
        implements NewChatScreen {

    @InjectView(R.id.new_chat_content_view) View contentView;
    @InjectView(R.id.new_chat_loading_view) View loadingView;
    @InjectView(R.id.new_chat_error_view) View errorView;

    @InjectView(R.id.new_chat_toolbar) Toolbar toolbar;
    @InjectView(R.id.new_chat_recycler_view) RecyclerView recyclerView;

    @InjectView(R.id.new_chat_conversation_icon) ImageView conversationIcon;
    @InjectView(R.id.new_chat_conversation_name) EditText conversationNameEditText;
    @InjectView(R.id.new_chat_chosen_contacts_textview) TextView chosenContactsTextView;

    private ToolbarPresenter toolbarPresenter;

    private ChatContactsAdapter adapter;

    public NewChatScreenImpl(Context context) {
        super(context);
        init(context);
    }

    public NewChatScreenImpl(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        presenter.connect();
    }

    private void init(Context context) {
        setOrientation(LinearLayout.VERTICAL);
        LayoutInflater.from(context).inflate(R.layout.screen_new_chat, this, true);
        ButterKnife.inject(this, this);
        initUi();
    }

    private void initUi() {
        setBackgroundColor(ContextCompat.getColor(getContext(), R.color.white));
        toolbarPresenter = new ToolbarPresenter(toolbar, (AppCompatActivity) getContext());
        toolbarPresenter.setTitle(R.string.new_chat_title);
        toolbarPresenter.enableUpNavigationButton();

        recyclerView.setSaveEnabled(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter = new ChatContactsAdapter(getContext()));
        adapter.setSelectionListener((selectedUsers) -> getPresenter().onSelectedUsersStateChanged(selectedUsers));
    }

    @Override public NewChatLayoutPresenter createPresenter() {
        return new NewChatLayoutPresenterImpl((Activity)getContext());
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

    @Override public void setContacts(List<ChatUser> chatContacts) {
        adapter.setChatContacts(chatContacts);
        adapter.notifyDataSetChanged();
    }

    @Override public void setSelectedContacts(List<ChatUser> selectedContacts) {
        adapter.setSelectedContacts(selectedContacts);
        adapter.notifyDataSetChanged();
    }

    @Override public void setSelectedUsersHeaderText(CharSequence text) {
        chosenContactsTextView.setText(text);
    }

    @Override public void setConversationIcon(Bitmap bitmap) {
        conversationIcon.setImageBitmap(bitmap);
    }

    @Override public AppCompatActivity getActivity() {
        return (AppCompatActivity) getContext();
    }

    @Override public boolean onCreateOptionsMenu(Menu menu) {
        return presenter.onCreateOptionsMenu(menu);
    }

    @Override public boolean onOptionsItemSelected(MenuItem item) {
        return presenter.onOptionsItemSelected(item);
    }

    @Override public void onActivityResult(int requestCode, int resultCode, Intent data) {
        getPresenter().onActivityResult(requestCode, resultCode, data);
    }

    @OnClick(R.id.new_chat_conversation_icon) public void onConversationItemClick() {
        getPresenter().onHandleTakePictureIntent();
    }

    @Override public String getConversationName() {
        return conversationNameEditText.getText().toString();
    }
    
}
