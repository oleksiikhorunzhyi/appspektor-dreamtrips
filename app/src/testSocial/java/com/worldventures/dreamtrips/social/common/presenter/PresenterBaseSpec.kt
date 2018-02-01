package com.worldventures.dreamtrips.social.common.presenter

import android.content.Context
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.whenever
import com.worldventures.core.janet.SessionActionPipeCreator
import com.worldventures.core.model.session.FeatureManager
import com.worldventures.core.model.session.SessionHolder
import com.worldventures.core.service.ConnectionInfoProvider
import com.worldventures.core.service.analytics.AdobeTracker
import com.worldventures.core.service.analytics.AnalyticsInteractor
import com.worldventures.core.service.analytics.ApptentiveTracker
import com.worldventures.core.service.analytics.Tracker
import com.worldventures.core.test.common.AndroidRxJavaSchedulerInitializer
import com.worldventures.core.test.common.Injector
import com.worldventures.core.test.common.RxJavaSchedulerInitializer
import com.worldventures.core.test.janet.MockAnalyticsService
import com.worldventures.core.utils.HttpErrorHandlingUtil
import com.worldventures.dreamtrips.core.navigation.ActivityRouter
import com.worldventures.dreamtrips.modules.common.presenter.delegate.OfflineWarningDelegate
import io.techery.janet.Janet

import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.Spec
import org.junit.platform.runner.JUnitPlatform
import org.junit.runner.RunWith

@RunWith(JUnitPlatform::class)
abstract class PresenterBaseSpec(spekBody: Spec.() -> Unit) : Spek(spekBody) {

   companion object {

      var context: Context = mock()
      var activityRouter: ActivityRouter = mock()
      var featureManager: FeatureManager = mock()
      var offlineWarningDelegate: OfflineWarningDelegate = mock()
      var connectionInfoProvider: ConnectionInfoProvider = mock()
      var httpErrorHandlingUtil: HttpErrorHandlingUtil = mock()

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

            registerProvider(Context::class.java, { context })
            registerProvider(ActivityRouter::class.java, { activityRouter })
            registerProvider(SessionHolder::class.java, { sessionHolder })
            registerProvider(AnalyticsInteractor::class.java, { AnalyticsInteractor(pipeCreator) })
            registerProvider(FeatureManager::class.java, { featureManager })
            registerProvider(OfflineWarningDelegate::class.java, { offlineWarningDelegate })
            registerProvider(ConnectionInfoProvider::class.java, { connectionInfoProvider })
            registerProvider(HttpErrorHandlingUtil::class.java, { httpErrorHandlingUtil })
         }
      }
   }
}
