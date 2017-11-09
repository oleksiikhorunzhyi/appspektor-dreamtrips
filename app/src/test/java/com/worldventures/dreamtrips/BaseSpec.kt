package com.worldventures.dreamtrips

//import com.worldventures.dreamtrips.core.janet.cache.storage.MultipleActionStorage
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.spy
import com.nhaarman.mockito_kotlin.whenever
import com.worldventures.core.janet.cache.CacheResultWrapper
import com.worldventures.core.janet.cache.storage.ActionStorage
import com.worldventures.core.model.session.SessionHolder
import com.worldventures.core.model.session.UserSession
import com.worldventures.core.storage.complex_objects.Optional
import com.worldventures.core.test.common.RxJavaSchedulerInitializer
import com.worldventures.core.test.janet.MockDaggerActionService
import com.worldventures.core.test.janet.StubServiceWrapper
import io.techery.janet.ActionService
import io.techery.janet.command.test.Contract
import io.techery.janet.command.test.MockCommandActionService
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

//      fun CacheResultWrapper.bindMultipleStorageSet(multipleStorageSet: Set<MultipleActionStorage<*>>): CacheResultWrapper {
//         multipleStorageSet.forEach { storage ->
//            storage.actionClasses.forEach { actionClass ->
//               bindStorage(actionClass, storage)
//            }
//         }
//
//         return this
//      }

      fun StubServiceWrapper.spyCallback(): StubServiceWrapper.Callback {
         callback = spy()
         return callback
      }

      fun mockActionService(service: ActionService, mockContracts: List<Contract>) = MockCommandActionService.Builder()
            .apply {
               actionService(service)
               for (contract in mockContracts) addContract(contract)
            }
            .build()

      fun mockSessionHolder(): SessionHolder {
         val sessionHolder: SessionHolder = mock()
         val userSession: UserSession = mock()
         whenever(sessionHolder.get()).thenReturn(Optional.of(userSession))
         return sessionHolder
      }

      //hard code because mockito_kotlin doesn't work with String correctly
      fun anyString() = Mockito.any(String::class.java)

      inline fun <reified T : Any> any() = Mockito.any(T::class.java)
   }
}
