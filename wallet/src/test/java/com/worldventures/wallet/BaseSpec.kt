package com.worldventures.wallet

import com.worldventures.janet.cache.CacheResultWrapper
import com.worldventures.janet.cache.storage.ActionStorage
import com.worldventures.core.test.common.RxJavaSchedulerInitializer
import com.worldventures.core.test.janet.MockDaggerActionService
import com.worldventures.core.test.janet.StubServiceWrapper
import io.techery.janet.ActionService
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.SpecBody
import org.junit.platform.runner.JUnitPlatform
import org.junit.runner.RunWith
import org.mockito.Mockito

@RunWith(JUnitPlatform::class)
abstract class BaseSpec(spekBody: SpecBody.() -> Unit) : Spek(spekBody) {
   companion object {
      init {
         RxJavaSchedulerInitializer.init()
      }

      fun ActionService.wrapCache() = CacheResultWrapper(this)

      fun ActionService.wrapDagger() = MockDaggerActionService(this)

      fun ActionService.wrapStub() = StubServiceWrapper(this)

      fun CacheResultWrapper.bindStorageSet(storageSet: Set<ActionStorage<*>>): CacheResultWrapper {
         storageSet.forEach {
            bindStorage(it.actionClass, it)
         }

         return this
      }

      inline fun <reified T : Any> any() = Mockito.any(T::class.java)
   }
}
