package com.worldventures.wallet.service.command.device

import com.worldventures.core.janet.cache.CacheOptions
import com.worldventures.core.janet.cache.CachedAction
import com.worldventures.core.janet.cache.ImmutableCacheOptions
import com.worldventures.wallet.domain.entity.ConnectionStatus
import com.worldventures.wallet.domain.entity.SmartCardStatus
import io.techery.janet.ActionHolder
import io.techery.janet.Command
import io.techery.janet.command.annotations.CommandAction

@CommandAction
class DeviceStateCommand private constructor(private val func: (SmartCardStatus) -> SmartCardStatus) : Command<SmartCardStatus>(), CachedAction<SmartCardStatus> {
   private var cachedSmartCardStatus: SmartCardStatus? = null

   @Throws(Throwable::class)
   override fun run(callback: Command.CommandCallback<SmartCardStatus>) {
      val status = cachedSmartCardStatus ?: createDefault()
      val newSmartCardStatus = func.invoke(status)
      callback.onSuccess(newSmartCardStatus)
   }

   override fun getCacheData(): SmartCardStatus = result

   override fun onRestore(holder: ActionHolder<*>, cache: SmartCardStatus) {
      cachedSmartCardStatus = cache
   }

   override fun getCacheOptions(): CacheOptions {
      return ImmutableCacheOptions.builder()
            .saveToCache(true)
            .restoreFromCache(true)
            .sendAfterRestore(true)
            .build()
   }

   private fun createDefault() = SmartCardStatus()

   companion object {

      fun fetch(): DeviceStateCommand {
         return DeviceStateCommand({ smartCardStatus -> smartCardStatus })
      }

      fun lock(lock: Boolean) = DeviceStateCommand({ it.copy(lock = lock) })

      fun stealthMode(stealthMode: Boolean) = DeviceStateCommand({ it.copy(stealthMode = stealthMode) })

      @Deprecated(
            message = "This method is deprecated",
            replaceWith = ReplaceWith("connection(connectionStatus: ConnectionStatus, firmwareVersion: String)"),
            level = DeprecationLevel.ERROR)
      fun connection(connectionStatus: ConnectionStatus) = DeviceStateCommand({ it.copy(connectionStatus = connectionStatus) })

      fun connection(connectionStatus: ConnectionStatus, firmwareVersion: String) =
            DeviceStateCommand({
               it.copy(connectionStatus = connectionStatus,
                     firmwareVersion = firmwareVersion)
            })

      fun battery(batteryLevel: Int) = DeviceStateCommand({ it.copy(batteryLevel = batteryLevel) })

      fun disableCardDelay(disableDelay: Long) = DeviceStateCommand({ it.copy(disableCardDelay = disableDelay) })

      fun clearFlyeDelay(clearDelay: Long) = DeviceStateCommand({ it.copy(clearFlyeDelay = clearDelay) })
   }
}
