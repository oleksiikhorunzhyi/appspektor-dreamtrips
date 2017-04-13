package com.worldventures.dreamtrips.social.background_uploading.spec

import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.spy
import com.techery.spares.session.SessionHolder
import com.worldventures.dreamtrips.BaseSpec
import com.worldventures.dreamtrips.common.janet.service.MockAnalyticsService
import com.worldventures.dreamtrips.core.janet.SessionActionPipeCreator
import com.worldventures.dreamtrips.core.utils.tracksystem.AnalyticsInteractor
import com.worldventures.dreamtrips.modules.background_uploading.model.PostCompoundOperationMutator
import com.worldventures.dreamtrips.modules.background_uploading.service.BackgroundUploadingInteractor
import com.worldventures.dreamtrips.modules.background_uploading.service.CompoundOperationsInteractor
import com.worldventures.dreamtrips.modules.feed.model.FeedEntityHolder
import com.worldventures.dreamtrips.modules.feed.model.PhotoFeedItem
import com.worldventures.dreamtrips.modules.feed.model.TextualPost
import com.worldventures.dreamtrips.modules.feed.service.PostsInteractor
import com.worldventures.dreamtrips.modules.trips.model.Location
import com.worldventures.dreamtrips.modules.tripsimages.model.Photo
import io.techery.janet.ActionService
import io.techery.janet.CommandActionService
import io.techery.janet.Janet
import io.techery.janet.command.test.Contract
import io.techery.janet.command.test.MockCommandActionService
import org.jetbrains.spek.api.DescribeBody
import org.mockito.Mockito
import org.powermock.api.mockito.PowerMockito.`when`
import rx.schedulers.Schedulers
import java.util.*

abstract class BaseUploadingInteractorSpec(spekBody: DescribeBody.() -> Unit) : BaseSpec(spekBody) {
   companion object {
      val mockCreatedPost: TextualPost = mock()
      val listOfMockPhotos: ArrayList<Photo> = arrayListOf(mockPhoto(), mockPhoto())

      lateinit var backgroundUploadingInteractor: BackgroundUploadingInteractor
      lateinit var compoundOperationsInteractor: CompoundOperationsInteractor

      fun initJanet(mockContracts: List<Contract> = successContract()) {
         val mockPhotoItems = listOf(mockItem(listOfMockPhotos[0]), mockItem(listOfMockPhotos[1]));
         Mockito.`when`(mockCreatedPost.attachments).thenAnswer { mockPhotoItems }

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
         daggerCommandActionService.registerProvider(BackgroundUploadingInteractor::class.java) { backgroundUploadingInteractor }
         daggerCommandActionService.registerProvider(CompoundOperationsInteractor::class.java) { compoundOperationsInteractor }
         daggerCommandActionService.registerProvider(AnalyticsInteractor::class.java) { analyticsInteractor }
         daggerCommandActionService.registerProvider(SessionHolder::class.java) { mockSessionHolder() }
         daggerCommandActionService.registerProvider(PostCompoundOperationMutator::class.java) { PostCompoundOperationMutator(mockSessionHolder()) }
      }

      fun mockPhoto(): Photo {
         val photo: Photo = mock()
         `when`(photo.location).thenReturn(Location())
         return photo
      }

      fun mockItem(photo: Photo): PhotoFeedItem {
         val feedItem: PhotoFeedItem = mock()
         `when`(feedItem.item).thenReturn(photo)
         `when`(feedItem.type).thenReturn(FeedEntityHolder.Type.PHOTO)
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