package com.messenger.ui.view;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.messenger.messengerservers.entities.Conversation;
import com.messenger.ui.presenter.ChatSettingsScreenPresenter;
import com.messenger.ui.presenter.ChatSettingsScreenPresenterImpl;
import com.messenger.ui.presenter.ToolbarPresenter;
import com.messenger.ui.widget.AvatarView;
import com.messenger.ui.widget.ChatSettingsRow;
import com.messenger.ui.widget.GroupAvatarsView;
import com.worldventures.dreamtrips.R;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

public abstract class ChatSettingsScreenImpl extends BaseViewStateLinearLayout<ChatSettingsScreen, ChatSettingsScreenPresenter>
        implements ChatSettingsScreen {

    @InjectView(R.id.chat_settings_content_view)
    View contentView;
    @InjectView(R.id.chat_settings_loading_view)
    View loadingView;
    @InjectView(R.id.chat_settings_error_view)
    View errorView;

    @InjectView(R.id.chat_settings_group_avatars_view)
    GroupAvatarsView groupAvatarsView;
    @InjectView(R.id.chat_settings_single_chat_avatar_view)
    AvatarView singleChatAvatarView;
    @InjectView(R.id.chat_settings_chat_name_text_view)
    TextView chatNameTextView;
    @InjectView(R.id.chat_settings_chat_description_text_view)
    TextView chatDescriptionTextView;

    @InjectView(R.id.char_settings_rows_parent)
    ViewGroup chatSettingsRows;
    @InjectView(R.id.chat_settings_clear_history_button)
    Button clearHistoryButton;
    @InjectView(R.id.chat_settings_leave_chat_button)
    Button leaveChatButton;

    @InjectView(R.id.chat_settings_toolbar)
    Toolbar toolbar;

    protected ToolbarPresenter toolbarPresenter;

    protected ChatSettingsRow notificationsSettingsRow;

    public ChatSettingsScreenImpl(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public ChatSettingsScreenImpl(Context context) {
        super(context);
        init(context);
    }

    protected void init(Context context) {
        setOrientation(LinearLayout.VERTICAL);
        ButterKnife.inject(this, LayoutInflater.from(context).inflate(R.layout.screen_chat_settings,
                this, true));
        addSettingsRows(ButterKnife.findById(this, R.id.char_settings_rows_parent));
        initUi();
    }

    protected void addSettingsRows(ViewGroup parent) {
        notificationsSettingsRow = new ChatSettingsRow(getContext());
        notificationsSettingsRow.setIcon(R.drawable.ic_notifications_black_24_px);
        notificationsSettingsRow.setTitle(R.string.chat_settings_row_notifications);
        notificationsSettingsRow.enableSwitch(((compoundButton, b)
                -> getPresenter().onNotificationsSwitchClicked(b)));
        parent.addView(notificationsSettingsRow);
    }

    @Override
    public void setNotificationSettingStatus(boolean checked) {
        notificationsSettingsRow.setSwitchChecked(checked);
    }

    protected void initUi() {
        toolbarPresenter = new ToolbarPresenter(toolbar, (AppCompatActivity) getContext());
        toolbarPresenter.enableUpNavigationButton();
        // hide the button until we have user story
        clearHistoryButton.setVisibility(View.GONE);
        leaveChatButton.setText(getLeaveChatButtonStringRes());
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

    @OnClick(R.id.chat_settings_clear_history_button)
    void onClearHistoryButtonClicked() {
        getPresenter().onClearChatHistoryClicked();
    }

    @OnClick(R.id.chat_settings_leave_chat_button)
    void onLeaveChatButtonClicked() {
        new AlertDialog.Builder(getContext())
                .setPositiveButton(android.R.string.ok, (dialog, which) -> getPresenter().onLeaveChatClicked())
                .setNegativeButton(android.R.string.cancel, null)
                .setMessage(getResources().getString(R.string.chat_settings_leave_group_chat, getPresenter().getCurrentSubject()))
                .create()
                .show();
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
    public boolean onOptionsItemSelected(MenuItem item) {
        return getPresenter().onOptionsItemSelected(item);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        getPresenter().onPrepareOptionsMenu(menu);
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
    public ChatSettingsScreenPresenter createPresenter() {
        return new ChatSettingsScreenPresenterImpl(getActivity(), getActivity().getIntent());
    }

    protected abstract
    @StringRes
    int getLeaveChatButtonStringRes();

    @Override
    public void showSubjectDialog() {
        Context context = getContext();
        final View dialogView = inflate(context, R.layout.dialog_change_subject, null);
        EditText etSubject = (EditText) dialogView.findViewById(R.id.et_subject);
        String currentSubject = getPresenter().getCurrentSubject();
        etSubject.setText(currentSubject);
        if (currentSubject != null) {
            etSubject.setSelection(currentSubject.length());
        }
        new AlertDialog.Builder(context)
                .setView(dialogView)
                .setNegativeButton(android.R.string.cancel, null)
                .setPositiveButton(android.R.string.ok, (dialog1, which1) -> {
                    onSubjectEntered(etSubject.getText().toString());
                })
                .setTitle(R.string.change_subject)
                .create()
                .show();
    }

    private void onSubjectEntered(String subject) {
        getPresenter().applyNewChatSubject(subject);
    }

    @Override
    public void setConversation(Conversation conversation) {
    }

    @Override
    public void prepareViewForOwner(boolean isOwner) {
        if (!isOwner) return;
        leaveChatButton.setEnabled(false);
    }
}
