package com.worldventures.dreamtrips.common

import android.location.Location
import android.text.TextUtils
import com.nhaarman.mockito_kotlin.spy
import com.worldventures.dreamtrips.common.janet.MockDaggerActionService
import com.worldventures.dreamtrips.common.janet.StubServiceWrapper
import com.worldventures.dreamtrips.core.janet.cache.CacheResultWrapper
import io.techery.janet.ActionService
import org.jetbrains.spek.api.DescribeBody
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.junit.JUnitSpekRunner
import org.junit.runner.RunWith
import org.mockito.Mockito
import org.powermock.core.classloader.annotations.PrepareForTest
import org.powermock.modules.junit4.PowerMockRunner
import org.powermock.modules.junit4.PowerMockRunnerDelegate
import rx.Scheduler
import rx.plugins.RxJavaPlugins
import rx.plugins.RxJavaSchedulersHook
import rx.schedulers.Schedulers

@RunWith(PowerMockRunner::class)
@PowerMockRunnerDelegate(JUnitSpekRunner::class)
@PrepareForTest(TextUtils::class, Location::class)
open class BaseSpec(spekBody: DescribeBody.() -> Unit) : Spek(spekBody) {
    companion object {
        init {
            RxJavaPlugins.getInstance().registerSchedulersHook(object : RxJavaSchedulersHook() {
                override fun getIOScheduler(): Scheduler {
                    return Schedulers.immediate()
                }
            })
        }

        fun ActionService.wrapCache() = CacheResultWrapper(this)

        fun ActionService.wrapDagger() = MockDaggerActionService(this)

        fun ActionService.wrapStub() = StubServiceWrapper(this)

        fun StubServiceWrapper.spyCallback(): StubServiceWrapper.Callback {
            callback = spy()
            return callback
        }

        //hard code because mockito_kotlin doesn't work with String correctly
        fun anyString() = Mockito.any(String::class.java)

        inline fun <reified T : Any> any() = Mockito.any(T::class.java)
    }
}