package com.worldventures.dreamtrips.wallet.ui.settings.help.feedback;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.jakewharton.rxbinding.widget.RxTextView;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.common.model.EntityStateHolder;
import com.worldventures.dreamtrips.modules.common.view.custom.PhotoPickerLayout;
import com.worldventures.dreamtrips.modules.infopages.model.FeedbackImageAttachment;
import com.worldventures.dreamtrips.modules.infopages.service.command.UploadFeedbackAttachmentCommand;
import com.worldventures.dreamtrips.modules.infopages.view.custom.AttachmentImagesHorizontalView;
import com.worldventures.dreamtrips.wallet.service.command.settings.help.SendWalletFeedbackCommand;
import com.worldventures.dreamtrips.wallet.ui.common.base.MediaPickerService;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletLinearLayout;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.OperationScreen;
import com.worldventures.dreamtrips.wallet.ui.common.helper2.error.SimpleErrorDialogView;
import com.worldventures.dreamtrips.wallet.ui.common.helper2.progress.SimpleDialogProgressView;
import com.worldventures.dreamtrips.wallet.ui.common.helper2.success.SimpleToastSuccessView;

import java.util.List;

import butterknife.InjectView;
import butterknife.OnClick;
import io.techery.janet.operationsubscriber.view.ComposableOperationView;
import io.techery.janet.operationsubscriber.view.OperationView;
import rx.Observable;
import rx.subjects.BehaviorSubject;


public class SendFeedbackScreen extends WalletLinearLayout<SendFeedbackPresenter.Screen, SendFeedbackPresenter, SendFeedbackPath> implements SendFeedbackPresenter.Screen {

   public static final int MAX_PHOTOS_ATTACHMENT = 5;

   @InjectView(R.id.toolbar) Toolbar toolbar;

   @InjectView(R.id.tv_description) TextView tvDescription;
   @InjectView(R.id.et_feedback_message) EditText etFeedbackMessage;
   @InjectView(R.id.feedback_add_photos) View addPhotosButton;
   @InjectView(R.id.feedback_attachments) AttachmentImagesHorizontalView feedbackAttachments;

   private MenuItem actionSendMenuItem = null;
   private Observable<CharSequence> textMessageObserver;
   private BehaviorSubject<Boolean> photoPickerVisibilityObservable = BehaviorSubject.create(false);
   private MediaPickerService mediaPickerService;

   public SendFeedbackScreen(Context context) {
      super(context);
   }

   public SendFeedbackScreen(Context context, AttributeSet attrs) {
      super(context, attrs);
   }

   @NonNull
   @Override
   public SendFeedbackPresenter createPresenter() {
      return new SendFeedbackPresenter(getContext(), getInjector());
   }

   @Override
   public OperationScreen provideOperationDelegate() {
      return null;
   }

   @Override
   protected void onFinishInflate() {
      super.onFinishInflate();
      if (isInEditMode()) return;
      toolbar.setNavigationOnClickListener(v -> onNavigationBack());
      supportConnectionStatusLabel(false);
      supportHttpConnectionStatusLabel(true);

      //noinspection WrongConstant
      mediaPickerService = (MediaPickerService) getContext().getSystemService(MediaPickerService.SERVICE_NAME);
      mediaPickerService.setPhotoPickerListener(photoPickerListener);
      textMessageObserver = RxTextView.textChanges(etFeedbackMessage);

      initItemMenu();
      applyFeedbackType(getPath().getFeedbackType());
   }

   private void applyFeedbackType(SendFeedbackPath.FeedbackType feedbackType) {
      boolean smartCardFeedback = feedbackType == SendFeedbackPath.FeedbackType.SmartCardFeedback;
      toolbar.setTitle(smartCardFeedback ? R.string.wallet_card_settings_send_feedback : R.string.wallet_card_settings_customer_support);
      tvDescription.setText(smartCardFeedback ? R.string.wallet_settings_help_feedback_user_approve_info :
            R.string.wallet_settings_help_customer_support_email_us_description);
      etFeedbackMessage.setHint(smartCardFeedback ? R.string.wallet_settings_help_feedback_enter_comment_hint :
            R.string.wallet_settings_help_customer_support_email_us_hint);
   }

   private void onNavigationBack() {
      getPresenter().goBack();
      mediaPickerService.hidePicker();
      getPresenter().clearAttachments();
   }

   @Override
   protected void onAttachedToWindow() {
      super.onAttachedToWindow();
      if (isInEditMode()) return;
      initAttachments();
      getPresenter().fetchAttachments();
   }

   private void initItemMenu() {
      toolbar.inflateMenu(R.menu.menu_wallet_settings_help);
      actionSendMenuItem = toolbar.getMenu().findItem(R.id.action_send);
      toolbar.setOnMenuItemClickListener(item -> {
         switch (item.getItemId()) {
            case R.id.action_send:
               getPresenter().sendFeedback(getPath().getFeedbackType(), etFeedbackMessage.getText().toString());
         }
         return true;
      });
   }

   private void initAttachments() {
      feedbackAttachments.setPhotoCellDelegate(this::onFeedbackAttachmentClicked);
      feedbackAttachments.init(getInjector());
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

   private PhotoPickerLayout.PhotoPickerListener photoPickerListener = new PhotoPickerLayout.PhotoPickerListener() {
      @Override
      public void onClosed() {
         presenter.setupInputMode();
         photoPickerVisibilityObservable.onNext(false);
      }

      @Override
      public void onOpened() {
         clearMessageFocus();
         photoPickerVisibilityObservable.onNext(true);
      }
   };

   private void clearMessageFocus() {
      // because clearFocus() method does not work
      etFeedbackMessage.setFocusable(false);
      etFeedbackMessage.setFocusableInTouchMode(false);
      etFeedbackMessage.setFocusable(true);
      etFeedbackMessage.setFocusableInTouchMode(true);
   }

   @OnClick(R.id.feedback_add_photos)
   public void onAddAttachments() {
      getPresenter().chosenAttachments();
   }

   @Override
   public void pickPhoto() {
      mediaPickerService.pickPhotos(MAX_PHOTOS_ATTACHMENT - feedbackAttachments.getItemCount());
   }

   @Override
   public void changeActionSendMenuItemEnabled(boolean enable) {
      if (actionSendMenuItem != null) actionSendMenuItem.setEnabled(enable);
   }

   @Override
   public Observable<Boolean> getPhotoPickerVisibilityObservable() {
      return photoPickerVisibilityObservable;
   }

   @Override
   public Observable<CharSequence> getTextFeedbackMessage() {
      return textMessageObserver;
   }

   @Override
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
   public Observable<Uri> observePickPhoto() {
      return mediaPickerService.observePicker();
   }

   @Override
   public OperationView<UploadFeedbackAttachmentCommand> provideOperationUploadAttachments() {
      return new ComposableOperationView<>(
            new SimpleDialogProgressView<>(getContext(), R.string.wallet_settings_help_feedback_progress_add_attachments, false),
            new SimpleErrorDialogView<UploadFeedbackAttachmentCommand>(getContext(), R.string.wallet_settings_help_feedback_adding_attachments_fail)
      );
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
}
