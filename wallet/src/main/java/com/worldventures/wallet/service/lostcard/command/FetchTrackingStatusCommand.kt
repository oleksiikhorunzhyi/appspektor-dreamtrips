package com.worldventures.wallet.service.lostcard.command

import com.worldventures.janet.injection.InjectableAction
import com.worldventures.wallet.service.lostcard.LostCardRepository
import io.techery.janet.Command
import io.techery.janet.command.annotations.CommandAction
import javax.inject.Inject

@CommandAction
class FetchTrackingStatusCommand : Command<Boolean>(), InjectableAction {

   @Inject lateinit var lostCardRepository: LostCardRepository

   @Throws(Throwable::class)
   override fun run(callback: Command.CommandCallback<Boolean>) {
      callback.onSuccess(lostCardRepository.isEnableTracking)
   }
}
