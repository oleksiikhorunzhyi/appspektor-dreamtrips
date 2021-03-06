package com.worldventures.dreamtrips.social.background_uploading.spec

import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.spy
import com.nhaarman.mockito_kotlin.whenever
import com.worldventures.core.janet.SessionActionPipeCreator
import com.worldventures.core.model.session.SessionHolder
import com.worldventures.core.service.analytics.AnalyticsInteractor
import com.worldventures.core.test.janet.MockAnalyticsService
import com.worldventures.dreamtrips.BaseSpec
import com.worldventures.core.model.Location
import com.worldventures.dreamtrips.social.ui.background_uploading.model.PostCompoundOperationMutator
import com.worldventures.dreamtrips.social.ui.background_uploading.service.BackgroundUploadingInteractor
import com.worldventures.dreamtrips.social.ui.background_uploading.service.CompoundOperationsInteractor
import com.worldventures.dreamtrips.social.ui.background_uploading.service.PingAssetStatusInteractor
import com.worldventures.dreamtrips.social.ui.feed.model.FeedEntityHolder
import com.worldventures.dreamtrips.social.ui.feed.model.PhotoFeedItem
import com.worldventures.dreamtrips.social.ui.feed.model.TextualPost
import com.worldventures.dreamtrips.social.ui.feed.service.PostsInteractor
import com.worldventures.dreamtrips.social.ui.tripsimages.model.Photo
import io.techery.janet.ActionService
import io.techery.janet.CommandActionService
import io.techery.janet.Janet
import io.techery.janet.command.test.Contract
import io.techery.janet.command.test.MockCommandActionService
import org.jetbrains.spek.api.dsl.SpecBody
import rx.schedulers.Schedulers
import java.util.ArrayList

abstract class BaseUploadingInteractorSpec(spekBody: SpecBody.() -> Unit) : BaseSpec(spekBody) {
   companion object {
      var mockCreatedPost: TextualPost = mock()
      val listOfMockPhotos: ArrayList<Photo> = arrayListOf(mockPhoto(), mockPhoto())

      lateinit var backgroundUploadingInteractor: BackgroundUploadingInteractor
      lateinit var compoundOperationsInteractor: CompoundOperationsInteractor

      fun initJanet(mockContracts: List<Contract> = successContract()) {
         val mockPhotoItems = listOf(mockItem(listOfMockPhotos[0]), mockItem(listOfMockPhotos[1]));
         whenever(mockCreatedPost.attachments).thenAnswer { mockPhotoItems }

         val daggerCommandActionService = CommandActionService().wrapDagger()

         val janet = Janet.Builder()
               .addService(mockActionService(daggerCommandActionService, mockContracts))
               .addService(MockAnalyticsService())
               .build()

         val sessionPipeCreator = SessionActionPipeCreator(janet)

         compoundOperationsInteractor = CompoundOperationsInteractor(sessionPipeCreator, Schedulers.immediate())
         backgroundUploadingInteractor = BackgroundUploadingInteractor(sessionPipeCreator)

         val analyticsInteractor = spy(AnalyticsInteractor(sessionPipeCreator))
         daggerCommandActionService.registerProvider(Janet::class.java) { janet }
         daggerCommandActionService.registerProvider(PostsInteractor::class.java) { PostsInteractor(SessionActionPipeCreator(janet)) }
         daggerCommandActionService.registerProvider(PingAssetStatusInteractor::class.java) { PingAssetStatusInteractor(SessionActionPipeCreator(janet)) }
         daggerCommandActionService.registerProvider(BackgroundUploadingInteractor::class.java) { backgroundUploadingInteractor }
         daggerCommandActionService.registerProvider(CompoundOperationsInteractor::class.java) { compoundOperationsInteractor }
         daggerCommandActionService.registerProvider(AnalyticsInteractor::class.java) { analyticsInteractor }
         daggerCommandActionService.registerProvider(SessionHolder::class.java) { mockSessionHolder() }
         daggerCommandActionService.registerProvider(PostCompoundOperationMutator::class.java) { PostCompoundOperationMutator(mockSessionHolder()) }
      }

      fun mockPhoto(): Photo {
         val photo = Photo()
         photo.location = Location()
         return photo
      }

      fun mockItem(photo: Photo): PhotoFeedItem {
         val feedItem: PhotoFeedItem = mock()
         whenever(feedItem.item).thenReturn(photo)
         whenever(feedItem.type).thenReturn(FeedEntityHolder.Type.PHOTO)
         return feedItem
      }

      fun mockActionService(service: ActionService, mockContracts: List<Contract>) = MockCommandActionService.Builder()
            .apply {
               actionService(service)
               for (contract in mockContracts) addContract(contract)
            }
            .build()
   }
}