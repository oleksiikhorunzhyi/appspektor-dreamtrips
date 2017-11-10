package com.worldventures.wallet.service.command.device

import com.worldventures.core.janet.cache.CacheOptions
import com.worldventures.core.janet.cache.CachedAction
import com.worldventures.core.janet.cache.ImmutableCacheOptions
import com.worldventures.wallet.domain.entity.SmartCardFirmware

import io.techery.janet.ActionHolder
import io.techery.janet.Command
import io.techery.janet.command.annotations.CommandAction
import rx.functions.Func1

@CommandAction
class SmartCardFirmwareCommand private constructor(private val func: Func1<SmartCardFirmware, SmartCardFirmware>) : Command<SmartCardFirmware>(), CachedAction<SmartCardFirmware> {
   private var cachedSmartCardFirmware: SmartCardFirmware? = null

   @Throws(Throwable::class)
   override fun run(callback: Command.CommandCallback<SmartCardFirmware>) {
      if (cachedSmartCardFirmware == null) {
         cachedSmartCardFirmware = createDefault()
      }
      val smartCardFirmware = func.call(cachedSmartCardFirmware)
      callback.onSuccess(smartCardFirmware)
   }

   override fun getCacheData(): SmartCardFirmware {
      return result
   }

   override fun onRestore(holder: ActionHolder<*>, cache: SmartCardFirmware) {
      cachedSmartCardFirmware = cache
   }

   override fun getCacheOptions(): CacheOptions {
      return ImmutableCacheOptions.builder()
            .saveToCache(true)
            .restoreFromCache(true)
            .sendAfterRestore(true)
            .build()
   }

   private fun createDefault(): SmartCardFirmware {
      return SmartCardFirmware()
   }

   companion object {

      fun fetch(): SmartCardFirmwareCommand {
         return SmartCardFirmwareCommand(Func1 { smartCardFirmware -> smartCardFirmware })
      }

      fun bundleVersion(bundleVersion: String): SmartCardFirmwareCommand {
         return update({ it.copy(firmwareBundleVersion = bundleVersion) })
      }

      fun save(smartCardFirmware: SmartCardFirmware): SmartCardFirmwareCommand {
         return SmartCardFirmwareCommand(Func1 { smartCardFirmware })
      }

      private fun update(builderFunc: (SmartCardFirmware) -> SmartCardFirmware): SmartCardFirmwareCommand {
         return SmartCardFirmwareCommand(Func1 { builderFunc.invoke(it) }
         )
      }
   }
}
