package com.worldventures.dreamtrips.wallet.ui.settings.help.feedback.payment;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import com.afollestad.materialdialogs.MaterialDialog;
import com.jakewharton.rxbinding.widget.RxTextView;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.utils.ProjectTextUtils;
import com.worldventures.dreamtrips.databinding.ScreenWalletSettingsHelpPaymentFeedbackBinding;
import com.worldventures.dreamtrips.modules.common.model.EntityStateHolder;
import com.worldventures.dreamtrips.modules.common.view.custom.PhotoPickerLayout;
import com.worldventures.dreamtrips.modules.infopages.model.FeedbackImageAttachment;
import com.worldventures.dreamtrips.modules.infopages.view.custom.AttachmentImagesHorizontalView;
import com.worldventures.dreamtrips.wallet.service.command.settings.help.SendWalletFeedbackCommand;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.OperationScreen;
import com.worldventures.dreamtrips.wallet.ui.common.helper2.error.SimpleErrorDialogView;
import com.worldventures.dreamtrips.wallet.ui.common.helper2.progress.SimpleDialogProgressView;
import com.worldventures.dreamtrips.wallet.ui.common.helper2.success.SimpleToastSuccessView;
import com.worldventures.dreamtrips.wallet.ui.settings.help.feedback.BaseFeedbackScreen;
import com.worldventures.dreamtrips.wallet.ui.settings.help.feedback.payment.model.PaymentFeedbackViewModel;

import java.util.List;

import butterknife.InjectView;
import butterknife.OnClick;
import butterknife.OnItemSelected;
import io.techery.janet.operationsubscriber.view.ComposableOperationView;
import io.techery.janet.operationsubscriber.view.OperationView;
import rx.Observable;

public class PaymentFeedbackScreen extends BaseFeedbackScreen<PaymentFeedbackPresenter.Screen, PaymentFeedbackPresenter, PaymentFeedbackPath> implements PaymentFeedbackPresenter.Screen {

   @InjectView(R.id.toolbar) Toolbar toolbar;
   @InjectView(R.id.feedback_add_photos) View addPhotosButton;
   @InjectView(R.id.feedback_attachments) AttachmentImagesHorizontalView feedbackAttachments;
   @InjectView(R.id.et_merchant_name) EditText tvMerchantName;

   private PaymentFeedbackViewModel paymentFeedbackView = new PaymentFeedbackViewModel();

   private MenuItem actionSendMenuItem = null;
   private ScreenWalletSettingsHelpPaymentFeedbackBinding binding;

   private Observable<CharSequence> observerMerchantName;

   public PaymentFeedbackScreen(Context context) {
      super(context);
   }

   public PaymentFeedbackScreen(Context context, AttributeSet attrs) {
      super(context, attrs);
   }

   @NonNull
   @Override
   public PaymentFeedbackPresenter createPresenter() {
      return new PaymentFeedbackPresenter(getContext(), getInjector());
   }

   @Override
   public OperationScreen provideOperationDelegate() {
      return null;
   }

   @Override
   protected void onFinishInflate() {
      super.onFinishInflate();
      supportConnectionStatusLabel(false);
      supportHttpConnectionStatusLabel(true);
      if (isInEditMode()) return;

      initToolbar();

      binding = DataBindingUtil.bind(this);
      binding.setPaymentFeedbackViewModel(paymentFeedbackView);
      setupAsteriskColor();

      observerMerchantName = RxTextView.textChanges(tvMerchantName);
   }

   private void setupAsteriskColor() {
      binding.incAttempts.tvCounterTitle.setText(ProjectTextUtils.fromHtml(
            getString(R.string.wallet_payment_feedback_number_attempts_label)
      ));
      binding.incMerchant.tvMerchantTypeTitle.setText(ProjectTextUtils.fromHtml(
            getString(R.string.wallet_payment_feedback_merchant_type_label)
      ));
      binding.incMerchant.tvMerchantNameTitle.setText(ProjectTextUtils.fromHtml(
            getString(R.string.wallet_payment_feedback_merchant_name_label)
      ));
   }

   private PhotoPickerLayout.PhotoPickerListener photoPickerListener = new PhotoPickerLayout.PhotoPickerListener() {
      @Override
      public void onClosed() {
         presenter.setupInputMode();
      }

      @Override
      public void onOpened() {}
   };

   private void initToolbar() {
      toolbar.setNavigationOnClickListener(v -> getPresenter().goBack());

      toolbar.inflateMenu(R.menu.menu_wallet_payment_feedback);
      actionSendMenuItem = toolbar.getMenu().findItem(R.id.action_send);

      toolbar.setOnMenuItemClickListener(item -> {
         switch (item.getItemId()) {
            case R.id.action_send:
               getPresenter().sendFeedback();
               break;
         }
         return true;
      });
   }

   @Override
   protected void onAttachedToWindow() {
      super.onAttachedToWindow();
      if (isInEditMode()) return;
      initAttachments();
      getPresenter().fetchAttachments();
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

   @OnClick(R.id.feedback_add_photos)
   public void onAddAttachments() {
      getPresenter().chosenAttachments();
   }

   @Override
   public void changeActionSendMenuItemEnabled(boolean enable) {
      if (actionSendMenuItem != null) actionSendMenuItem.setEnabled(enable);
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
   public void removeAttachment(EntityStateHolder<FeedbackImageAttachment> holder) {
      feedbackAttachments.removeItem(holder);
      updateAttachmentsViewVisibility();
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
            new SimpleErrorDialogView<>(getContext(), R.string.wallet_settings_help_feedback_sending_fail)
      );
   }

   @Override
   public Observable<CharSequence> observeMerchantName() {
      return observerMerchantName;
   }

   @Override
   public PaymentFeedbackViewModel getPaymentFeedbackViewModel() {
      return paymentFeedbackView;
   }

   @Override
   public void showBackConfirmDialog() {
      new MaterialDialog.Builder(getContext()).content(R.string.wallet_settings_help_payment_feedback_dialog_discard_changes)
            .positiveText(R.string.wallet_settings_help_payment_feedback_dialog_changes_positive)
            .negativeText(R.string.cancel)
            .onPositive((dialog, which) -> onNavigationBack())
            .onNegative((dialog, which) -> dialog.cancel())
            .show();
   }

   private void onNavigationBack() {
      getPresenter().clearAttachments();
      getPresenter().back();
   }

   private void updateAttachmentsViewVisibility() {
      int itemsCount = feedbackAttachments.getItemCount();
      feedbackAttachments.setVisibility(itemsCount > 0 ? View.VISIBLE : View.GONE);
   }

   @OnItemSelected(R.id.s_merchant_type)
   public void onMerchantTypeSelected(int position) {
      paymentFeedbackView.getMerchantView().setMerchantType(binding.incMerchant.sMerchantType.getAdapter()
            .getItem(position)
            .toString());
   }
}
