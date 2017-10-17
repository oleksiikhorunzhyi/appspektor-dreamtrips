package com.worldventures.dreamtrips.wallet.ui.common

import com.trello.rxlifecycle.RxLifecycle
import com.worldventures.dreamtrips.common.AndroidRxJavaSchedulerInitializer
import com.worldventures.dreamtrips.common.RxJavaSchedulerInitializer
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.RxLifecycleView
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import rx.subjects.BehaviorSubject

abstract class BaseTest {

   init {
      MockitoAnnotations.initMocks(this)
   }

   companion object {
      init {
         AndroidRxJavaSchedulerInitializer.init()
         RxJavaSchedulerInitializer.init()
      }


      fun <V : RxLifecycleView> mockScreen(clazz: Class<V>) : V {
         val screen : V = Mockito.mock(clazz)
         Mockito.`when`(screen.bindUntilDetach<Any>()).thenReturn(RxLifecycle.bind(BehaviorSubject.create<Any>()))
         return screen
      }
   }
}