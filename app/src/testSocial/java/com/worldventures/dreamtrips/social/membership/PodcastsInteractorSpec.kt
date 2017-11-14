package com.worldventures.dreamtrips.social.membership

import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.spy
import com.nhaarman.mockito_kotlin.times
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.whenever
import com.worldventures.core.janet.SessionActionPipeCreator
import com.worldventures.core.model.CachedModel
import com.worldventures.core.modules.video.service.storage.MediaModelStorage
import com.worldventures.core.test.AssertUtil.assertActionSuccess
import com.worldventures.dreamtrips.BaseSpec
import com.worldventures.dreamtrips.api.podcasts.model.ImmutablePodcast
import com.worldventures.dreamtrips.social.domain.mapping.PodcastsMapper
import com.worldventures.dreamtrips.social.ui.membership.model.Podcast
import com.worldventures.dreamtrips.social.ui.membership.service.PodcastsInteractor
import com.worldventures.dreamtrips.social.ui.membership.service.command.GetPodcastsCommand
import io.techery.janet.ActionState
import io.techery.janet.CommandActionService
import io.techery.janet.Janet
import io.techery.janet.http.test.MockHttpActionService
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import org.junit.Assert
import rx.observers.TestSubscriber
import java.util.ArrayList
import java.util.Date
import kotlin.test.assertEquals

typealias ApiPodcast = com.worldventures.dreamtrips.api.podcasts.model.Podcast

class PodcastsInteractorSpec : BaseSpec({

   describe("Test getting podcasts action") {

      beforeEachTest { setup() }

      it("Check podcasts from API") {
         val subscriber = loadPodcastsWithRefresh()
         assertActionSuccess(subscriber) { checkIfPodcastsAreValid(it.result)}
      }

      it("Verify cached entity restored") {
         val subscriber = loadPodcastsWithRefresh()
         verify(mockDb, times(2)).getDownloadMediaModel(anyString())
      }
   }
}) {
   companion object BaseCompanion {
      lateinit var mockDb: MediaModelStorage

      lateinit var stubPodcasts: List<ApiPodcast>

      lateinit var podcastsInteractor: PodcastsInteractor

      fun setup() {
         mockDb = spy()
         val cachedEntity = mock<CachedModel>()
         whenever(mockDb.getDownloadMediaModel(anyString())).thenReturn(cachedEntity)
         stubPodcasts = makeStubPodcasts()

         val daggerCommandActionService = CommandActionService()
               .wrapCache()
               .wrapDagger()
         val janet = Janet.Builder()
               .addService(daggerCommandActionService)
               .addService(mockHttpService(stubPodcasts))
               .build()

         daggerCommandActionService.registerProvider(Janet::class.java) { janet }
         daggerCommandActionService.registerProvider(MediaModelStorage::class.java) { mockDb }
         daggerCommandActionService.registerProvider(PodcastsMapper::class.java) { PodcastsMapper() }

         podcastsInteractor = PodcastsInteractor(SessionActionPipeCreator(janet))
      }

      fun mockHttpService(podcasts: List<ApiPodcast>): MockHttpActionService {
         return MockHttpActionService.Builder()
               .bind(MockHttpActionService.Response(200)
                     .body(podcasts)) { it.url.contains("/api/podcasts") }
               .build()
      }

      fun makeStubPodcasts(): List<ApiPodcast> {
         val podcasts = ArrayList<ApiPodcast>()
         for (i in 1..2) {
            podcasts.add(makeStubPodcast(i))
         }
         return podcasts
      }

      fun makeStubPodcast(i: Int): ApiPodcast {
         return ImmutablePodcast.builder()
               .title("Title $i")
               .description("Description $i")
               .speaker("Speaker $i")
               .category("Category $i")
               .date(Date())
               .audioURL("http://audiourl.com/$i.mp3")
               .imageURL("http://imageurl.com/$i.jpg")
               .duration(i)
               .build()
      }

      fun loadPodcastsWithRefresh(): TestSubscriber<ActionState<GetPodcastsCommand>> {
         val testSub = TestSubscriber<ActionState<GetPodcastsCommand>>()
         podcastsInteractor.podcastsActionPipe.createObservable(GetPodcastsCommand(true)).subscribe(testSub)
         return testSub
      }

      fun checkIfPodcastsAreValid(responsePodcasts: List<Podcast>): Boolean {
         Assert.assertTrue(stubPodcasts.size == responsePodcasts.size)
         for (i in 0..stubPodcasts.size - 1) {
            val apiPodcast = stubPodcasts[i]
            val mappedPodcast = responsePodcasts[i]
            assertEquals(apiPodcast.title(), mappedPodcast.title)
            assertEquals(apiPodcast.description(), mappedPodcast.description)
            assertEquals(apiPodcast.speaker(), mappedPodcast.speaker)
            assertEquals(apiPodcast.audioURL(), mappedPodcast.fileUrl)
            assertEquals(apiPodcast.imageURL(), mappedPodcast.imageUrl)
            assertEquals(apiPodcast.date(), mappedPodcast.date)
            assertEquals(apiPodcast.duration(), mappedPodcast.duration.toInt())
         }
         return true
      }
   }
}
