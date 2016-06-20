package com.worldventures.dreamtrips.core.test

import android.location.Location
import android.text.TextUtils
import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.spy
import com.worldventures.dreamtrips.core.janet.cache.CacheResultWrapper
import io.techery.janet.ActionService
import org.jetbrains.spek.api.DescribeBody
import org.jetbrains.spek.api.Spek
import org.junit.Before
import org.junit.Rule
import org.mockito.Mockito
import org.powermock.api.mockito.PowerMockito
import org.powermock.api.mockito.PowerMockito.mockStatic
import org.powermock.core.classloader.annotations.PrepareForTest
import org.powermock.modules.junit4.rule.PowerMockRule
import rx.Scheduler
import rx.plugins.RxJavaPlugins
import rx.plugins.RxJavaSchedulersHook
import rx.schedulers.Schedulers

@PrepareForTest(TextUtils::class, Location::class)
open class BaseSpec(spekBody: DescribeBody.() -> Unit) : Spek(spekBody) {

    @Rule
    val rule = PowerMockRule()


    @Before
    private fun mockStatic() { //PowerMock works before running tests only
        mockStatic(TextUtils::class.java)//See http://g.co/androidstudio/not-mocked
        PowerMockito.`when`(TextUtils.isEmpty(any())).thenAnswer { invocation ->
            val arg = invocation.arguments[0] as String
            arg.isEmpty()
        }
    }

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
            return callback;
        }

        //hard code because mockito_kotlin doesn't work with String correctly
        fun anyString() = Mockito.any(String::class.java)

    }

}
