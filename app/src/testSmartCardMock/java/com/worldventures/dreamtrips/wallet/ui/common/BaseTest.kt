package com.worldventures.dreamtrips.wallet.ui.common

import com.worldventures.dreamtrips.common.AndroidRxJavaSchedulerInitializer
import com.worldventures.dreamtrips.common.RxJavaSchedulerInitializer
import org.mockito.MockitoAnnotations

abstract class BaseTest {

   init {
      MockitoAnnotations.initMocks(this)
   }

   companion object {
      init {
         AndroidRxJavaSchedulerInitializer.init()
         RxJavaSchedulerInitializer.init()
      }
   }
}