package com.worldventures.wallet.ui.records.detail.impl

import android.databinding.DataBindingUtil
import android.databinding.Observable.OnPropertyChangedCallback
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import com.afollestad.materialdialogs.MaterialDialog
import com.worldventures.core.utils.HttpErrorHandlingUtil
import com.worldventures.wallet.BR
import com.worldventures.wallet.R
import com.worldventures.wallet.databinding.ScreenWalletWizardViewCardDetailsBinding
import com.worldventures.wallet.service.command.SetDefaultCardOnDeviceCommand
import com.worldventures.wallet.service.command.SetPaymentCardAction
import com.worldventures.wallet.service.command.record.DeleteRecordCommand
import com.worldventures.wallet.service.command.record.UpdateRecordCommand
import com.worldventures.wallet.ui.common.base.WalletBaseController
import com.worldventures.wallet.ui.common.binding.LastPositionSelector
import com.worldventures.wallet.ui.common.helper2.error.ErrorViewFactory
import com.worldventures.wallet.ui.common.helper2.error.SmartCardErrorViewProvider
import com.worldventures.wallet.ui.common.helper2.error.http.HttpErrorViewProvider
import com.worldventures.wallet.ui.common.helper2.progress.SimpleDialogProgressView
import com.worldventures.wallet.ui.common.helper2.success.SimpleToastSuccessView
import com.worldventures.wallet.ui.dashboard.util.model.CommonCardViewModel
import com.worldventures.wallet.ui.records.common.ChangeDefaultPaymentCardDialog
import com.worldventures.wallet.ui.records.detail.CardDetailsPresenter
import com.worldventures.wallet.ui.records.detail.CardDetailsScreen
import com.worldventures.wallet.ui.records.detail.DefaultRecordDetail
import com.worldventures.wallet.ui.records.detail.RecordDetailViewModel
import com.worldventures.wallet.util.WalletCardNameUtil.bindSpannableStringToTarget
import io.techery.janet.operationsubscriber.view.ComposableOperationView
import io.techery.janet.operationsubscriber.view.OperationView
import javax.inject.Inject

private const val ARG_KEY_MODIFY_RECORD = "key_modify_record"
private const val STATE_KEY_VIEW_MODEL = ""

@Suppress("UnsafeCallOnNullableType")
class CardDetailsScreenImpl(args: Bundle) : WalletBaseController<CardDetailsScreen, CardDetailsPresenter>(args), CardDetailsScreen {

   private lateinit var binding: ScreenWalletWizardViewCardDetailsBinding

   @Inject lateinit var screenPresenter: CardDetailsPresenter
   @Inject lateinit var httpErrorHandlingUtil: HttpErrorHandlingUtil

   private var detailViewModel: RecordDetailViewModel? = null

   private lateinit var saveMenuItem: MenuItem

   private var networkConnectionErrorDialog: MaterialDialog? = null

   override val recordId: String
      get() = detailViewModel!!.recordId

   override val recordName: String
      get() = detailViewModel!!.recordName

   override var isSaveButtonEnabled: Boolean
      get() = saveMenuItem.isEnabled
      set(value) {
         saveMenuItem.isEnabled = value
         detailViewModel?.isSaveButtonEnabled = value
      }

   override var cardNameErrorVisible: Boolean
      get() = detailViewModel!!.nameInputError.isNotEmpty()
      set(value) {
         cardNameErrorVisibility(value)
      }

   override var defaultRecordDetails: DefaultRecordDetail?
      get() = detailViewModel?.defaultRecordDetail
      set(value) {
         detailViewModel?.defaultRecordDetail = value
      }

   override val isDataChanged: Boolean
      get() = detailViewModel!!.recordName != detailViewModel!!.originCardName

   override fun onFinishInflate(view: View) {
      super.onFinishInflate(view)
      setupToolbar()
      binding.deleteButton.setOnClickListener { presenter.onDeleteCardClick() }
      binding.payThisCardButton.setOnClickListener { presenter.payThisCard() }
      bindSpannableStringToTarget(binding.cardNicknameLabel, R.string.wallet_card_details_label_card_nickname,
            R.string.wallet_add_card_details_hint_card_name_length, false, false)
   }

   override fun inflateView(layoutInflater: LayoutInflater, viewGroup: ViewGroup): View {
      binding = DataBindingUtil.inflate(layoutInflater, R.layout.screen_wallet_wizard_view_card_details, viewGroup, false)
      binding.lastPositionSelector = LastPositionSelector()
      return binding.root
   }

   private fun attachViewModel(detailViewModel: RecordDetailViewModel) {
      detailViewModel.addOnPropertyChangedCallback(object : OnPropertyChangedCallback() {
         override fun onPropertyChanged(sender: android.databinding.Observable, propertyId: Int) {
            when (propertyId) {
               BR.recordName -> presenter.validateRecordName(detailViewModel.recordName.trim())
               BR.defaultRecord -> {
                  presenter.changeDefaultCard(detailViewModel.isDefaultRecord, detailViewModel.recordId, detailViewModel.defaultRecordDetail)
               }
            }
         }
      })
      saveMenuItem.isEnabled = detailViewModel.isSaveButtonEnabled
      binding.recordDetails = detailViewModel
      this.detailViewModel = detailViewModel
   }

   override fun supportConnectionStatusLabel() = false

   override fun supportHttpConnectionStatusLabel() = false

   private fun setupToolbar() {
      val toolbar = binding.toolbar
      toolbar.setNavigationOnClickListener { presenter.goBack() }
      toolbar.inflateMenu(R.menu.wallet_payment_card_detail)
      saveMenuItem = toolbar.menu.findItem(R.id.action_save)
      toolbar.setOnMenuItemClickListener {
         if (it.itemId == R.id.action_save) {
            presenter.updateNickName()
            return@setOnMenuItemClickListener true
         } else {
            return@setOnMenuItemClickListener false
         }
      }
   }

   override fun showDefaultCardDialog() {
      defaultRecordDetails?.let {
         ChangeDefaultPaymentCardDialog(context, it.recordName)
               .setOnConfirmAction { presenter.onChangeDefaultCardConfirmed(detailViewModel!!.recordId) }
               .setOnCancelAction { presenter.onChangeDefaultCardCanceled() }
               .show()
      }
   }

   override fun showDeleteCardDialog() {
      MaterialDialog.Builder(context)
            .title(R.string.wallet_card_details_delete_card_dialog_title)
            .content(R.string.wallet_card_details_delete_card_dialog_content)
            .positiveText(R.string.wallet_ok)
            .negativeText(R.string.wallet_cancel_label)
            .onPositive { _, _ -> presenter.onDeleteCardConfirmed() }
            .build()
            .show()
   }

   override fun showNetworkConnectionErrorDialog() {
      if (networkConnectionErrorDialog == null) {
         networkConnectionErrorDialog = MaterialDialog.Builder(context)
               .title(R.string.wallet_error_label)
               .content(R.string.wallet_no_internet_connection)
               .positiveText(R.string.wallet_ok)
               .onPositive { dialog, _ -> dialog.dismiss() }
               .build()
      }
      if (!networkConnectionErrorDialog!!.isShowing) {
         networkConnectionErrorDialog!!.show()
      }
   }

   override fun showCardIsReadyDialog(cardName: String) {
      val builder = MaterialDialog.Builder(context)
      builder.content(getString(R.string.wallet_wizard_card_list_card_is_ready_text, cardName))
            .positiveText(R.string.wallet_ok)
            .onPositive { _, _ -> presenter.onCardIsReadyDialogShown() }
            .build()
            .show()
   }

   override fun showSCNonConnectionDialog() {
      MaterialDialog.Builder(context)
            .title(R.string.wallet_card_settings_cant_connected)
            .content(R.string.wallet_card_settings_message_cant_connected)
            .positiveText(R.string.wallet_ok)
            .build()
            .show()
   }

   override fun provideOperationSaveCardData(): OperationView<UpdateRecordCommand> {
      return ComposableOperationView(
            SimpleDialogProgressView(context, R.string.wallet_card_details_progress_save, false),
            SimpleToastSuccessView(context, R.string.wallet_card_details_success_save),
            ErrorViewFactory.builder<UpdateRecordCommand>()
                  .addProvider(SmartCardErrorViewProvider(context) { presenter.updateNickName() })
                  .addProvider(HttpErrorViewProvider(context, httpErrorHandlingUtil, {
                     presenter.updateNickName()
                  }) { })
                  .build()
      )
   }

   override fun notifyRecordDataIsSaved(newCardName: String) {
      detailViewModel?.let {
         it.originCardName = newCardName
         it.notifyPropertyChanged(BR.recordName)
      }
   }

   override fun undoDefaultCardChanges() {
      detailViewModel?.let {
         it.isDefaultRecord = it.recordId == it.defaultRecordDetail?.recordId
      }
   }

   private fun cardNameErrorVisibility(visible: Boolean) {
      detailViewModel?.nameInputError = if (visible) getString(R.string.wallet_card_details_nickname_error) else ""
   }

   override fun provideOperationDeleteRecord(): OperationView<DeleteRecordCommand> {
      return ComposableOperationView(
            SimpleDialogProgressView(context, R.string.wallet_loading, false),
            ErrorViewFactory.builder<DeleteRecordCommand>()
                  .addProvider(SmartCardErrorViewProvider(context) { presenter.onDeleteCardClick() })
                  .build()
      )
   }

   override fun provideOperationSetDefaultOnDevice(): OperationView<SetDefaultCardOnDeviceCommand> {
      return ComposableOperationView(
            SimpleDialogProgressView(context, R.string.wallet_loading, false),
            ErrorViewFactory.builder<SetDefaultCardOnDeviceCommand>()
                  .addProvider(SmartCardErrorViewProvider(context))
                  .build()
      )
   }

   override fun provideOperationSetPaymentCardAction(): OperationView<SetPaymentCardAction> {
      return ComposableOperationView(
            SimpleDialogProgressView(context, R.string.wallet_loading, false),
            ErrorViewFactory.builder<SetPaymentCardAction>()
                  .addProvider(SmartCardErrorViewProvider(context) { presenter.payThisCard() })
                  .build()
      )
   }

   override fun onAttach(view: View) {
      var shouldRequestDefaultId = false
      if (detailViewModel == null) {
         val record: CommonCardViewModel = args.getParcelable(ARG_KEY_MODIFY_RECORD)
         val detailViewModel = RecordDetailViewModel(recordModel = record.copy())
         attachViewModel(detailViewModel)
         shouldRequestDefaultId = true
      }
      super.onAttach(view)
      if (shouldRequestDefaultId) presenter.fetchDefaultRecord()
   }

   override fun onDetach(view: View) {
      networkConnectionErrorDialog?.dismiss()
      super.onDetach(view)
   }

   override fun onSaveViewState(view: View, outState: Bundle) {
      super.onSaveViewState(view, outState)
      outState.putParcelable(STATE_KEY_VIEW_MODEL, detailViewModel)
   }

   override fun onRestoreViewState(view: View, savedViewState: Bundle) {
      super.onRestoreViewState(view, savedViewState)
      attachViewModel(savedViewState.getParcelable(STATE_KEY_VIEW_MODEL))
   }

   override fun getPresenter(): CardDetailsPresenter = screenPresenter

   override fun screenModule() = CardDetailsScreenModule()

   companion object {

      fun create(recordViewModel: CommonCardViewModel): CardDetailsScreenImpl {
         val args = Bundle()
         args.putParcelable(ARG_KEY_MODIFY_RECORD, recordViewModel)
         return CardDetailsScreenImpl(args)
      }
   }
}
