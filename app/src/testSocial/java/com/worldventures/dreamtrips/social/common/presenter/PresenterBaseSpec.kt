package com.worldventures.dreamtrips.social.common.presenter

import android.content.Context
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.whenever
import com.techery.spares.session.SessionHolder
import com.worldventures.dreamtrips.common.AndroidRxJavaSchedulerInitializer
import com.worldventures.dreamtrips.common.Injector
import com.worldventures.dreamtrips.common.RxJavaSchedulerInitializer
import com.worldventures.dreamtrips.common.janet.service.MockAnalyticsService
import com.worldventures.dreamtrips.core.api.PhotoUploadingManagerS3
import com.worldventures.dreamtrips.core.janet.SessionActionPipeCreator
import com.worldventures.dreamtrips.core.navigation.ActivityRouter
import com.worldventures.dreamtrips.core.session.acl.FeatureManager
import com.worldventures.dreamtrips.core.utils.HttpErrorHandlingUtil
import com.worldventures.dreamtrips.core.utils.tracksystem.*
import com.worldventures.dreamtrips.modules.common.delegate.system.ConnectionInfoProvider
import com.worldventures.dreamtrips.modules.common.presenter.delegate.OfflineWarningDelegate
import io.techery.janet.Janet
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.Spec
import org.junit.platform.runner.JUnitPlatform
import org.junit.runner.RunWith

@RunWith(JUnitPlatform::class)
abstract class PresenterBaseSpec(spekBody: Spec.() -> Unit) : Spek(spekBody) {

   companion object {
      init {
         RxJavaSchedulerInitializer.init()
         AndroidRxJavaSchedulerInitializer.init()
         val apptentiveTracker = mock<Tracker>()
         whenever(apptentiveTracker.key).thenReturn(ApptentiveTracker.TRACKER_KEY)
         val adobeTracker = mock<Tracker>()
         whenever(adobeTracker.key).thenReturn(AdobeTracker.TRACKER_KEY)
      }

      fun prepareInjector(sessionHolder: SessionHolder = mock()): Injector {
         return Injector().apply {
            val janet = Janet.Builder().addService(MockAnalyticsService()).build()
            val pipeCreator = SessionActionPipeCreator(janet)

            registerProvider(Context::class.java, { mock() })
            registerProvider(ActivityRouter::class.java, { mock() })
            registerProvider(SessionHolder::class.java, { sessionHolder })
            registerProvider(AnalyticsInteractor::class.java, { AnalyticsInteractor(pipeCreator) })
            registerProvider(FeatureManager::class.java, { mock() })
            registerProvider(PhotoUploadingManagerS3::class.java, { mock() })
            registerProvider(OfflineWarningDelegate::class.java, { mock() })
            registerProvider(ConnectionInfoProvider::class.java, { mock() })
            registerProvider(HttpErrorHandlingUtil::class.java, { mock() })
         }
      }
   }
}
