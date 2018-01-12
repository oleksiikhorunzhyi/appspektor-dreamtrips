package com.worldventures.wallet.ui.dashboard.impl

import android.animation.ObjectAnimator
import android.app.Dialog
import android.content.Context
import android.databinding.DataBindingUtil
import android.graphics.Point
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.TextView
import com.afollestad.materialdialogs.MaterialDialog
import com.worldventures.core.ui.view.custom.BadgeView
import com.worldventures.wallet.R
import com.worldventures.wallet.databinding.ScreenWalletCardlistBinding
import com.worldventures.wallet.domain.WalletConstants
import com.worldventures.wallet.domain.entity.SmartCardUser
import com.worldventures.wallet.service.command.SyncSmartCardCommand
import com.worldventures.wallet.service.command.record.SyncRecordOnNewDeviceCommand
import com.worldventures.wallet.service.command.reset.ResetSmartCardCommand
import com.worldventures.wallet.ui.common.adapter.BaseViewModel
import com.worldventures.wallet.ui.common.adapter.RecyclerItemClickListener
import com.worldventures.wallet.ui.common.base.WalletBaseController
import com.worldventures.wallet.ui.common.helper2.error.ErrorViewFactory
import com.worldventures.wallet.ui.common.helper2.error.SimpleDialogErrorViewProvider
import com.worldventures.wallet.ui.common.helper2.error.SmartCardErrorViewProvider
import com.worldventures.wallet.ui.common.helper2.progress.AnimatorProgressView
import com.worldventures.wallet.ui.common.helper2.progress.SimpleDialogProgressView
import com.worldventures.wallet.ui.common.recycler.WrapContentLinearLayoutManager
import com.worldventures.wallet.ui.dashboard.CardListPresenter
import com.worldventures.wallet.ui.dashboard.CardListScreen
import com.worldventures.wallet.ui.dashboard.util.OverlapDecoration
import com.worldventures.wallet.ui.dashboard.util.adapter.DashboardHolderAdapter
import com.worldventures.wallet.ui.dashboard.util.adapter.DashboardHolderFactoryImpl
import com.worldventures.wallet.ui.dashboard.util.model.CommonCardViewModel
import com.worldventures.wallet.ui.dashboard.util.model.TransitionModel
import com.worldventures.wallet.ui.dashboard.util.viewholder.CardStackHeaderHolder
import com.worldventures.wallet.ui.dashboard.util.viewholder.CommonCardHolder
import com.worldventures.wallet.ui.dialog.InstallFirmwareErrorDialog
import com.worldventures.wallet.ui.settings.general.reset.FactoryResetDelegate
import com.worldventures.wallet.ui.settings.general.reset.FactoryResetOperationView
import com.worldventures.wallet.ui.widget.SmartCardWidget
import io.techery.janet.operationsubscriber.view.ComposableOperationView
import io.techery.janet.operationsubscriber.view.OperationView
import io.techery.janet.smartcard.exception.WaitingResponseException
import java.util.ArrayList
import javax.inject.Inject

@Suppress("UnsafeCallOnNullableType")
class CardListScreenImpl : WalletBaseController<CardListScreen, CardListPresenter>(), CardListScreen {

   private lateinit var bankCardList: RecyclerView
   private lateinit var emptyCardListView: TextView
   private lateinit var fabButton: FloatingActionButton
   private lateinit var btnFirmwareAvailable: Button
   private lateinit var smartCardWidget: SmartCardWidget
   private lateinit var badgeView: BadgeView

   @Inject lateinit var screenPresenter: CardListPresenter

   private var cardStackHeaderHolder: CardStackHeaderHolder = CardStackHeaderHolder()

   private var installFirmwareErrorDialog: InstallFirmwareErrorDialog? = null
   private var forceUpdateDialog: MaterialDialog? = null
   private var addCardErrorDialog: Dialog? = null
   private var factoryResetConfirmationDialog: Dialog? = null
   private var scNonConnectionDialog: Dialog? = null

   private lateinit var multiAdapter: DashboardHolderAdapter<BaseViewModel<*>>
   private lateinit var binding: ScreenWalletCardlistBinding

   private var cardViewModels: ArrayList<BaseViewModel<*>>? = null

   override fun onFinishInflate(view: View) {
      super.onFinishInflate(view)
      val toolbar = view.findViewById<Toolbar>(R.id.toolbar)
      toolbar.inflateMenu(R.menu.wallet_dashboard)
      toolbar.setNavigationOnClickListener { presenter.navigationClick() }
      val actionSettingsView = toolbar.menu.findItem(R.id.item_settings).actionView
      actionSettingsView.setOnClickListener { presenter.onSettingsChosen() } // because onMenuItemClickListener isn't called
      badgeView = actionSettingsView.findViewById(R.id.badge_view)
      badgeView.hide() // because BadgeView is piece of shit.
      bankCardList = view.findViewById(R.id.bank_card_list)
      emptyCardListView = view.findViewById(R.id.empty_card_view)
      fabButton = view.findViewById(R.id.fab_button)
      btnFirmwareAvailable = view.findViewById(R.id.firmware_available)
      btnFirmwareAvailable.setOnClickListener { presenter.navigateToFirmwareUpdate() }
      smartCardWidget = view.findViewById(R.id.widget_dashboard_smart_card)
      binding = DataBindingUtil.bind(view)
      setupCardStackList()
   }

   override fun onDetach(view: View) {
      dismissDialogs()
      super.onDetach(view)
   }

   private fun dismissDialogs() {
      installFirmwareErrorDialog?.dismiss()
      forceUpdateDialog?.dismiss()
      addCardErrorDialog?.dismiss()
      factoryResetConfirmationDialog?.dismiss()
      scNonConnectionDialog?.dismiss()
   }

   override fun showRecordsInfo(result: ArrayList<BaseViewModel<*>>) {
      if (this.cardViewModels == null) {
         bankCardList.layoutAnimation = AnimationUtils.loadLayoutAnimation(context,
               R.anim.wallet_bottom_to_top_layout_anim)
         bankCardList.scheduleLayoutAnimation()
      } else {
         bankCardList.layoutAnimation = AnimationUtils.loadLayoutAnimation(context,
               R.anim.wallet_instant_layout_anim)
      }

      multiAdapter.swapList(result)
      this.cardViewModels = result
      emptyCardListView.visibility = if (result.isNotEmpty()) GONE else VISIBLE
   }

   override fun setDefaultSmartCard() {
      smartCardWidget.bindCard(cardStackHeaderHolder)
   }

   override fun setSmartCardStatusAttrs(batteryLevel: Int, connected: Boolean, lock: Boolean, stealthMode: Boolean) {
      cardStackHeaderHolder = cardStackHeaderHolder.copy(
            batteryLevel = batteryLevel,
            connected = connected,
            lock = lock,
            stealthMode = stealthMode
      )

      smartCardWidget.bindCard(cardStackHeaderHolder)
   }

   override fun setSmartCardUser(smartCardUser: SmartCardUser) {
      val photo = smartCardUser.userPhoto
      val phone = smartCardUser.phoneNumber
      cardStackHeaderHolder = cardStackHeaderHolder.copy(
            firstName = smartCardUser.firstName,
            middleName = smartCardUser.middleName,
            lastName = smartCardUser.lastName,
            photoUrl = photo?.uri ?: "",
            phoneNumber = phone?.fullPhoneNumber() ?: "")
      smartCardWidget.bindCard(cardStackHeaderHolder)
   }

   override fun setCardsCount(count: Int) {
      cardStackHeaderHolder = cardStackHeaderHolder.copy(cardCount = count)
      smartCardWidget.bindCard(cardStackHeaderHolder)
   }

   override fun setDisplayType(displayType: Int) {
      cardStackHeaderHolder = cardStackHeaderHolder.copy(displayType = displayType)
      smartCardWidget.bindCard(cardStackHeaderHolder)
   }

   override fun showAddCardErrorDialog(@CardListScreen.ErrorDialogType errorDialogType: Int) {
      val builder = MaterialDialog.Builder(context)

      when (errorDialogType) {
         CardListScreen.ERROR_DIALOG_FULL_SMARTCARD -> builder.content(R.string.wallet_wizard_full_card_list_error_message, WalletConstants.MAX_CARD_LIMIT)
         CardListScreen.ERROR_DIALOG_NO_INTERNET_CONNECTION -> {
            builder.title(R.string.wallet_wizard_no_internet_connection_title)
            builder.content(R.string.wallet_wizard_limited_access)
         }
         CardListScreen.ERROR_DIALOG_NO_SMARTCARD_CONNECTION -> {
            builder.title(R.string.wallet_wizard_no_connection_to_card_title)
            builder.content(R.string.wallet_wizard_limited_access)
         }
         else -> {
         }
      }

      addCardErrorDialog = builder.positiveText(R.string.wallet_ok)
            .negativeText(R.string.wallet_cancel_label)
            .build()
      addCardErrorDialog?.show()
   }

   override fun hideFirmwareUpdateBtn() {
      btnFirmwareAvailable.visibility = GONE
      badgeView.hide()
   }

   override fun showFirmwareUpdateBtn() {
      if (btnFirmwareAvailable.visibility == VISIBLE) {
         return
      }
      btnFirmwareAvailable.visibility = VISIBLE
      badgeView.show()
   }

   override fun showFirmwareUpdateError() {
      val dialog = installFirmwareErrorDialog ?:
            InstallFirmwareErrorDialog(context)
                  .setOnRetryction { presenter.retryFWU() }
                  .setOnCancelAction { presenter.retryFWUCanceled() }

      if (!dialog.isShowing) {
         if (forceUpdateDialog?.isShowing == true) {
            forceUpdateDialog?.setOnCancelListener(null)
            forceUpdateDialog?.cancel()
         }
         dialog.show()
      }
   }

   override fun showForceFirmwareUpdateDialog() {
      forceUpdateDialog?.dismiss()

      val dialog = forceUpdateDialog ?:
            MaterialDialog.Builder(context)
                  .title(R.string.wallet_dashboard_update_dialog_title)
                  .content(R.string.wallet_dashboard_update_dialog_content)
                  .negativeText(R.string.wallet_dashboard_update_dialog_btn_text_negative)
                  .cancelable(false)
                  .onNegative { _, _ -> presenter.navigateBack() }
                  .positiveText(R.string.wallet_dashboard_update_dialog_btn_text_positive)
                  .onPositive { _, _ -> presenter.confirmForceFirmwareUpdate() }
                  .build()

      if (!dialog.isShowing && installFirmwareErrorDialog?.isShowing != true) {
         dialog.show()
      }
   }

   override fun showFactoryResetConfirmationDialog() {
      val dialog = factoryResetConfirmationDialog ?:
            MaterialDialog.Builder(context)
                  .content(R.string.wallet_dashboard_factory_reset_dialog_content)
                  .negativeText(R.string.wallet_dashboard_factory_reset_dialog_btn_text_negative)
                  .cancelListener { presenter.navigateBack() }
                  .onNegative { _, _ -> presenter.navigateBack() }
                  .positiveText(R.string.wallet_dashboard_factory_reset_dialog_btn_text_positive)
                  .onPositive { _, _ -> presenter.navigateToFirmwareUpdate() }
                  .build()

      if (!dialog.isShowing) {
         dialog.show()
      }
   }

   override fun onSaveViewState(view: View, outState: Bundle) {
      outState.putInt(KEY_SHOW_UPDATE_BUTTON_STATE, btnFirmwareAvailable.visibility)
      super.onSaveViewState(view, outState)
   }

   override fun onRestoreViewState(view: View, savedViewState: Bundle) {
      super.onRestoreViewState(view, savedViewState)
      val visibility = savedViewState.getInt(KEY_SHOW_UPDATE_BUTTON_STATE, GONE)
      btnFirmwareAvailable.visibility = visibility
      if (visibility == VISIBLE) {
         badgeView.show()
      }
   }

   override fun onSaveInstanceState(outState: Bundle) {
      outState.putParcelableArrayList(KEY_LOADED_CARDS_LIST, this.cardViewModels)
      super.onSaveInstanceState(outState)
   }

   override fun onRestoreInstanceState(savedInstanceState: Bundle) {
      super.onRestoreInstanceState(savedInstanceState)
      this.cardViewModels = savedInstanceState.getParcelableArrayList(KEY_LOADED_CARDS_LIST)
   }

   private fun setupCardStackList() {
      val dimension = resources?.getDimensionPixelSize(R.dimen.wallet_card_height) ?: 0
      multiAdapter = DashboardHolderAdapter(ArrayList(), DashboardHolderFactoryImpl())
      bankCardList.adapter = multiAdapter
      val listAnimator = DefaultItemAnimator()
      listAnimator.supportsChangeAnimations = false
      bankCardList.itemAnimator = listAnimator
      bankCardList.addItemDecoration(OverlapDecoration((dimension.toDouble() * VISIBLE_SCALE * -1.0).toInt()))
      val layout = WrapContentLinearLayoutManager(context)
      layout.isAutoMeasureEnabled = true
      bankCardList.layoutManager = layout
      bankCardList.addOnItemTouchListener(RecyclerItemClickListener(context,
            object : RecyclerItemClickListener.OnItemClickListener {
               override fun onItemClick(view: View, position: Int) {
                  if (!presenter.isCardDetailSupported) {
                     return
                  }
                  if (multiAdapter.getItemViewType(position) == R.layout.item_wallet_record) {
                     showDetails(view, (dimension.toDouble() * VISIBLE_SCALE * -1.0).toInt())
                  }
               }

               override fun onItemLongClick(childView: View, position: Int, point: Point) {

               }
            }))

      smartCardWidget.setOnPhotoClickListener { presenter.onProfileChosen() }
      binding.transitionView?.root?.visibility = GONE

      cardViewModels?.let { showRecordsInfo(it) }
   }

   @Suppress("UnsafeCast")
   private fun showDetails(view: View, overlap: Int) {
      val model = (bankCardList.getChildViewHolder(view) as CommonCardHolder).data
      val transitionModel = presenter.getCardPosition(view, overlap, model.cardBackGround,
            model.defaultCard)
      addTransitionView(model, transitionModel)
      presenter.cardClicked(model, transitionModel)
   }

   private fun addTransitionView(model: CommonCardViewModel, transitionModel: TransitionModel) {
      val transitionView = binding.transitionView

      transitionView!!.cardModel = model
      setUpViewPosition(transitionModel, transitionView.root)
      transitionView.root.visibility = VISIBLE
   }

   private fun setUpViewPosition(params: TransitionModel, view: View) {
      val coords = IntArray(2)
      view.getLocationOnScreen(coords)
      view.translationX = 0f
      view.translationY = (params.top - coords[1]).toFloat()
   }

   override fun showSCNonConnectionDialog() {
      val dialog = scNonConnectionDialog ?:
            MaterialDialog.Builder(context)
                  .title(R.string.wallet_card_settings_cant_connected)
                  .content(R.string.wallet_card_settings_message_cant_connected)
                  .positiveText(R.string.wallet_ok)
                  .build()
      if (!dialog.isShowing) {
         dialog.show()
      }
   }

   override fun modeAddCard() {
      emptyCardListView.setText(R.string.wallet_wizard_empty_card_list_label)
      fabButton.rotation = 0f
      fabButton.setImageResource(R.drawable.ic_wallet_vector_white_plus)
      fabButton.setOnClickListener { addCardButtonClick() }
   }

   override fun modeSyncPaymentsFab() {
      emptyCardListView.setText(R.string.wallet_wizard_card_list_remove_payment_cards_message)
      fabButton.setImageResource(R.drawable.ic_wallet_sync)
      fabButton.setOnClickListener { onSyncPaymentsCardsButtonClick() }
   }

   private fun addCardButtonClick() {
      presenter.addCardRequired(cardStackHeaderHolder.cardCount)
   }

   private fun onSyncPaymentsCardsButtonClick() {
      presenter.syncPayments()
   }

   override fun showSyncFailedOptionsDialog() {
      MaterialDialog.Builder(context)
            .title(R.string.wallet_wizard_card_list_sync_fail_dialog_title)
            .content(R.string.wallet_wizard_card_list_sync_fail_dialog_message)
            .positiveText(R.string.wallet_wizard_card_list_sync_fail_dialog_cancel)
            .neutralText(R.string.wallet_wizard_card_list_sync_fail_dialog_retry)
            .negativeText(R.string.wallet_wizard_card_list_sync_fail_dialog_factory_reset)
            .onNeutral { _, _ -> presenter.syncPayments() }
            .onNegative { _, _ -> presenter.goToFactoryReset() }
            .build().show()
   }

   override fun provideOperationSyncSmartCard(): OperationView<SyncSmartCardCommand> {
      return ComposableOperationView(
            SimpleDialogProgressView(context, R.string.wallet_wizard_card_list_card_synchronization_dialog_text, false),
            ErrorViewFactory.builder<SyncSmartCardCommand>()
                  .addProvider(SimpleDialogErrorViewProvider(context, WaitingResponseException::class.java, R.string.wallet_smart_card_is_disconnected))
                  .addProvider(SmartCardErrorViewProvider(context))
                  .build()
      )
   }

   @Suppress("MagicNumber")
   override fun provideReSyncOperationView(): OperationView<SyncRecordOnNewDeviceCommand> {
      return ComposableOperationView(
            AnimatorProgressView(ObjectAnimator.ofFloat(fabButton, View.ROTATION.name, 0f, -360f)
                  .setDuration(650))
      )
   }

   //todo remove it
   override fun getViewContext(): Context = context

   override fun getCardListFab(): FloatingActionButton? = fabButton

   override fun getEmptyCardListView(): TextView? = emptyCardListView

   override fun provideResetOperationView(factoryResetDelegate: FactoryResetDelegate): OperationView<ResetSmartCardCommand> {
      return FactoryResetOperationView.create(context,
            { factoryResetDelegate.factoryReset() },
            { },
            R.string.wallet_error_enter_pin_title,
            R.string.wallet_error_enter_pin_msg,
            R.string.wallet_retry_label,
            R.string.wallet_cancel_label,
            R.string.wallet_loading,
            false)
   }

   override fun getPresenter(): CardListPresenter = screenPresenter

   override fun inflateView(layoutInflater: LayoutInflater, viewGroup: ViewGroup): View =
         layoutInflater.inflate(R.layout.screen_wallet_cardlist, viewGroup, false)

   override fun supportConnectionStatusLabel() = true

   override fun supportHttpConnectionStatusLabel() = true

   override fun screenModule(): Any? = CardListScreenModule()

   companion object {
      private val KEY_SHOW_UPDATE_BUTTON_STATE = "CardListScreen#KEY_SHOW_UPDATE_BUTTON_STATE"
      private val VISIBLE_SCALE = 0.64
      private val KEY_LOADED_CARDS_LIST = "CardListScreen#KEY_CARD_LIST"
   }
}
