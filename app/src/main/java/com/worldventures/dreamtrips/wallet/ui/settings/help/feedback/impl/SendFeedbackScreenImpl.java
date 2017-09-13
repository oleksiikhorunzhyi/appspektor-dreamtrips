package com.worldventures.dreamtrips.wallet.ui.settings.help.feedback.impl;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.jakewharton.rxbinding.widget.RxTextView;
import com.techery.spares.module.Injector;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.common.model.EntityStateHolder;
import com.worldventures.dreamtrips.modules.infopages.model.FeedbackImageAttachment;
import com.worldventures.dreamtrips.modules.infopages.view.custom.AttachmentImagesHorizontalView;
import com.worldventures.dreamtrips.wallet.service.command.settings.help.SendWalletFeedbackCommand;
import com.worldventures.dreamtrips.wallet.ui.common.helper2.error.SimpleErrorDialogView;
import com.worldventures.dreamtrips.wallet.ui.common.helper2.progress.SimpleDialogProgressView;
import com.worldventures.dreamtrips.wallet.ui.common.helper2.success.SimpleToastSuccessView;
import com.worldventures.dreamtrips.wallet.ui.settings.help.feedback.FeedbackType;
import com.worldventures.dreamtrips.wallet.ui.settings.help.feedback.SendFeedbackPresenter;
import com.worldventures.dreamtrips.wallet.ui.settings.help.feedback.SendFeedbackScreen;
import com.worldventures.dreamtrips.wallet.ui.settings.help.feedback.base.impl.BaseFeedbackScreenImpl;

import java.util.List;

import javax.inject.Inject;

import io.techery.janet.operationsubscriber.view.ComposableOperationView;
import io.techery.janet.operationsubscriber.view.OperationView;
import rx.Observable;

public class SendFeedbackScreenImpl extends BaseFeedbackScreenImpl<SendFeedbackScreen, SendFeedbackPresenter> implements SendFeedbackScreen {

   private static final String KEY_FEEDBACK_TYPE = "key_feedback_type";

   @Inject SendFeedbackPresenter presenter;

   private Toolbar toolbar;
   private TextView tvDescription;
   private EditText etFeedbackMessage;
   private View addPhotosButton;
   private AttachmentImagesHorizontalView feedbackAttachments;
   private MenuItem actionSendMenuItem = null;
   private Observable<CharSequence> textMessageObserver;

   public static SendFeedbackScreenImpl create(FeedbackType feedbackType) {
      final Bundle args = new Bundle();
      args.putSerializable(KEY_FEEDBACK_TYPE, feedbackType);
      return new SendFeedbackScreenImpl(args);
   }

   public SendFeedbackScreenImpl(Bundle args) {
      super(args);
   }

   @Override
   protected void onFinishInflate(View view) {
      super.onFinishInflate(view);
      toolbar = view.findViewById(R.id.toolbar);
      toolbar.setNavigationOnClickListener(v -> onNavigationBack());
      tvDescription = view.findViewById(R.id.tv_description);
      etFeedbackMessage = view.findViewById(R.id.et_feedback_message);
      addPhotosButton = view.findViewById(R.id.feedback_add_photos);
      addPhotosButton.setOnClickListener(btnAddPhotos -> getPresenter().chosenAttachments());
      feedbackAttachments = view.findViewById(R.id.feedback_attachments);
      //noinspection WrongConstant
      textMessageObserver = RxTextView.textChanges(etFeedbackMessage);

      initItemMenu();
   }

   @Override
   public void applyFeedbackType(FeedbackType feedbackType) {
      boolean smartCardFeedback = feedbackType == FeedbackType.SmartCardFeedback;
      toolbar.setTitle(smartCardFeedback ? R.string.wallet_card_settings_send_feedback : R.string.wallet_card_settings_customer_support);
      tvDescription.setText(smartCardFeedback ? R.string.wallet_settings_help_feedback_user_approve_info :
            R.string.wallet_settings_help_customer_support_email_us_description);
      etFeedbackMessage.setHint(smartCardFeedback ? R.string.wallet_settings_help_feedback_enter_comment_hint :
            R.string.wallet_settings_help_customer_support_email_us_hint);
   }

   private void onNavigationBack() {
      getPresenter().goBack();
      getPresenter().clearAttachments();
   }

   @Override
   protected void onAttach(@NonNull View view) {
      super.onAttach(view);
      initAttachments();
      getPresenter().fetchAttachments();
   }

   private void initItemMenu() {
      toolbar.inflateMenu(R.menu.wallet_settings_help);
      actionSendMenuItem = toolbar.getMenu().findItem(R.id.action_send);
      toolbar.setOnMenuItemClickListener(item -> {
         switch (item.getItemId()) {
            case R.id.action_send:
               getPresenter().sendFeedback(etFeedbackMessage.getText().toString());
         }
         return true;
      });
   }

   private void initAttachments() {
      feedbackAttachments.setPhotoCellDelegate(this::onFeedbackAttachmentClicked);
      feedbackAttachments.init((Injector) getContext());
   }

   private void onFeedbackAttachmentClicked(EntityStateHolder<FeedbackImageAttachment> holder) {
      EntityStateHolder.State state = holder.state();
      switch (state) {
         case DONE:
            getPresenter().openFullScreenPhoto(holder);
            break;
         case PROGRESS:
            showAttachmentActionDialog(holder);
            break;
         case FAIL:
            showRetryUploadingUiForAttachment(holder);
            break;
      }
   }

   private void showAttachmentActionDialog(final EntityStateHolder<FeedbackImageAttachment> holder) {
      new MaterialDialog.Builder(getContext())
            .content(R.string.wallet_settings_help_feedback_action_delete_attachment)
            .positiveText(R.string.wallet_label_yes)
            .onPositive((dialog, which) -> getPresenter().onRemoveAttachment(holder))
            .negativeText(R.string.wallet_label_no)
            .onNegative((dialog, which) -> dialog.dismiss())
            .build()
            .show();
   }

   private void clearMessageFocus() {
      // because clearFocus() method does not work
      etFeedbackMessage.setFocusable(false);
      etFeedbackMessage.setFocusableInTouchMode(false);
      etFeedbackMessage.setFocusable(true);
      etFeedbackMessage.setFocusableInTouchMode(true);
   }

   @Override
   public void changeActionSendMenuItemEnabled(boolean enable) {
      if (actionSendMenuItem != null) actionSendMenuItem.setEnabled(enable);
   }

   @Override
   public Observable<CharSequence> getTextFeedbackMessage() {
      return textMessageObserver;
   }

   @Override
   public FeedbackType getFeedbackType() {
      return (getArgs() != null && !getArgs().isEmpty() && getArgs().containsKey(KEY_FEEDBACK_TYPE))
            ? (FeedbackType) getArgs().getSerializable(KEY_FEEDBACK_TYPE)
            : null;
   }

   public void showRetryUploadingUiForAttachment(EntityStateHolder<FeedbackImageAttachment> attachmentHolder) {
      new MaterialDialog.Builder(getContext())
            .items(R.array.wallet_settings_help_feedback_failed_uploading_attachment)
            .itemsCallback((dialog, v, which, text) -> {
               switch (which) {
                  case 0:
                     getPresenter().onRetryUploadingAttachment(attachmentHolder);
                     break;
                  case 1:
                     getPresenter().onRemoveAttachment(attachmentHolder);
                     break;
               }
            }).show();
   }

   @Override
   public void changeAddPhotosButtonEnabled(boolean enable) {
      addPhotosButton.setAlpha(enable ? 1f : 0.5f);
      addPhotosButton.setEnabled(enable);
   }

   @Override
   public void setAttachments(List<EntityStateHolder<FeedbackImageAttachment>> attachments) {
      feedbackAttachments.setImages(attachments);
      updateAttachmentsViewVisibility();
   }

   @Override
   public void updateAttachment(EntityStateHolder<FeedbackImageAttachment> image) {
      feedbackAttachments.changeItemState(image);
      updateAttachmentsViewVisibility();
   }

   @Override
   public OperationView<SendWalletFeedbackCommand> provideOperationSendFeedback() {
      return new ComposableOperationView<>(
            new SimpleDialogProgressView<>(getContext(), R.string.wallet_settings_help_feedback_progress_send, false),
            new SimpleToastSuccessView<>(getContext(), R.string.wallet_settings_help_feedback_has_been_sent),
            new SimpleErrorDialogView<SendWalletFeedbackCommand>(getContext(), R.string.wallet_settings_help_feedback_sending_fail)
      );
   }

   @Override
   public void removeAttachment(EntityStateHolder<FeedbackImageAttachment> image) {
      feedbackAttachments.removeItem(image);
      updateAttachmentsViewVisibility();
   }

   private void updateAttachmentsViewVisibility() {
      int itemsCount = feedbackAttachments.getItemCount();
      feedbackAttachments.setVisibility(itemsCount > 0 ? View.VISIBLE : View.GONE);
   }

   @Override
   public SendFeedbackPresenter getPresenter() {
      return presenter;
   }

   @Override
   public View inflateView(LayoutInflater layoutInflater, ViewGroup viewGroup) {
      return layoutInflater.inflate(R.layout.screen_wallet_settings_help_feedback, viewGroup, false);
   }

   @Override
   public boolean supportConnectionStatusLabel() {
      return false;
   }

   @Override
   public boolean supportHttpConnectionStatusLabel() {
      return true;
   }
}
