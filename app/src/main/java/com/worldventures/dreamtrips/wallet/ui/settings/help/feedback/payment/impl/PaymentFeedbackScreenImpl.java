package com.worldventures.dreamtrips.wallet.ui.settings.help.feedback.payment.impl;

import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Spinner;

import com.afollestad.materialdialogs.MaterialDialog;
import com.jakewharton.rxbinding.widget.RxTextView;
import com.worldventures.core.janet.Injector;
import com.worldventures.core.model.EntityStateHolder;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.databinding.ScreenWalletSettingsHelpPaymentFeedbackBinding;
import com.worldventures.core.modules.infopages.model.FeedbackImageAttachment;
import com.worldventures.dreamtrips.wallet.service.command.settings.help.SendWalletFeedbackCommand;
import com.worldventures.dreamtrips.wallet.ui.common.helper2.error.SimpleErrorDialogView;
import com.worldventures.dreamtrips.wallet.ui.common.helper2.progress.SimpleDialogProgressView;
import com.worldventures.dreamtrips.wallet.ui.common.helper2.success.SimpleToastSuccessView;
import com.worldventures.dreamtrips.wallet.ui.settings.help.feedback.base.impl.BaseFeedbackScreenImpl;
import com.worldventures.dreamtrips.wallet.ui.settings.help.feedback.payment.PaymentFeedbackPresenter;
import com.worldventures.dreamtrips.wallet.ui.settings.help.feedback.payment.PaymentFeedbackScreen;
import com.worldventures.dreamtrips.wallet.ui.settings.help.feedback.payment.model.MerchantViewModel;
import com.worldventures.dreamtrips.wallet.ui.settings.help.feedback.payment.model.PaymentFeedbackViewModel;

import java.util.List;

import javax.inject.Inject;

import io.techery.janet.operationsubscriber.view.ComposableOperationView;
import io.techery.janet.operationsubscriber.view.OperationView;
import rx.Observable;

import static com.worldventures.core.utils.ProjectTextUtils.fromHtml;

public class PaymentFeedbackScreenImpl extends BaseFeedbackScreenImpl<PaymentFeedbackScreen, PaymentFeedbackPresenter> implements PaymentFeedbackScreen {

   private PaymentFeedbackViewModel paymentFeedbackViewModel = new PaymentFeedbackViewModel();
   private MenuItem actionSendMenuItem = null;
   private ScreenWalletSettingsHelpPaymentFeedbackBinding binding;
   private Observable<CharSequence> observerMerchantName;

   @Inject PaymentFeedbackPresenter presenter;

   @Override
   protected void onFinishInflate(View view) {
      super.onFinishInflate(view);
      binding = DataBindingUtil.bind(view);
      binding.setPaymentFeedbackViewModel(paymentFeedbackViewModel);
      initToolbar();
      setupAsteriskColor();

      observerMerchantName = RxTextView.textChanges(binding.incMerchant.etMerchantName);
      binding.incAdditionalInfo.feedbackAddPhotos.setOnClickListener(v -> getPresenter().chosenAttachments());
      final MerchantViewModel merchantViewModel = paymentFeedbackViewModel.getMerchantView();
      final Spinner merchantTypeSpinner = binding.incMerchant.sMerchantType;
      merchantTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
         @Override
         public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            merchantViewModel.setMerchantType(parent.getAdapter().getItem(position).toString());
            merchantViewModel.setSelectedTypeIndex(position);
         }

         @Override
         public void onNothingSelected(AdapterView<?> parent) {
         }
      });
      merchantTypeSpinner.setSelection(merchantViewModel.getSelectedTypeIndex());
   }

   private void setupAsteriskColor() {
      binding.incAttempts.tvCounterTitle.setText(
            fromHtml(getString(R.string.wallet_payment_feedback_number_attempts_label)));
      binding.incMerchant.tvMerchantTypeTitle.setText(
            fromHtml(getString(R.string.wallet_payment_feedback_merchant_type_label)));
      binding.incMerchant.tvMerchantNameTitle.setText(
            fromHtml(getString(R.string.wallet_payment_feedback_merchant_name_label)));
   }

   private void initToolbar() {
      binding.toolbar.setNavigationOnClickListener(v -> getPresenter().goBack());

      binding.toolbar.inflateMenu(R.menu.wallet_payment_feedback);
      actionSendMenuItem = binding.toolbar.getMenu().findItem(R.id.action_send);

      binding.toolbar.setOnMenuItemClickListener(item -> {
         switch (item.getItemId()) {
            case R.id.action_send:
               getPresenter().sendFeedback();
               break;
         }
         return true;
      });
   }

   @Override
   protected void onAttach(@NonNull View view) {
      super.onAttach(view);
      initAttachments();
      getPresenter().fetchAttachments();
   }

   private void initAttachments() {
      binding.incAdditionalInfo.feedbackAttachments.setPhotoCellDelegate(this::onFeedbackAttachmentClicked);
      binding.incAdditionalInfo.feedbackAttachments.init((Injector) getContext());
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

   @Override
   public void changeActionSendMenuItemEnabled(boolean enable) {
      if (actionSendMenuItem != null) actionSendMenuItem.setEnabled(enable);
   }

   private void showRetryUploadingUiForAttachment(EntityStateHolder<FeedbackImageAttachment> attachmentHolder) {
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
      binding.incAdditionalInfo.feedbackAttachments.removeItem(holder);
      updateAttachmentsViewVisibility();
   }

   @Override
   public void changeAddPhotosButtonEnabled(boolean enable) {
      binding.incAdditionalInfo.feedbackAddPhotos.setAlpha(enable ? 1f : 0.5f);
      binding.incAdditionalInfo.feedbackAddPhotos.setEnabled(enable);
   }

   @Override
   public void setAttachments(List<EntityStateHolder<FeedbackImageAttachment>> attachments) {
      binding.incAdditionalInfo.feedbackAttachments.setImages(attachments);
      updateAttachmentsViewVisibility();
   }

   @Override
   public void updateAttachment(EntityStateHolder<FeedbackImageAttachment> image) {
      binding.incAdditionalInfo.feedbackAttachments.changeItemState(image);
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
      return paymentFeedbackViewModel;
   }

   @Override
   public void showBackConfirmDialog() {
      new MaterialDialog.Builder(getContext()).content(R.string.wallet_settings_help_payment_feedback_dialog_discard_changes)
            .positiveText(R.string.wallet_settings_help_payment_feedback_dialog_changes_positive)
            .negativeText(R.string.cancel)
            .onPositive((dialog, which) -> presenter.discardChanges())
            .onNegative((dialog, which) -> dialog.cancel())
            .show();
   }

   @Override
   public void discardViewModelChanges() {
      paymentFeedbackViewModel.setCanBeLost(true);
   }

   private void updateAttachmentsViewVisibility() {
      int itemsCount = binding.incAdditionalInfo.feedbackAttachments.getItemCount();
      binding.incAdditionalInfo.feedbackAttachments.setVisibility(itemsCount > 0 ? View.VISIBLE : View.GONE);
   }

   @Override
   public PaymentFeedbackPresenter getPresenter() {
      return presenter;
   }

   @Override
   public View inflateView(LayoutInflater layoutInflater, ViewGroup viewGroup) {
      return layoutInflater.inflate(R.layout.screen_wallet_settings_help_payment_feedback, viewGroup, false);
   }

   @Override
   public boolean supportConnectionStatusLabel() {
      return false;
   }

   @Override
   public boolean supportHttpConnectionStatusLabel() {
      return true;
   }

   @Override
   public boolean handleBack() {
      if (paymentFeedbackViewModel.isDataChanged()) {
         getPresenter().handleBackOnDataChangedAction();
         return true;
      } else {
         return super.handleBack();
      }
   }
}
