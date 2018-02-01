package com.worldventures.wallet.ui.wizard.assign.impl

import com.worldventures.wallet.service.RecordInteractor
import com.worldventures.wallet.service.SmartCardInteractor
import com.worldventures.wallet.service.WalletAnalyticsInteractor
import com.worldventures.wallet.service.WizardInteractor
import com.worldventures.wallet.service.command.wizard.AddDummyRecordCommand
import com.worldventures.wallet.service.command.wizard.WizardCompleteCommand
import com.worldventures.wallet.ui.common.base.WalletDeviceConnectionDelegate
import com.worldventures.wallet.ui.common.base.WalletPresenterImpl
import com.worldventures.wallet.ui.common.navigation.Navigator
import com.worldventures.wallet.ui.wizard.assign.WizardAssignDelegate
import com.worldventures.wallet.ui.wizard.assign.WizardAssignUserPresenter
import com.worldventures.wallet.ui.wizard.assign.WizardAssignUserScreen
import com.worldventures.wallet.util.WalletFeatureHelper

import io.techery.janet.operationsubscriber.OperationActionSubscriber
import rx.android.schedulers.AndroidSchedulers

class WizardAssignUserPresenterImpl(navigator: Navigator,
                                    deviceConnectionDelegate: WalletDeviceConnectionDelegate,
                                    private val smartCardInteractor: SmartCardInteractor,
                                    private val wizardInteractor: WizardInteractor,
                                    private val recordInteractor: RecordInteractor,
                                    private val analyticsInteractor: WalletAnalyticsInteractor,
                                    private val walletFeatureHelper: WalletFeatureHelper)
   : WalletPresenterImpl<WizardAssignUserScreen>(navigator, deviceConnectionDelegate), WizardAssignUserPresenter {

   private lateinit var wizardAssignDelegate: WizardAssignDelegate

   override fun attachView(view: WizardAssignUserScreen) {
      super.attachView(view)
      this.wizardAssignDelegate = WizardAssignDelegate.create(getView().provisionMode, wizardInteractor,
            recordInteractor, analyticsInteractor, smartCardInteractor, walletFeatureHelper, navigator)
      observeComplete()
      onWizardComplete()
   }

   override fun onWizardComplete() {
      wizardInteractor.addDummyPipe.send(AddDummyRecordCommand())
   }

   override fun onWizardCancel() {
      navigator.goBack()
   }

   private fun observeComplete() {
      wizardInteractor.addDummyPipe
            .observe()
            .compose(view.bindUntilDetach())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(OperationActionSubscriber.forView(view.provideDummyRecordOperationView())
                  .onSuccess { wizardInteractor.completePipe.send(WizardCompleteCommand()) }
                  .create())

      wizardInteractor.completePipe
            .observe()
            .compose(view.bindUntilDetach())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(OperationActionSubscriber.forView(view.provideOperationView())
                  .onSuccess { wizardAssignDelegate.onAssignUserSuccess(view) }
                  .create())
   }

   override fun retryUploadDummyCards() {
      onWizardComplete()
   }

   override fun cancelUploadDummyCards() {
      wizardInteractor.completePipe.send(WizardCompleteCommand())
   }
}
