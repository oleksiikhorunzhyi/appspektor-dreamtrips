package com.worldventures.dreamtrips.social.bucket

import android.content.Context
import android.test.mock.MockContext
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.spy
import com.nhaarman.mockito_kotlin.whenever
import com.techery.spares.session.SessionHolder
import com.techery.spares.storage.complex_objects.Optional
import com.worldventures.dreamtrips.BaseSpec
import com.worldventures.dreamtrips.api.bucketlist.model.BucketItemSimple
import com.worldventures.dreamtrips.api.bucketlist.model.BucketStatus
import com.worldventures.dreamtrips.api.bucketlist.model.BucketType
import com.worldventures.dreamtrips.api.bucketlist.model.ImmutableBucketItemSimple
import com.worldventures.dreamtrips.core.api.uploadery.UploaderyInteractor
import com.worldventures.dreamtrips.core.janet.SessionActionPipeCreator
import com.worldventures.dreamtrips.core.janet.cache.storage.ActionStorage
import com.worldventures.dreamtrips.core.repository.SnappyRepository
import com.worldventures.dreamtrips.core.session.UserSession
import com.worldventures.dreamtrips.modules.bucketlist.model.converter.*
import com.worldventures.dreamtrips.modules.bucketlist.service.BucketInteractor
import com.worldventures.dreamtrips.modules.bucketlist.service.storage.BucketMemoryStorage
import com.worldventures.dreamtrips.modules.common.model.User
import com.worldventures.dreamtrips.modules.infopages.StaticPageProvider
import com.worldventures.dreamtrips.modules.mapping.converter.*
import io.techery.janet.CommandActionService
import io.techery.janet.Janet
import io.techery.janet.http.test.MockHttpActionService
import io.techery.mappery.Mappery
import io.techery.mappery.MapperyContext
import org.jetbrains.spek.api.dsl.SpecBody
import java.util.*

abstract class BucketInteractorBaseSpec(speckBody: SpecBody.() -> Unit) : BaseSpec(speckBody) {
   companion object BaseCompanion {
      val MOCK_USER_ID = 1

      lateinit var mockMemoryStorage: BucketMemoryStorage
      lateinit var mockDb: SnappyRepository
      lateinit var mockSessionHolder: SessionHolder<UserSession>
      lateinit var userSession: UserSession
      lateinit var staticPageProvider: StaticPageProvider
      lateinit var bucketInteractor: BucketInteractor

      fun setup(storageSet: () -> Set<ActionStorage<*>>, httpService: () -> MockHttpActionService) {
         mockMemoryStorage = spy()
         mockDb = spy()

         mockSessionHolder = mock()
         userSession = mock()
         staticPageProvider = mock()

         val daggerCommandActionService = CommandActionService()
               .wrapCache()
               .bindStorageSet(storageSet())
               .wrapDagger()
         val janet = Janet.Builder()
               .addService(daggerCommandActionService)
               .addService(httpService().wrapStub().wrapCache())
               .build()

         daggerCommandActionService.registerProvider(Janet::class.java) { janet }
         daggerCommandActionService.registerProvider(MapperyContext::class.java) { getMappery() }
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

      private fun getMappery(): Mappery {
         val builder = Mappery.Builder()
         for (converter in constructConverters()) {
            builder.map(converter.sourceClass()).to(converter.targetClass(), converter)
         }
         return builder.build()
      }

      fun constructConverters(): List<Converter<Any, Any>> {
         return listOf(castConverter(BucketItemSimpleConverter()),
               castConverter(BucketItemSocializedConverter()),
               castConverter(ShortProfilesConverter()),
               castConverter(BucketTypeConverter()),
               castConverter(BucketPhotoConverter()),
               castConverter(BucketPhotoBodyConverter()),
               castConverter(ReverseBucketBodyConverter()),
               castConverter(ReverseBucketPostBodyConverter()),
               castConverter(ReverseBucketUpdateBodyConverter()),
               castConverter(ReverseBucketPostBodyToUpdateBodyConverter()),
               castConverter(ReverseBucketCoverBodyToUpdateBodyConverter())
         )
      }

      // TODO Improve this, there should not be need for cast, but currently
      // it's not compilable if we return Converter<*, *>
      fun castConverter(converter: Converter<*, *>): Converter<Any, Any> {
         return converter as Converter<Any, Any>
      }

      fun getStubApiBucket(id: Int): BucketItemSimple {
         return  ImmutableBucketItemSimple.builder()
               .id(id)
               .uid("$id")
               .creationDate(Date())
               .link("")
               .name("$id")
               .type(BucketType.ACTIVITY)
               .status(BucketStatus.NEW)
               .bucketPhoto(emptyList())
               .tags(emptyList())
               .friends(emptyList())
               .build()
      }
   }
}
