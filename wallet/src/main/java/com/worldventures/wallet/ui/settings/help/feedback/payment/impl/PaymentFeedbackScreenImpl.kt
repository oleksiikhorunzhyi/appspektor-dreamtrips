package com.worldventures.wallet.ui.settings.help.feedback.payment.impl

import android.databinding.DataBindingUtil
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import com.afollestad.materialdialogs.MaterialDialog
import com.jakewharton.rxbinding.widget.RxTextView
import com.worldventures.core.janet.Injector
import com.worldventures.core.model.EntityStateHolder
import com.worldventures.core.modules.infopages.model.FeedbackImageAttachment
import com.worldventures.core.utils.ProjectTextUtils.fromHtml
import com.worldventures.wallet.R
import com.worldventures.wallet.databinding.ScreenWalletSettingsHelpPaymentFeedbackBinding
import com.worldventures.wallet.service.command.settings.help.SendWalletFeedbackCommand
import com.worldventures.wallet.ui.common.helper2.error.SimpleErrorDialogView
import com.worldventures.wallet.ui.common.helper2.progress.SimpleDialogProgressView
import com.worldventures.wallet.ui.common.helper2.success.SimpleToastSuccessView
import com.worldventures.wallet.ui.settings.help.feedback.base.impl.BaseFeedbackScreenImpl
import com.worldventures.wallet.ui.settings.help.feedback.payment.PaymentFeedbackPresenter
import com.worldventures.wallet.ui.settings.help.feedback.payment.PaymentFeedbackScreen
import com.worldventures.wallet.ui.settings.help.feedback.payment.model.PaymentFeedbackViewModel
import io.techery.janet.operationsubscriber.view.ComposableOperationView
import io.techery.janet.operationsubscriber.view.OperationView
import rx.Observable
import javax.inject.Inject

private const val STATE_KEY_FEEDBACK_VIEW_MODEL = "PaymentFeedbackScreenImpl#STATE_KEY_FEEDBACK_VIEW_MODEL"

@Suppress("UnsafeCallOnNullableType")
class PaymentFeedbackScreenImpl : BaseFeedbackScreenImpl<PaymentFeedbackScreen, PaymentFeedbackPresenter>(), PaymentFeedbackScreen {

   private lateinit var actionSendMenuItem: MenuItem
   private lateinit var binding: ScreenWalletSettingsHelpPaymentFeedbackBinding
   private lateinit var observerMerchantName: Observable<CharSequence>

   @Inject lateinit var paymentFeedbackPresenter: PaymentFeedbackPresenter

   override val paymentFeedbackViewModel
      get() = binding.paymentFeedbackViewModel!!

   override fun onFinishInflate(view: View) {
      super.onFinishInflate(view)
      binding = DataBindingUtil.bind(view)
      binding.paymentFeedbackViewModel = PaymentFeedbackViewModel()
      initToolbar()
      setupAsteriskColor()

      observerMerchantName = RxTextView.textChanges(binding.incMerchant!!.etMerchantName)
      binding.incAdditionalInfo!!.feedbackAddPhotos.setOnClickListener { presenter.chosenAttachments() }
      val merchantViewModel = paymentFeedbackViewModel.merchantView
      val merchantTypeSpinner = binding.incMerchant!!.sMerchantType
      merchantTypeSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
         override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
            merchantViewModel.merchantType = parent.adapter.getItem(position).toString()
            merchantViewModel.selectedTypeIndex = position
         }

         override fun onNothingSelected(parent: AdapterView<*>) {
            //do nothing
         }
      }
      merchantTypeSpinner.setSelection(merchantViewModel.selectedTypeIndex)
   }

   private fun setupAsteriskColor() {
      binding.incAttempts!!.tvCounterTitle.text = fromHtml(getString(R.string.wallet_payment_feedback_number_attempts_label))
      binding.incMerchant!!.tvMerchantTypeTitle.text = fromHtml(getString(R.string.wallet_payment_feedback_merchant_type_label))
      binding.incMerchant!!.tvMerchantNameTitle.text = fromHtml(getString(R.string.wallet_payment_feedback_merchant_name_label))
   }

   private fun initToolbar() {
      binding.toolbar.setNavigationOnClickListener { presenter.goBack() }

      binding.toolbar.inflateMenu(R.menu.wallet_payment_feedback)
      actionSendMenuItem = binding.toolbar.menu.findItem(R.id.action_send)

      binding.toolbar.setOnMenuItemClickListener { item ->
         if (item.itemId == R.id.action_send) {
            presenter.sendFeedback()
         }
         true
      }
   }

   override fun onAttach(view: View) {
      super.onAttach(view)
      initAttachments()
      presenter.fetchAttachments()
   }

   override fun onSaveViewState(view: View, outState: Bundle) {
      outState.putParcelable(STATE_KEY_FEEDBACK_VIEW_MODEL, binding.paymentFeedbackViewModel)
      super.onSaveViewState(view, outState)
   }

   override fun onRestoreViewState(view: View, savedViewState: Bundle) {
      binding.paymentFeedbackViewModel = savedViewState.getParcelable(STATE_KEY_FEEDBACK_VIEW_MODEL)
      super.onRestoreViewState(view, savedViewState)
   }

   private fun initAttachments() {
      binding.incAdditionalInfo!!.feedbackAttachments.setPhotoCellDelegate { onFeedbackAttachmentClicked(it) }
      binding.incAdditionalInfo!!.feedbackAttachments.init(context as Injector)
   }

   private fun onFeedbackAttachmentClicked(holder: EntityStateHolder<FeedbackImageAttachment>) {
      val state = holder.state()
      when (state) {
         EntityStateHolder.State.DONE -> presenter.openFullScreenPhoto(holder)
         EntityStateHolder.State.PROGRESS -> showAttachmentActionDialog(holder)
         EntityStateHolder.State.FAIL -> showRetryUploadingUiForAttachment(holder)
         else -> {
         }
      }
   }

   private fun showAttachmentActionDialog(holder: EntityStateHolder<FeedbackImageAttachment>) {
      MaterialDialog.Builder(context)
            .content(R.string.wallet_settings_help_feedback_action_delete_attachment)
            .positiveText(R.string.wallet_label_yes)
            .onPositive { _, _ -> presenter.removeAttachment(holder) }
            .negativeText(R.string.wallet_label_no)
            .onNegative { dialog, _ -> dialog.dismiss() }
            .build()
            .show()
   }

   override fun changeActionSendMenuItemEnabled(enable: Boolean) {
      actionSendMenuItem.isEnabled = enable
   }

   private fun showRetryUploadingUiForAttachment(attachmentHolder: EntityStateHolder<FeedbackImageAttachment>) {
      MaterialDialog.Builder(context)
            .items(R.array.wallet_settings_help_feedback_failed_uploading_attachment)
            .itemsCallback { _, _, which, _ ->
               when (which) {
                  0 -> presenter.retryUploadingAttachment(attachmentHolder)
                  1 -> presenter.removeAttachment(attachmentHolder)
                  else -> {
                  }
               }
            }.show()
   }

   override fun removeAttachment(holder: EntityStateHolder<FeedbackImageAttachment>) {
      binding.incAdditionalInfo!!.feedbackAttachments.removeItem(holder)
      updateAttachmentsViewVisibility()
   }

   @Suppress("MagicNumber")
   override fun changeAddPhotosButtonEnabled(enable: Boolean) {
      binding.incAdditionalInfo!!.feedbackAddPhotos.alpha = if (enable) 1f else 0.5f
      binding.incAdditionalInfo!!.feedbackAddPhotos.isEnabled = enable
   }

   override fun setAttachments(attachments: List<EntityStateHolder<FeedbackImageAttachment>>) {
      binding.incAdditionalInfo!!.feedbackAttachments.setImages(attachments)
      updateAttachmentsViewVisibility()
   }

   override fun updateAttachment(updatedHolder: EntityStateHolder<FeedbackImageAttachment>) {
      binding.incAdditionalInfo!!.feedbackAttachments.changeItemState(updatedHolder)
      updateAttachmentsViewVisibility()
   }

   override fun provideOperationSendFeedback(): OperationView<SendWalletFeedbackCommand<*>> {
      return ComposableOperationView(
            SimpleDialogProgressView<SendWalletFeedbackCommand<*>>(context, R.string.wallet_settings_help_feedback_progress_send, false),
            SimpleToastSuccessView<SendWalletFeedbackCommand<*>>(context, R.string.wallet_settings_help_feedback_has_been_sent),
            SimpleErrorDialogView<SendWalletFeedbackCommand<*>>(context, R.string.wallet_settings_help_feedback_sending_fail)
      )
   }

   override fun observeMerchantName() = observerMerchantName

   override fun showBackConfirmDialog() {
      MaterialDialog.Builder(context).content(R.string.wallet_settings_help_payment_feedback_dialog_discard_changes)
            .positiveText(R.string.wallet_settings_help_payment_feedback_dialog_changes_positive)
            .negativeText(R.string.wallet_cancel_label)
            .onPositive { _, _ -> presenter.discardChanges() }
            .onNegative { dialog, _ -> dialog.cancel() }
            .show()
   }

   override fun discardViewModelChanges() {
      paymentFeedbackViewModel.setCanBeLost(true)
   }

   private fun updateAttachmentsViewVisibility() {
      val itemsCount = binding.incAdditionalInfo!!.feedbackAttachments.itemCount
      binding.incAdditionalInfo!!.feedbackAttachments.visibility = if (itemsCount > 0) View.VISIBLE else View.GONE
   }

   override fun getPresenter() = paymentFeedbackPresenter

   override fun inflateView(layoutInflater: LayoutInflater, viewGroup: ViewGroup): View
         = layoutInflater.inflate(R.layout.screen_wallet_settings_help_payment_feedback, viewGroup, false)

   override fun supportConnectionStatusLabel() = false

   override fun supportHttpConnectionStatusLabel() = true

   override fun handleBack(): Boolean {
      return if (paymentFeedbackViewModel.isDataChanged) {
         presenter.showBackConfirmation()
         true
      } else {
         super.handleBack()
      }
   }
}
