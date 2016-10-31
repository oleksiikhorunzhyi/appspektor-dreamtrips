package com.worldventures.dreamtrips.social.bucket.spek

import android.content.Context
import android.test.mock.MockContext
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.spy
import com.nhaarman.mockito_kotlin.whenever
import com.techery.spares.session.SessionHolder
import com.techery.spares.storage.complex_objects.Optional
import com.worldventures.dreamtrips.BaseSpec
import com.worldventures.dreamtrips.core.api.uploadery.UploaderyInteractor
import com.worldventures.dreamtrips.core.janet.SessionActionPipeCreator
import com.worldventures.dreamtrips.core.janet.cache.storage.ActionStorage
import com.worldventures.dreamtrips.core.repository.SnappyRepository
import com.worldventures.dreamtrips.core.session.UserSession
import com.worldventures.dreamtrips.modules.bucketlist.service.BucketInteractor
import com.worldventures.dreamtrips.modules.bucketlist.service.storage.BucketMemoryStorage
import com.worldventures.dreamtrips.modules.common.model.User
import com.worldventures.dreamtrips.modules.infopages.StaticPageProvider
import io.techery.janet.CommandActionService
import io.techery.janet.Janet
import io.techery.janet.http.test.MockHttpActionService
import org.jetbrains.spek.api.DescribeBody

abstract class BucketInteractorBaseSpec(speckBody: DescribeBody.() -> Unit) : BaseSpec(speckBody) {
   companion object BaseCompanion {
      val MOCK_USER_ID = 1

      val mockMemoryStorage: BucketMemoryStorage = spy()
      val mockDb: SnappyRepository = spy()

      val mockSessionHolder: SessionHolder<UserSession> = mock()
      val userSession: UserSession = mock()
      val staticPageProvider: StaticPageProvider = mock()

      lateinit var bucketInteractor: BucketInteractor

      fun setup(storageSet: () -> Set<ActionStorage<*>>, httpService: () -> MockHttpActionService) {
         val daggerCommandActionService = CommandActionService()
               .wrapCache()
               .bindStorageSet(storageSet())
               .wrapDagger()
         val janet = Janet.Builder()
               .addService(daggerCommandActionService)
               .addService(httpService().wrapStub().wrapCache())
               .build()

         daggerCommandActionService.registerProvider(Janet::class.java) { janet }
         daggerCommandActionService.registerProvider(SnappyRepository::class.java) { mockDb }
         daggerCommandActionService.registerProvider(SessionHolder::class.java) { mockSessionHolder }
         daggerCommandActionService.registerProvider(BucketInteractor::class.java) { bucketInteractor }
         daggerCommandActionService.registerProvider(UploaderyInteractor::class.java) { UploaderyInteractor(janet) }
         daggerCommandActionService.registerProvider(Context::class.java, { MockContext() })
         daggerCommandActionService.registerProvider(StaticPageProvider::class.java, { staticPageProvider })

         bucketInteractor = BucketInteractor(SessionActionPipeCreator(janet))

         val mockUser = mock<User>()

         whenever(mockUser.id).thenReturn(MOCK_USER_ID)
         whenever(userSession.user).thenReturn(mockUser)
         whenever(mockSessionHolder.get()).thenReturn(Optional.of(userSession))
         whenever(staticPageProvider.uploaderyUrl).thenReturn("http://test-uploadery")
      }
   }
}
