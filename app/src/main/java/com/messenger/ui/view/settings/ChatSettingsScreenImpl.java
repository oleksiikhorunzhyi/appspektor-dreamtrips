package com.messenger.ui.view.settings;

import android.content.Context;
import android.support.annotation.StringRes;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.messenger.entities.DataConversation;
import com.messenger.flow.path.StyledPath;
import com.messenger.ui.presenter.ChatSettingsScreenPresenter;
import com.messenger.ui.presenter.ToolbarPresenter;
import com.messenger.ui.view.layout.MessengerPathLayout;
import com.messenger.ui.widget.AvatarView;
import com.messenger.ui.widget.ChatSettingsRow;
import com.messenger.ui.widget.GroupAvatarsView;
import com.worldventures.dreamtrips.R;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

public abstract class ChatSettingsScreenImpl<S extends ChatSettingsScreen, P extends StyledPath>
        extends MessengerPathLayout<S, ChatSettingsScreenPresenter<S>, P>
        implements ChatSettingsScreen {

    @InjectView(R.id.chat_settings_content_view)
    ViewGroup contentView;
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

    public ChatSettingsScreenImpl(Context context) {
        super(context);
    }

    public ChatSettingsScreenImpl(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onPrepared() {
        super.onPrepared();
        init();
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        inflateToolbarMenu(toolbar);
    }

    protected void init() {
        setOrientation(LinearLayout.VERTICAL);
        ButterKnife.inject(this);
        //
        // TODO: 1/2/16  hide for RC version
        //addSettingsRows(ButterKnife.findById(this, R.id.char_settings_rows_parent));
        //
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
        toolbarPresenter = new ToolbarPresenter(toolbar, getContext());
        toolbarPresenter.attachPathAttrs(getPath().getAttrs());
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

    public ViewGroup getContentView() {
        return contentView;
    }

    @Override
    public void showErrorDialog(@StringRes int msg) {
        Snackbar.make(getRootView(), msg, Snackbar.LENGTH_SHORT).show();
    }

    @OnClick(R.id.chat_settings_clear_history_button)
    void onClearHistoryButtonClicked() {
        getPresenter().onClearChatHistoryClicked();
    }

    @OnClick(R.id.chat_settings_leave_chat_button)
    void onLeaveChatButtonClicked() {
        getPresenter().onLeaveButtonClick();
    }

    public void showLeaveChatDialog(String message) {
        new AlertDialog.Builder(getContext())
                .setPositiveButton(android.R.string.ok, (dialog, which) -> getPresenter().onLeaveChatClicked())
                .setNegativeButton(android.R.string.cancel, null)
                .setMessage(message)
                .create()
                .show();
    }

    protected abstract
    @StringRes
    int getLeaveChatButtonStringRes();

    @Override
    public void showSubjectDialog(String currentSubject) {
        Context context = getContext();
        final View dialogView = inflate(context, R.layout.dialog_change_subject, null);
        EditText etSubject = (EditText) dialogView.findViewById(R.id.et_subject);
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
    public void setConversation(DataConversation conversation) {
    }

    @Override
    public void prepareViewForOwner(boolean isOwner) {
        if (!isOwner) return;
        leaveChatButton.setVisibility(GONE);
    }
}
