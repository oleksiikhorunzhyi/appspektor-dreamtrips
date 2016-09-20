package com.worldventures.dreamtrips

import android.location.Location
import android.text.TextUtils
import com.nhaarman.mockito_kotlin.spy
import com.worldventures.dreamtrips.common.RxJavaSchedulerInitializer
import com.worldventures.dreamtrips.core.janet.cache.CacheResultWrapper
import com.worldventures.dreamtrips.core.janet.cache.storage.ActionStorage
import com.worldventures.dreamtrips.janet.MockDaggerActionService
import com.worldventures.dreamtrips.janet.StubServiceWrapper
import io.techery.janet.ActionService
import org.jetbrains.spek.api.DescribeBody
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.junit.JUnitSpekRunner
import org.junit.runner.RunWith
import org.mockito.Mockito
import org.powermock.core.classloader.annotations.PrepareForTest
import org.powermock.modules.junit4.PowerMockRunner
import org.powermock.modules.junit4.PowerMockRunnerDelegate

@RunWith(PowerMockRunner::class)
@PowerMockRunnerDelegate(JUnitSpekRunner::class)
@PrepareForTest(Location::class, TextUtils::class)
abstract class BaseSpec(spekBody: DescribeBody.() -> Unit) : Spek(spekBody) {
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

      fun StubServiceWrapper.spyCallback(): StubServiceWrapper.Callback {
         callback = spy()
         return callback
      }

      //hard code because mockito_kotlin doesn't work with String correctly
      fun anyString() = Mockito.any(String::class.java)

      inline fun <reified T : Any> any() = Mockito.any(T::class.java)
   }
}
