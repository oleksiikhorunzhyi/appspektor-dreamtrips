package com.worldventures.dreamtrips.social.bucket

import android.content.Context
import android.test.mock.MockContext
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.whenever
import com.worldventures.core.converter.Converter
import com.worldventures.core.janet.SessionActionPipeCreator
import com.worldventures.core.janet.cache.storage.ActionStorage
import com.worldventures.core.model.User
import com.worldventures.core.model.session.SessionHolder
import com.worldventures.core.model.session.UserSession
import com.worldventures.core.modules.infopages.StaticPageProvider
import com.worldventures.core.service.UploadingFileManager
import com.worldventures.core.service.UriPathProvider
import com.worldventures.core.storage.complex_objects.Optional
import com.worldventures.dreamtrips.BaseSpec
import com.worldventures.dreamtrips.api.bucketlist.model.BucketItemSimple
import com.worldventures.dreamtrips.api.bucketlist.model.BucketStatus
import com.worldventures.dreamtrips.api.bucketlist.model.BucketType
import com.worldventures.dreamtrips.api.bucketlist.model.ImmutableBucketItemSimple
import com.worldventures.dreamtrips.core.api.uploadery.UploaderyInteractor
import com.worldventures.dreamtrips.social.domain.mapping.*
import com.worldventures.dreamtrips.social.domain.storage.SocialSnappyRepository
import com.worldventures.dreamtrips.social.ui.bucketlist.model.converter.*
import com.worldventures.dreamtrips.social.ui.bucketlist.service.BucketInteractor
import io.techery.janet.CommandActionService
import io.techery.janet.Janet
import io.techery.janet.http.test.MockHttpActionService
import io.techery.mappery.Mappery
import io.techery.mappery.MapperyContext
import org.jetbrains.spek.api.dsl.SpecBody
import org.junit.rules.TemporaryFolder
import java.util.*

abstract class BucketInteractorBaseSpec(speckBody: SpecBody.() -> Unit) : BaseSpec(speckBody) {
   companion object BaseCompanion {
      val MOCK_USER_ID = 1
      val TEST_IMAGE_PATH = TemporaryFolder().createFileAndGetPath("TestPhoto.jpeg")

      val mockSessionHolder: SessionHolder = mock()
      val userSession: UserSession = mock()
      val staticPageProvider: StaticPageProvider = mock()

      lateinit var bucketInteractor: BucketInteractor

      fun setup(storageSet: () -> Set<ActionStorage<*>>,
                mockDb: SocialSnappyRepository,
                httpService: () -> MockHttpActionService) {
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
         daggerCommandActionService.registerProvider(SocialSnappyRepository::class.java) { mockDb }
         daggerCommandActionService.registerProvider(SessionHolder::class.java) { mockSessionHolder }
         daggerCommandActionService.registerProvider(BucketInteractor::class.java) { bucketInteractor }
         daggerCommandActionService.registerProvider(UploaderyInteractor::class.java) { UploaderyInteractor(janet) }
         daggerCommandActionService.registerProvider(Context::class.java, { MockContext() })
         daggerCommandActionService.registerProvider(StaticPageProvider::class.java, { staticPageProvider })

         val uploadingFileManager: UploadingFileManager = mock()
         daggerCommandActionService.registerProvider(UploadingFileManager::class.java, { uploadingFileManager })
         whenever(uploadingFileManager.copyFileIfNeed(anyString())).thenReturn(TEST_IMAGE_PATH)

         val uriPathProvider: UriPathProvider = UriPathProvider { TEST_IMAGE_PATH }
         daggerCommandActionService.registerProvider(UriPathProvider::class.java, { uriPathProvider })

         bucketInteractor = BucketInteractor(SessionActionPipeCreator(janet))

         val mockUser = mock<User>()

         whenever(mockUser.id).thenReturn(MOCK_USER_ID)
         whenever(userSession.user()).thenReturn(mockUser)
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

      fun TemporaryFolder.createFileAndGetPath(fileName: String): String {
         this.create()
         return this.newFile(fileName).path
      }
   }
}
