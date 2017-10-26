package com.messenger.ui.view.chat;

import android.app.ProgressDialog;
import android.content.Context;
import android.database.Cursor;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;
import com.innahema.collections.query.queriables.Queryable;
import com.jakewharton.rxbinding.widget.RxTextView;
import com.messenger.entities.DataConversation;
import com.messenger.entities.DataMessage;
import com.messenger.entities.DataUser;
import com.messenger.storage.dao.MessageDAO;
import com.messenger.ui.adapter.ChatAdapter;
import com.messenger.ui.adapter.ChatCellDelegate;
import com.messenger.ui.adapter.holder.chat.MessageViewHolder;
import com.messenger.ui.adapter.inflater.chat.ChatTimestampInflater;
import com.messenger.ui.helper.ConversationUIHelper;
import com.messenger.ui.model.AttachmentMenuItem;
import com.messenger.ui.module.flagging.FlaggingView;
import com.messenger.ui.module.flagging.FlaggingViewImpl;
import com.messenger.ui.presenter.ChatScreenPresenter;
import com.messenger.ui.presenter.ChatScreenPresenterImpl;
import com.messenger.ui.presenter.ToolbarPresenter;
import com.messenger.ui.util.chat.ChatTimestampFormatter;
import com.messenger.ui.util.chat.anim.TimestampItemAnimator;
import com.messenger.ui.view.layout.MessengerPathLayout;
import com.messenger.ui.widget.ChatUsersTypingView;
import com.messenger.util.ScrollStatePersister;
import com.worldventures.core.ui.util.SoftInputUtil;
import com.worldventures.core.modules.picker.view.dialog.MediaPickerDialog;
import com.worldventures.dreamtrips.R;

import java.util.List;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import butterknife.OnEditorAction;
import rx.Observable;
import rx.subjects.PublishSubject;

public class ChatScreenImpl extends MessengerPathLayout<ChatScreen, ChatScreenPresenter, ChatPath> implements ChatScreen {

   private static final int THRESHOLD = 5;

   @Inject ChatTimestampInflater chatTimestampInflater;
   @Inject ChatTimestampFormatter chatTimestampFormatter;

   @InjectView(R.id.content_layout) ViewGroup contentView;
   @InjectView(R.id.chat_loading_view) View loadingView;
   @InjectView(R.id.chat_toolbar) Toolbar toolbar;
   @InjectView(R.id.chat_toolbar_title) TextView title;
   @InjectView(R.id.chat_toolbar_subtitle) TextView subtitle;
   @InjectView(R.id.chat_recycler_view) RecyclerView recyclerView;
   @InjectView(R.id.chat_users_typing_view) ChatUsersTypingView chatUsersTypingView;
   @InjectView(R.id.chat_message_edit_text) EditText messageEditText;
   @InjectView(R.id.chat_message_send_button) View sendMessageButton;
   @InjectView(R.id.input_holder) ViewGroup inputHolder;
   @InjectView(R.id.input_disabled_text_view) View inputDisabledView;

   private ChatAdapter adapter;
   private LinearLayoutManager linearLayoutManager;
   private ScrollStatePersister scrollStatePersister = new ScrollStatePersister();

   private ProgressDialog progressDialog;
   private FlaggingView flaggingView;
   private View reloadHistoryView;

   private PublishSubject<String> attachmentClickStream = PublishSubject.create();
   private PublishSubject<DataMessage> lastVisibleItemStream = PublishSubject.create();

   public ChatScreenImpl(Context context) {
      super(context);
   }

   public ChatScreenImpl(Context context, AttributeSet attrs) {
      super(context, attrs);
   }

   @Override
   protected void onPrepared() {
      super.onPrepared();
      initUi();
   }

   @NonNull
   @Override
   public ChatScreenPresenter createPresenter() {
      return new ChatScreenPresenterImpl(getContext(), injector, getPath().getConversationId());
   }

   @SuppressWarnings("Deprecated")
   private void initUi() {
      ButterKnife.inject(this);
      injector.inject(this);
      //
      ToolbarPresenter toolbarPresenter = new ToolbarPresenter(toolbar, getContext());
      toolbarPresenter.attachPathAttrs(getPath().getAttrs());
      toolbarPresenter.hideBackButtonInLandscape();
      toolbarPresenter.setTitle("");
      toolbarPresenter.setSubtitle("");

      linearLayoutManager = new LinearLayoutManager(getContext());
      linearLayoutManager.setStackFromEnd(true);
      recyclerView.setLayoutManager(linearLayoutManager);
      recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
         @Override
         public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            if (adapter == null || adapter.getCursor() == null) return;

            int headersCount = adapter.getHeaderViewCount();
            int firstVisibleItem = linearLayoutManager.findFirstVisibleItemPosition();
            int lastVisibleItem = linearLayoutManager.findLastVisibleItemPosition();
            int totalItem = linearLayoutManager.getItemCount();
            onLastVisibleMessageChanged(adapter.getCursor(), lastVisibleItem - headersCount);

            if (dy > 0) return;

            if (firstVisibleItem <= THRESHOLD + headersCount && totalItem > headersCount) {
               getPresenter().onNextPageReached();
            }
         }
      });

      scrollStatePersister.restoreInstanceState(getLastRestoredInstanceState(), linearLayoutManager);

      // Mosby's presenter is created in super.onAttachedToWindow()
      // and restore instace state is called before onAttachedToWindow() also.
      // Make sure to have the view prepared before this.
      flaggingView = new FlaggingViewImpl(this, injector);

      reloadHistoryView = LayoutInflater.from(getContext())
            .inflate(R.layout.list_item_reload_chat_hystory, recyclerView, false);
      reloadHistoryView.findViewById(R.id.reload).setOnClickListener(v -> getPresenter().onReloadHistoryRequired());
   }

   @Override
   protected void onAttachedToWindow() {
      super.onAttachedToWindow();
      injector.inject(chatTimestampInflater);
      recyclerView.setAdapter(adapter = createAdapter(chatTimestampInflater));
      recyclerView.setItemAnimator(new TimestampItemAnimator(chatTimestampInflater));
      inflateToolbarMenu(toolbar);
   }

   protected ChatAdapter createAdapter(ChatTimestampInflater chatTimestampInflater) {
      ChatAdapter adapter = new ChatAdapter(null, chatTimestampInflater);
      injector.inject(adapter);
      adapter.setCellDelegate(chatCellDelegate);
      adapter.setNeedMarkUnreadMessages(true);
      return adapter;
   }

   private ChatCellDelegate chatCellDelegate = new ChatCellDelegate() {

      @Override
      public void onTimestampViewClicked(int position) {
         getPresenter().onTimestampViewClicked(position);
      }

      @Override
      public void onAvatarClicked(DataUser dataUser) {
         getPresenter().openUserProfile(dataUser);
      }

      @Override
      public void onImageClicked(String attachmentId) {
         attachmentClickStream.onNext(attachmentId);
      }

      @Override
      public void onMessageLongClicked(DataMessage dataMessage) {
         presenter.onShowContextualMenu(dataMessage);
      }

      @Override
      public void onRetryClicked(DataMessage dataMessage) {
         presenter.retryClicked(dataMessage);
      }

      @Override
      public void onMapClicked(LatLng latLng) {
         getPresenter().onMapClicked(latLng);
      }
   };

   @Override
   public void enableSendMessageButton(boolean enable) {
      sendMessageButton.setEnabled(enable);
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

   @OnClick(R.id.chat_message_add_button)
   protected void onAttachmentButtonClicked() {
      getPresenter().onAttachmentButtonClick();
   }

   @Override
   public void showAttachmentMenu(AttachmentMenuItem[] items) {
      new AlertDialog.Builder(getContext()).setItems(Queryable.from(items)
            .map(AttachmentMenuItem::getTitle)
            .toArray(), (dialog, which) -> getPresenter().onAttachmentMenuItemChosen(items[which])).show();
   }

   @Override
   public void showRetrySendMessageDialog(DataMessage dataMessage) {
      new AlertDialog.Builder(getContext(), R.style.RetrySendMessageDialogStyle).setMessage(R.string.chat_retry_send_message_dialog_resend_message)
            .setNegativeButton(R.string.chat_retry_send_message_dialog_cancel, null)
            .setPositiveButton(R.string.chat_retry_send_message_dialog_resend, (dialog, which) -> presenter.retrySendMessage(dataMessage))
            .show();
   }

   @Override
   public Observable<DataMessage> getLastVisibleItemStream() {
      return lastVisibleItemStream;
   }

   private void onLastVisibleMessageChanged(Cursor cursor, int position) {
      DataMessage message = cursor.isClosed() || !cursor.moveToPosition(position) ? null : MessageDAO.fromCursor(cursor, false);
      if (message != null) lastVisibleItemStream.onNext(message);
   }


   @Override
   public void showLoading() {
      loadingView.setVisibility(View.VISIBLE);
   }

   @Override
   public void showContent() {
      contentView.setVisibility(View.VISIBLE);
      loadingView.setVisibility(View.GONE);
   }

   @Override
   public void showError(Throwable e) {
      contentView.setVisibility(View.GONE);
      loadingView.setVisibility(View.GONE);
   }

   @Override
   public void setTitle(DataConversation conversation, List<DataUser> members) {
      ConversationUIHelper.setTitle(title, conversation, members, false);
      ConversationUIHelper.setSubtitle(subtitle, conversation, members);
   }

   @Override
   public void enableReloadChatButton(long clearDate) {
      String timestamp = chatTimestampFormatter.getMessageTimestamp(clearDate);
      ((TextView) reloadHistoryView.findViewById(R.id.timestamp)).setText(timestamp);
      adapter.addHeaderView(reloadHistoryView);
   }

   @Override
   public void disableReloadChatButton() {
      adapter.removeHeaderView(reloadHistoryView);
   }

   @Override
   public void showProgressDialog() {
      progressDialog = ProgressDialog.show(getContext(), null, null);
   }

   @Override
   public void dismissProgressDialog() {
      if (progressDialog != null) progressDialog.dismiss();
   }

   @Override
   public void showErrorMessage(@StringRes int error_no_connection) {
      Snackbar.make(this, error_no_connection, Snackbar.LENGTH_SHORT).show();
   }

   @Override
   public void setShowMarkUnreadMessage(boolean needShow) {
      if (adapter != null) adapter.setNeedMarkUnreadMessages(needShow);
   }

   @Override
   public void changeTypingUsers(List<DataUser> user) {
      chatUsersTypingView.changeTypingUsers(user);
   }

   @Override
   public void showMessages(Cursor cursor) {
      int firstVisibleViewTop = 0;
      int cursorCountDiff = 0;
      View firstItemView = null;
      if (adapter.getCursor() != null) {
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
         MessageViewHolder messageHolder = adapter.onCreateElementViewHolder(recyclerView, adapter.getItemViewType(position));
         adapter.onBindViewHolderCursor(messageHolder, cursor);
         messageHolder.itemView.measure(MeasureSpec.UNSPECIFIED, MeasureSpec.UNSPECIFIED);
         int diffHeight = firstItemView.getHeight() - messageHolder.itemView.getMeasuredHeight();
         int offset = firstVisibleViewTop + diffHeight;

         linearLayoutManager.scrollToPositionWithOffset(position, offset);
      } else if (cursor.getCount() == 1) {
         onLastVisibleMessageChanged(cursor, 0);
      }
   }

   @Override
   public void showContextualAction(Menu menu, DataMessage message) {
      CharSequence[] menuItems = new CharSequence[menu.size()];
      for (int i = 0; i < menu.size(); i++) {
         menuItems[i] = menu.getItem(i).getTitle();
      }
      new AlertDialog.Builder(getContext()).setItems(menuItems, ((dialogInterface, i) -> {
         switch (menu.getItem(i).getItemId()) {
            case R.id.action_copy_message:
               getPresenter().onCopyMessageTextToClipboard(message);
               break;
            case R.id.action_translate:
               getPresenter().onTranslateMessage(message);
               break;
            case R.id.action_revert_translate:
               getPresenter().onRevertTranslate(message);
               break;
            case R.id.action_start_chat:
               getPresenter().onStartNewChatForMessageOwner(message);
               break;
            case R.id.action_flag:
               getPresenter().onFlagMessage(message);
               break;
         }
      })).show();
   }

   ///////////////////////////////////////////////////////////////////////////
   // Message input
   ///////////////////////////////////////////////////////////////////////////

   @Override
   public Observable<String> getEditMessageObservable() {
      return RxTextView.textChangeEvents(messageEditText).map(event -> event.text().toString());
   }

   @Override
   public void enableInput(boolean enabled) {
      // use invisible so that disabled input overlay can have the same size as input holder
      inputHolder.setVisibility(enabled ? VISIBLE : INVISIBLE);
      inputDisabledView.setVisibility(enabled ? GONE : VISIBLE);
      if (!enabled) {
         SoftInputUtil.hideSoftInputMethod(this);
      }
   }

   @Override
   public Observable<String> getAttachmentClickStream() {
      return attachmentClickStream;
   }

   @Override
   public Parcelable onSaveInstanceState() {
      Parcelable parcelable = scrollStatePersister.saveScrollState(super.onSaveInstanceState(), linearLayoutManager);
      flaggingView.onSaveInstanceState(parcelable);
      return parcelable;
   }

   @Override
   public void onRestoreInstanceState(Parcelable state) {
      super.onRestoreInstanceState(state);
      flaggingView.onRestoreInstanceState(state);
   }

   ////////////////////////////////////////
   /////// Timestamp
   ////////////////////////////////////////

   @Override
   public void refreshChatTimestampView(int position) {
      adapter.refreshTimestampView(position);
   }

   ///////////////////////////////////////////////////////////////////////////
   // Location picking
   ///////////////////////////////////////////////////////////////////////////

   @Override
   public void showPickLocationError() {
      Snackbar.make(this, R.string.chat_could_not_share_location, Snackbar.LENGTH_SHORT);
   }

   ///////////////////////////////////////////////////////////////////////////
   // Photo picking
   ///////////////////////////////////////////////////////////////////////////

   @Override
   public void showPicker() {
      final MediaPickerDialog mediaPickerDialog = new MediaPickerDialog(getContext());
      mediaPickerDialog.setOnDoneListener(pickerAttachment -> getPresenter().imagesPicked(pickerAttachment));
      mediaPickerDialog.show(getResources().getInteger(R.integer.messenger_pick_image_limit));
   }

   @Override
   public FlaggingView getFlaggingView() {
      return flaggingView;
   }
}
