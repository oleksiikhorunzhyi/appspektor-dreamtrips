package com.worldventures.wallet.service.command.offline_mode

import com.worldventures.janet.injection.InjectableAction
import com.worldventures.wallet.domain.storage.disk.RecordsStorage
import io.techery.janet.Command
import io.techery.janet.command.annotations.CommandAction
import javax.inject.Inject

@CommandAction
class OfflineModeStatusCommand private constructor(private val func: (Boolean) -> Boolean) : Command<Boolean>(), InjectableAction {

   @Inject lateinit var recordsStorage: RecordsStorage

   @Throws(Throwable::class)
   override fun run(callback: Command.CommandCallback<Boolean>) {
      val storedValue = recordsStorage.readOfflineModeState()
      val newValue = func.invoke(storedValue)

      if (newValue xor storedValue) {
         recordsStorage.saveOfflineModeState(newValue)
      }

      callback.onSuccess(newValue)
   }

   companion object {

      fun fetch(): OfflineModeStatusCommand {
         return OfflineModeStatusCommand({ it })
      }

      fun save(status: Boolean): OfflineModeStatusCommand {
         return OfflineModeStatusCommand({ status })
      }
   }

}
