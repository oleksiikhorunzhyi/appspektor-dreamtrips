package com.worldventures.wallet.service.lostcard.command

import com.worldventures.core.modules.settings.command.SettingsCommand
import com.worldventures.core.modules.settings.model.FlagSetting
import com.worldventures.core.modules.settings.model.Setting
import com.worldventures.core.modules.settings.service.SettingsInteractor
import com.worldventures.janet.injection.InjectableAction
import com.worldventures.wallet.domain.WalletTrackingStatusStorage
import com.worldventures.wallet.service.lostcard.LostCardRepository
import io.techery.janet.Command
import io.techery.janet.command.annotations.CommandAction
import rx.Observable
import javax.inject.Inject

@CommandAction
class UpdateTrackingStatusCommand(private val enabled: Boolean) : Command<Boolean>(), InjectableAction {

   @Inject lateinit var settingsInteractor: SettingsInteractor
   @Inject lateinit var lostCardRepository: LostCardRepository

   @Throws(Throwable::class)
   override fun run(callback: Command.CommandCallback<Boolean>) {
      settingsInteractor.settingsActionPipe()
            .createObservableResult(SettingsCommand(prepareTrackingStatus()))
            .onErrorReturn { null }
            .flatMap { saveTrackingStatus() }
            .subscribe({ callback.onSuccess(it) }, { callback.onFail(it) })
   }

   private fun saveTrackingStatus(): Observable<Boolean> {
      lostCardRepository.saveEnabledTracking(enabled)
      return Observable.just(enabled)
   }

   private fun prepareTrackingStatus(): List<Setting<*>> {
      val trackingStatusSetting = FlagSetting(WalletTrackingStatusStorage.SETTING_TRACKING_STATUS, Setting.Type.FLAG, enabled)
      return listOf<Setting<*>>(trackingStatusSetting)
   }
}
