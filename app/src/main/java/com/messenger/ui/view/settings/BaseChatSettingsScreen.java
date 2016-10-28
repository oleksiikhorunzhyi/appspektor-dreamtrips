package com.messenger.ui.view.settings;

import android.app.ProgressDialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.messenger.entities.DataConversation;
import com.messenger.ui.presenter.ToolbarPresenter;
import com.messenger.ui.presenter.settings.ChatSettingsScreenPresenter;
import com.messenger.ui.view.layout.MessengerPathLayout;
import com.messenger.ui.widget.AvatarView;
import com.messenger.ui.widget.ChatSettingsRow;
import com.messenger.ui.widget.GroupAvatarsView;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.flow.path.StyledPath;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

public abstract class BaseChatSettingsScreen<Screen extends ChatSettingsScreen, Presenter extends ChatSettingsScreenPresenter<Screen>, Path extends StyledPath> extends MessengerPathLayout<Screen, Presenter, Path> implements ChatSettingsScreen {

   @InjectView(R.id.content_layout) ViewGroup contentView;
   @InjectView(R.id.chat_settings_loading_view) View loadingView;
   @InjectView(R.id.chat_settings_error_view) View errorView;

   @InjectView(R.id.chat_settings_group_avatars_view) GroupAvatarsView groupAvatarsView;
   @InjectView(R.id.chat_settings_single_chat_avatar_view) AvatarView singleChatAvatarView;
   @InjectView(R.id.chat_settings_chat_name_text_view) TextView chatNameTextView;
   @InjectView(R.id.chat_settings_chat_description_text_view) TextView chatDescriptionTextView;
   @InjectView(R.id.char_settings_rows_parent) ViewGroup chatSettingsRows;
   @InjectView(R.id.chat_settings_clear_history_button) Button clearChatButton;
   @InjectView(R.id.chat_settings_leave_chat_button) Button leaveChatButton;
   @InjectView(R.id.chat_settings_group_chat_info_textview) TextView infoTextView;
   @InjectView(R.id.chat_settings_toolbar) Toolbar toolbar;

   private ProgressDialog progressDialog;
   protected ToolbarPresenter toolbarPresenter;

   protected ChatSettingsRow notificationsSettingsRow;

   public BaseChatSettingsScreen(Context context) {
      super(context);
   }

   public BaseChatSettingsScreen(Context context, AttributeSet attrs) {
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
      notificationsSettingsRow.enableSwitch(((compoundButton, b) -> getPresenter().onNotificationsSwitchClicked(b)));
      parent.addView(notificationsSettingsRow);
   }

   protected void initUi() {
      toolbarPresenter = new ToolbarPresenter(toolbar, getContext());
      toolbarPresenter.attachPathAttrs(getPath().getAttrs());
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
   public void showErrorDialog(@StringRes int msg) {
      Snackbar.make(getRootView(), msg, Snackbar.LENGTH_SHORT).show();
   }

   @OnClick(R.id.chat_settings_clear_history_button)
   void onClearHistoryButtonClicked() {
      getPresenter().onClearChatHistoryClicked();
   }

   @Override
   public void showClearChatDialog() {
      new AlertDialog.Builder(getContext(), R.style.RetrySendMessageDialogStyle).setMessage(R.string.chat_settings_clear_chat_confirmation_text)
            .setNegativeButton(R.string.chat_settings_clear_chat_cancel, null)
            .setPositiveButton(R.string.chat_settings_clear_chat_ok, (dialog, which) -> presenter.onClearChatHistory())
            .show();
   }

   @Override
   public void setConversation(@NonNull DataConversation conversation) {
   }

   @Override
   public void invalidateToolbarMenu() {
      inflateToolbarMenu(toolbar);
   }

   @Override
   public void showProgressDialog() {
      progressDialog = ProgressDialog.show(getContext(), null, getResources().getString(R.string.loading));
   }

   @Override
   public void dismissProgressDialog() {
      if (progressDialog != null) {
         progressDialog.dismiss();
      }
   }
}
