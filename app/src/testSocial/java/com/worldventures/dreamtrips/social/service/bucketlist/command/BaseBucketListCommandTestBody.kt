package com.worldventures.dreamtrips.social.service.bucketlist.command

import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.whenever
import com.worldventures.core.converter.Converter
import com.worldventures.core.janet.SessionActionPipeCreator
import com.worldventures.core.model.User
import com.worldventures.core.model.session.SessionHolder
import com.worldventures.core.model.session.UserSession
import com.worldventures.core.storage.complex_objects.Optional
import com.worldventures.core.test.janet.MockDaggerActionService
import com.worldventures.dreamtrips.BaseSpec.Companion.wrapCache
import com.worldventures.dreamtrips.BaseSpec.Companion.wrapDagger
import com.worldventures.dreamtrips.core.api.uploadery.UploaderyInteractor
import com.worldventures.dreamtrips.social.common.base.BaseTestBody
import com.worldventures.dreamtrips.social.domain.mapping.ReverseBucketBodyConverter
import com.worldventures.dreamtrips.social.domain.mapping.ReverseBucketCoverBodyToUpdateBodyConverter
import com.worldventures.dreamtrips.social.domain.mapping.ReverseBucketPostBodyConverter
import com.worldventures.dreamtrips.social.domain.mapping.ReverseBucketPostBodyToUpdateBodyConverter
import com.worldventures.dreamtrips.social.domain.mapping.ReverseBucketUpdateBodyConverter
import com.worldventures.dreamtrips.social.domain.mapping.ShortProfilesConverter
import com.worldventures.dreamtrips.social.ui.bucketlist.model.BucketItem
import com.worldventures.dreamtrips.social.ui.bucketlist.model.converter.BucketItemSimpleConverter
import com.worldventures.dreamtrips.social.ui.bucketlist.model.converter.BucketItemSocializedConverter
import com.worldventures.dreamtrips.social.ui.bucketlist.model.converter.BucketPhotoBodyConverter
import com.worldventures.dreamtrips.social.ui.bucketlist.model.converter.BucketPhotoConverter
import com.worldventures.dreamtrips.social.ui.bucketlist.model.converter.BucketTypeConverter
import com.worldventures.dreamtrips.social.ui.bucketlist.service.BucketInteractor
import io.techery.janet.ActionService
import io.techery.janet.CommandActionService
import io.techery.janet.Janet
import io.techery.janet.http.test.MockHttpActionService
import io.techery.mappery.Mappery
import io.techery.mappery.MapperyContext

abstract class BaseBucketListCommandTestBody : BaseTestBody {

   protected lateinit var bucketInteractor: BucketInteractor

   @Suppress("LeakingThis")
   protected val bucketItem = mockBucketItem("1")

   open fun setup(httpService: MockHttpActionService = MockHttpActionService.Builder().build()) {

      val daggerCommandActionService = mockDaggerActionService()

      val janet = mockJanetBuilder(mutableListOf<ActionService>(httpService, daggerCommandActionService)).build()

      daggerCommandActionService.registerProvider(Janet::class.java) { janet }
      daggerCommandActionService.registerProvider(MapperyContext::class.java) { mockMappery() }

      val userSession: UserSession = mock()
      whenever(userSession.user()).thenReturn(User().apply { id = 1 })

      val sessionHolder: SessionHolder = mock()
      sessionHolder.apply {
         daggerCommandActionService.registerProvider(SessionHolder::class.java) { this }
         whenever(get()).thenReturn(Optional.of(userSession))
      }

      bucketInteractor = BucketInteractor(SessionActionPipeCreator(janet))
      daggerCommandActionService.registerProvider(BucketInteractor::class.java) { bucketInteractor }
      daggerCommandActionService.registerProvider(UploaderyInteractor::class.java) { UploaderyInteractor(janet) }
      registerProviders(daggerCommandActionService)
   }

   protected open fun registerProviders(daggerActionService: MockDaggerActionService) {
      //do nothing
   }

   protected open fun mockDaggerActionService() = CommandActionService().wrapCache().wrapDagger()

   protected open fun mockMappery() = Mappery.Builder().apply {
      constructConverters().forEach { map(it.sourceClass()).to(it.targetClass(), it) }
   }.build()

   protected open fun constructConverters(): List<Converter<Any, Any>> {
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

   @Suppress("UNCHECKED_CAST")
   fun castConverter(converter: Converter<*, *>): Converter<Any, Any> = converter as Converter<Any, Any>

   protected open fun mockHttpServiceForSuccess() = MockHttpActionService.Builder().build()

   protected open fun mockHttpServiceForError() = MockHttpActionService.Builder().build()

   protected open fun mockJanetBuilder(actionServices: List<ActionService>) = Janet.Builder().apply {
      actionServices.forEach { addService(it) }
   }

   protected open fun mockBucketItem(uid: String) = BucketItem().apply {
      this.uid = uid
      name = uid
   }
}
