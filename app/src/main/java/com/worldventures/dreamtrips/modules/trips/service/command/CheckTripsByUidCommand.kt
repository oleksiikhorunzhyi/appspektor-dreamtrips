package com.worldventures.dreamtrips.modules.trips.service.command

import com.worldventures.dreamtrips.core.repository.SnappyRepository
import com.worldventures.janet.injection.InjectableAction
import io.techery.janet.Command
import io.techery.janet.command.annotations.CommandAction
import javax.inject.Inject

@CommandAction
class CheckTripsByUidCommand(private val uids: List<String>) : Command<Boolean>(), InjectableAction {

   @Inject internal lateinit var snappyRepository: SnappyRepository

   @Throws(Throwable::class)
   override fun run(callback: Command.CommandCallback<Boolean>) {
      callback.onSuccess(snappyRepository.hasTripsDetailsForUids(uids))
   }
}
