package com.worldventures.social

import com.worldventures.core.test.common.RxJavaSchedulerInitializer
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

      inline fun <reified T : Any> any() = Mockito.any(T::class.java)
   }
}