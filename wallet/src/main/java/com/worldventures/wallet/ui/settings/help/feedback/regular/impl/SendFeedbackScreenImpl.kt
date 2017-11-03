package com.worldventures.wallet.ui.settings.help.feedback.regular.impl


import android.os.Bundle
import android.support.v7.widget.Toolbar
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import com.afollestad.materialdialogs.MaterialDialog
import com.jakewharton.rxbinding.widget.RxTextView
import com.worldventures.core.janet.Injector
import com.worldventures.core.model.EntityStateHolder
import com.worldventures.core.modules.infopages.custom.AttachmentImagesHorizontalView
import com.worldventures.core.modules.infopages.model.FeedbackImageAttachment
import com.worldventures.wallet.R
import com.worldventures.wallet.service.command.settings.help.SendWalletFeedbackCommand
import com.worldventures.wallet.ui.common.helper2.error.SimpleErrorDialogView
import com.worldventures.wallet.ui.common.helper2.progress.SimpleDialogProgressView
import com.worldventures.wallet.ui.common.helper2.success.SimpleToastSuccessView
import com.worldventures.wallet.ui.settings.help.feedback.base.impl.BaseFeedbackScreenImpl
import com.worldventures.wallet.ui.settings.help.feedback.regular.FeedbackType
import com.worldventures.wallet.ui.settings.help.feedback.regular.SendFeedbackPresenter
import com.worldventures.wallet.ui.settings.help.feedback.regular.SendFeedbackScreen
import io.techery.janet.operationsubscriber.view.ComposableOperationView
import io.techery.janet.operationsubscriber.view.OperationView
import rx.Observable
import javax.inject.Inject

class SendFeedbackScreenImpl(args: Bundle) : BaseFeedbackScreenImpl<SendFeedbackScreen, SendFeedbackPresenter>(args), SendFeedbackScreen {

   @Inject lateinit var sendFeedbackPresenter: SendFeedbackPresenter

   private lateinit var toolbar: Toolbar
   private lateinit var tvDescription: TextView
   private lateinit var etFeedbackMessage: EditText
   private lateinit var addPhotosButton: View
   private lateinit var feedbackAttachments: AttachmentImagesHorizontalView
   private lateinit var actionSendMenuItem: MenuItem

   override lateinit var textFeedbackMessage: Observable<CharSequence>
      private set

   override val feedbackMessage: String
      get() = etFeedbackMessage.text.toString()

   override val feedbackType: FeedbackType
      get() = args.getSerializable(KEY_FEEDBACK_TYPE) as FeedbackType

   override fun onFinishInflate(view: View) {
      super.onFinishInflate(view)
      toolbar = view.findViewById(R.id.toolbar)
      toolbar.setNavigationOnClickListener { onNavigationBack() }
      tvDescription = view.findViewById(R.id.tv_description)
      etFeedbackMessage = view.findViewById(R.id.et_feedback_message)
      addPhotosButton = view.findViewById(R.id.feedback_add_photos)
      addPhotosButton.setOnClickListener { presenter!!.chosenAttachments() }
      feedbackAttachments = view.findViewById(R.id.feedback_attachments)

      textFeedbackMessage = RxTextView.textChanges(etFeedbackMessage)

      initItemMenu()
   }

   private fun initItemMenu() {
      toolbar.inflateMenu(R.menu.wallet_settings_help)
      actionSendMenuItem = toolbar.menu.findItem(R.id.action_send)
      toolbar.setOnMenuItemClickListener { item ->
         if (item.itemId == R.id.action_send) {
            presenter!!.sendFeedback()
         }
         true
      }
   }

   override fun applyFeedbackType(feedbackType: FeedbackType) {
      val smartCardFeedback = feedbackType == FeedbackType.SmartCardFeedback
      toolbar.setTitle(if (smartCardFeedback)
         R.string.wallet_card_settings_send_feedback
      else
         R.string.wallet_card_settings_customer_support)
      tvDescription.setText(if (smartCardFeedback)
         R.string.wallet_settings_help_feedback_user_approve_info
      else
         R.string.wallet_settings_help_customer_support_email_us_description)
      etFeedbackMessage.setHint(if (smartCardFeedback)
         R.string.wallet_settings_help_feedback_enter_comment_hint
      else
         R.string.wallet_settings_help_customer_support_email_us_hint)
   }

   private fun onNavigationBack() {
      presenter!!.goBack()
   }

   override fun onAttach(view: View) {
      super.onAttach(view)
      initAttachments()
      presenter!!.fetchAttachments()
   }

   private fun initAttachments() {
      feedbackAttachments.setPhotoCellDelegate { onFeedbackAttachmentClicked(it) }
      feedbackAttachments.init(context as Injector)
   }

   private fun onFeedbackAttachmentClicked(holder: EntityStateHolder<FeedbackImageAttachment>) {
      val state = holder.state()
      when (state) {
         EntityStateHolder.State.DONE -> presenter!!.openFullScreenPhoto(holder)
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
            .onPositive { _, _ -> presenter!!.removeAttachment(holder) }
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
                  0 -> presenter!!.retryUploadingAttachment(attachmentHolder)
                  1 -> presenter!!.removeAttachment(attachmentHolder)
                  else -> {
                  }
               }
            }.show()
   }

   override fun changeAddPhotosButtonEnabled(enable: Boolean) {
      addPhotosButton.alpha = if (enable) 1f else 0.5f
      addPhotosButton.isEnabled = enable
   }

   override fun setAttachments(attachments: List<EntityStateHolder<FeedbackImageAttachment>>) {
      feedbackAttachments.setImages(attachments)
      updateAttachmentsViewVisibility()
   }

   override fun updateAttachment(updatedHolder: EntityStateHolder<FeedbackImageAttachment>) {
      feedbackAttachments.changeItemState(updatedHolder)
      updateAttachmentsViewVisibility()
   }

   override fun provideOperationSendFeedback(): OperationView<SendWalletFeedbackCommand<*>> {
      return ComposableOperationView(
            SimpleDialogProgressView<SendWalletFeedbackCommand<*>>(context, R.string.wallet_settings_help_feedback_progress_send, false),
            SimpleToastSuccessView<SendWalletFeedbackCommand<*>>(context, R.string.wallet_settings_help_feedback_has_been_sent),
            SimpleErrorDialogView<SendWalletFeedbackCommand<*>>(context, R.string.wallet_settings_help_feedback_sending_fail)
      )
   }

   override fun removeAttachment(holder: EntityStateHolder<FeedbackImageAttachment>) {
      feedbackAttachments.removeItem(holder)
      updateAttachmentsViewVisibility()
   }

   private fun updateAttachmentsViewVisibility() {
      val itemsCount = feedbackAttachments.itemCount
      feedbackAttachments.visibility = if (itemsCount > 0) View.VISIBLE else View.GONE
   }

   override fun getPresenter(): SendFeedbackPresenter? {
      return sendFeedbackPresenter
   }

   override fun inflateView(layoutInflater: LayoutInflater, viewGroup: ViewGroup): View {
      return layoutInflater.inflate(R.layout.screen_wallet_settings_help_feedback, viewGroup, false)
   }

   override fun supportConnectionStatusLabel(): Boolean {
      return false
   }

   override fun supportHttpConnectionStatusLabel(): Boolean {
      return true
   }

   companion object {

      private val KEY_FEEDBACK_TYPE = "key_feedback_type"

      fun create(feedbackType: FeedbackType): SendFeedbackScreenImpl {
         val args = Bundle()
         args.putSerializable(KEY_FEEDBACK_TYPE, feedbackType)
         return SendFeedbackScreenImpl(args)
      }
   }
}
