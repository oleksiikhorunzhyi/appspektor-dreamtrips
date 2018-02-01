package com.worldventures.wallet.service.command.offline_mode

import com.worldventures.janet.injection.InjectableAction
import com.worldventures.wallet.service.SmartCardInteractor

import javax.inject.Inject

import io.techery.janet.Command
import io.techery.janet.command.annotations.CommandAction
import rx.Observable

/**
 * Checks whether offline mode is enabled.
 * Then sets offline mode to default state (disabled) if needed.
 */
@CommandAction
class RestoreOfflineModeDefaultStateCommand : Command<Void>(), InjectableAction {

   @Inject lateinit var smartCardInteractor: SmartCardInteractor

   @Throws(Throwable::class)
   override fun run(callback: Command.CommandCallback<Void>) {
      smartCardInteractor.offlineModeStatusPipe()
            .createObservableResult(OfflineModeStatusCommand.fetch())
            .map { it.result }
            .flatMap { offlineModeEnabled ->
               if (offlineModeEnabled)
                  smartCardInteractor.switchOfflineModePipe()
                        .createObservableResult(SwitchOfflineModeCommand())
               else
                  Observable.just<SwitchOfflineModeCommand>(null)
            }
            .map<Void> { null }
            .subscribe({ callback.onSuccess(it) }, { callback.onFail(it) })
   }

}
