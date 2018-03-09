package com.worldventures.wallet.ui.settings.general.reset.delegate

import com.worldventures.core.janet.composer.ActionPipeCacheWiper
import com.worldventures.wallet.service.FactoryResetInteractor
import com.worldventures.wallet.service.SmartCardInteractor
import com.worldventures.wallet.service.command.FactoryResetCommand
import com.worldventures.wallet.service.command.reset.ResetOptions
import com.worldventures.wallet.ui.common.navigation.Navigator
import io.techery.janet.operationsubscriber.OperationActionSubscriber
import io.techery.janet.smartcard.action.settings.CheckPinStatusAction
import io.techery.janet.smartcard.event.PinStatusEvent
import rx.android.schedulers.AndroidSchedulers

abstract class FactoryResetDelegate internal constructor(
      private val smartCardInteractor: SmartCardInteractor,
      private val factoryResetInteractor: FactoryResetInteractor,
      protected val navigator: Navigator) {

   private var view: FactoryResetView? = null

   @JvmOverloads
   fun factoryReset(resetOptions: ResetOptions = provideResetOptions()) =
         factoryResetInteractor.factoryResetCommandActionPipe().send(FactoryResetCommand(resetOptions))

   fun cancelFactoryReset() = factoryResetInteractor.factoryResetCommandActionPipe().cancelLatest()

   fun goBack() = navigator.goBack()

   fun bindView(view: FactoryResetView) {
      this.view = view
      factoryResetInteractor.resetSmartCardCommandActionPipe()
            .observeWithReplay()
            .compose(view.bindUntilDetach())
            .compose(ActionPipeCacheWiper(factoryResetInteractor.resetSmartCardCommandActionPipe()))
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(OperationActionSubscriber.forView(view.provideResetOperationView(this))
                  .onSuccess({ this.handleSuccessResult() })
                  .create())
   }

   protected fun checkPinStatus(pinStatusCallback: (Boolean) -> Unit) {
      val localView = view ?: throw IllegalStateException("Method bindView() is not called")
      smartCardInteractor.pinStatusEventPipe()
            .observeSuccess()
            .compose(localView.bindUntilDetach<PinStatusEvent>())
            .observeOn(AndroidSchedulers.mainThread())
            .map({ pinStatusEvent -> pinStatusEvent.pinStatus != PinStatusEvent.PinStatus.DISABLED })
            .take(1)
            .subscribe(pinStatusCallback::invoke)
      smartCardInteractor.checkPinStatusActionPipe().send(CheckPinStatusAction())
   }

   open fun startRegularFactoryReset() = factoryReset()

   protected abstract fun provideResetOptions(): ResetOptions

   protected abstract fun handleSuccessResult()
}

internal open class GeneralFactoryResetDelegate(
      smartCardInteractor: SmartCardInteractor,
      factoryResetInteractor: FactoryResetInteractor,
      navigator: Navigator)
   : FactoryResetDelegate(smartCardInteractor, factoryResetInteractor, navigator) {

   override fun handleSuccessResult() = navigator.goFactoryResetSuccess()

   override fun provideResetOptions(): ResetOptions = ResetOptions.builder()
         .withEnterPin(true)
         .build()
}

internal open class NewCardFactoryResetDelegate(
      smartCardInteractor: SmartCardInteractor,
      factoryResetInteractor: FactoryResetInteractor,
      navigator: Navigator) : FactoryResetDelegate(smartCardInteractor, factoryResetInteractor, navigator) {

   override fun provideResetOptions(): ResetOptions = ResetOptions.builder()
         .withEnterPin(true)
         .wipePaymentCards(false)
         .wipeUserSmartCardData(false)
         .build()

   override fun handleSuccessResult() = navigator.goUnassignSuccess()
}

internal class NewCardFactoryResetDelegateCheckPin(
      smartCardInteractor: SmartCardInteractor,
      factoryResetInteractor: FactoryResetInteractor,
      navigator: Navigator) : NewCardFactoryResetDelegate(smartCardInteractor, factoryResetInteractor, navigator) {

   override fun startRegularFactoryReset() {
      checkPinStatus {
         if (it) {
            performStart()
         } else {
            super.startRegularFactoryReset()
         }
      }
   }

   private fun performStart() = navigator.goEnterPinUnassign()
}

internal class GeneralResetDelegateCheckPin(
      smartCardInteractor: SmartCardInteractor,
      factoryResetInteractor: FactoryResetInteractor,
      navigator: Navigator) : GeneralFactoryResetDelegate(smartCardInteractor, factoryResetInteractor, navigator) {

   override fun startRegularFactoryReset() {
      checkPinStatus {
         if (it) {
            performStart()
         } else {
            super.startRegularFactoryReset()
         }
      }
   }

   private fun performStart() = navigator.goFactoryReset()
}
