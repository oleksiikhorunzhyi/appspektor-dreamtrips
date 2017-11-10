package com.worldventures.wallet.service.command

import com.worldventures.janet.injection.InjectableAction
import com.worldventures.wallet.domain.entity.CardStatus
import com.worldventures.wallet.domain.entity.SmartCard
import com.worldventures.wallet.service.SmartCardInteractor
import io.techery.janet.Command
import io.techery.janet.command.annotations.CommandAction
import rx.Observable
import javax.inject.Inject

@CommandAction
class ActivateSmartCardCommand : Command<SmartCard>(), InjectableAction {

   @Inject lateinit var smartCardInteractor: SmartCardInteractor

   @Throws(Throwable::class)
   override fun run(callback: Command.CommandCallback<SmartCard>) {
      smartCardInteractor.activeSmartCardPipe()
            .createObservableResult(ActiveSmartCardCommand { it.copy(cardStatus = CardStatus.ACTIVE) })
            .map { it.result }
            .doOnNext { setDefaultValues() }
            .subscribe({ callback.onSuccess(it) }, { callback.onFail(it) })
   }

   private fun setDefaultValues(): Observable<SetAutoClearSmartCardDelayCommand> {
      return smartCardInteractor.autoClearDelayPipe()
            .createObservableResult(SetAutoClearSmartCardDelayCommand(DEFAULT_AUTO_CLEAR_DELAY_CLEAR_RECORDS_MINS))
   }

   companion object {

      private val DEFAULT_AUTO_CLEAR_DELAY_CLEAR_RECORDS_MINS = (2 * 60 * 24).toLong()
   }
}
