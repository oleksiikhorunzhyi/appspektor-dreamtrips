package com.worldventures.wallet.ui.settings.security.offline_mode.impl

import android.content.Context
import android.support.v7.widget.Toolbar
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

import com.afollestad.materialdialogs.MaterialDialog
import com.jakewharton.rxbinding.widget.RxCompoundButton
import com.worldventures.core.utils.ProjectTextUtils
import com.worldventures.wallet.R
import com.worldventures.wallet.service.command.offline_mode.SwitchOfflineModeCommand
import com.worldventures.wallet.ui.common.base.WalletBaseController
import com.worldventures.wallet.ui.common.helper2.error.DialogErrorView
import com.worldventures.wallet.ui.common.helper2.error.ErrorViewFactory
import com.worldventures.wallet.ui.common.helper2.error.ErrorViewProvider
import com.worldventures.wallet.ui.common.helper2.progress.SimpleDialogProgressView
import com.worldventures.wallet.ui.settings.security.offline_mode.WalletOfflineModeSettingsPresenter
import com.worldventures.wallet.ui.settings.security.offline_mode.WalletOfflineModeSettingsScreen
import com.worldventures.wallet.ui.widget.WalletSwitcher
import com.worldventures.wallet.util.NetworkUnavailableException

import javax.inject.Inject

import io.techery.janet.operationsubscriber.view.ComposableOperationView
import io.techery.janet.operationsubscriber.view.ErrorView
import io.techery.janet.operationsubscriber.view.OperationView
import rx.Observable

class WalletOfflineModeSettingsScreenImpl : WalletBaseController<WalletOfflineModeSettingsScreen, WalletOfflineModeSettingsPresenter>(), WalletOfflineModeSettingsScreen {

   private lateinit var offlineModeSwitcher: WalletSwitcher
   @Inject lateinit var screenPresenter: WalletOfflineModeSettingsPresenter

   private lateinit var enableOfflineModeObservable: Observable<Boolean>

   private val defaultErrorDialogProvider: DialogErrorView<SwitchOfflineModeCommand>
      get() = object : DialogErrorView<SwitchOfflineModeCommand>(context) {
         override fun createDialog(command: SwitchOfflineModeCommand, throwable: Throwable, context: Context) =
               MaterialDialog.Builder(getContext())
                     .content(R.string.wallet_offline_mode_error_default_message)
                     .positiveText(R.string.wallet_retry_label)
                     .onPositive { _, _ -> presenter.switchOfflineMode() }
                     .negativeText(R.string.wallet_cancel_label)
                     .onNegative { _, _ -> presenter.switchOfflineModeCanceled() }
                     .cancelable(false)
                     .build()
      }

   private val networkUnavailableDialogProvider: ErrorViewProvider<SwitchOfflineModeCommand>
      get() = object : ErrorViewProvider<SwitchOfflineModeCommand> {
         override fun forThrowable() = NetworkUnavailableException::class.java

         override fun create(command: SwitchOfflineModeCommand, parentThrowable: Throwable?, throwable: Throwable):
               ErrorView<SwitchOfflineModeCommand> = object : DialogErrorView<SwitchOfflineModeCommand>(context) {
            override fun createDialog(command: SwitchOfflineModeCommand, throwable: Throwable, context: Context) =
                  MaterialDialog.Builder(getContext())
                        .content(R.string.wallet_offline_mode_error_no_internet_message)
                        .positiveText(R.string.wallet_settings)
                        .onPositive { _, _ -> presenter.navigateToSystemSettings() }
                        .negativeText(R.string.wallet_cancel_label)
                        .onNegative { _, _ -> presenter.switchOfflineModeCanceled() }
                        .cancelable(false)
                        .dismissListener { presenter.fetchOfflineModeState() }
                        .build()
         }
      }

   override fun onFinishInflate(view: View) {
      super.onFinishInflate(view)
      val toolbar = view.findViewById<Toolbar>(R.id.toolbar)
      toolbar.setNavigationOnClickListener { presenter.goBack() }
      val tvPleaseNoteMessage = view.findViewById<TextView>(R.id.offline_mode_please_note_label)
      tvPleaseNoteMessage.text = ProjectTextUtils.fromHtml(getString(R.string.wallet_offline_mode_please_note_message))
      offlineModeSwitcher = view.findViewById(R.id.offline_mode_switcher)
      enableOfflineModeObservable = RxCompoundButton.checkedChanges(offlineModeSwitcher).skip(1)
   }

   override fun inflateView(layoutInflater: LayoutInflater, viewGroup: ViewGroup): View =
         layoutInflater.inflate(R.layout.screen_wallet_settings_offline_mode, viewGroup, false)

   override fun supportConnectionStatusLabel() = false

   override fun supportHttpConnectionStatusLabel() = false

   override fun provideOperationView(): OperationView<SwitchOfflineModeCommand> {
      return ComposableOperationView(
            SimpleDialogProgressView(context,
                  ProjectTextUtils.fromHtml(getString(R.string.wallet_offline_mode_progress_message)), false),
            ErrorViewFactory.builder<SwitchOfflineModeCommand>()
                  .addProvider(networkUnavailableDialogProvider)
                  .defaultErrorView(defaultErrorDialogProvider)
                  .build()
      )
   }

   override fun observeOfflineModeSwitcher(): Observable<Boolean> = enableOfflineModeObservable

   override fun showConfirmationDialog(enable: Boolean) {
      MaterialDialog.Builder(context)
            .content(if (enable) R.string.wallet_offline_mode_enable_message else R.string.wallet_offline_mode_disable_message)
            .positiveText(R.string.wallet_continue_label)
            .onPositive { _, _ -> presenter.switchOfflineMode() }
            .negativeText(R.string.wallet_cancel_label)
            .onNegative { _, _ -> presenter.switchOfflineModeCanceled() }
            .cancelable(false)
            .build()
            .show()
   }

   override fun setOfflineModeState(enabled: Boolean) {
      offlineModeSwitcher.setCheckedWithoutNotify(enabled)
   }

   override fun getPresenter(): WalletOfflineModeSettingsPresenter = screenPresenter

   override fun screenModule() = WalletOfflineModeSettingsScreenModule()
}
