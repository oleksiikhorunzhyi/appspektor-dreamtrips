package com.worldventures.dreamtrips.social.membership

import com.nhaarman.mockito_kotlin.*
import com.worldventures.dreamtrips.AssertUtil.assertActionSuccess
import com.worldventures.dreamtrips.BaseSpec
import com.worldventures.dreamtrips.api.podcasts.model.ImmutablePodcast
import com.worldventures.dreamtrips.core.janet.SessionActionPipeCreator
import com.worldventures.dreamtrips.core.repository.SnappyRepository
import com.worldventures.dreamtrips.modules.mapping.mapper.PodcastsMapper
import com.worldventures.dreamtrips.modules.membership.model.Podcast
import com.worldventures.dreamtrips.modules.membership.service.PodcastsInteractor
import com.worldventures.dreamtrips.modules.membership.service.command.GetPodcastsCommand
import com.worldventures.dreamtrips.modules.video.model.CachedEntity
import io.techery.janet.ActionState
import io.techery.janet.CommandActionService
import io.techery.janet.Janet
import io.techery.janet.http.test.MockHttpActionService
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import org.junit.Assert
import rx.observers.TestSubscriber
import java.util.*
import kotlin.test.assertEquals

class PodcastsInteractorSpec : BaseSpec({

   describe("Test getting podcasts action") {

      beforeEachTest { setup() }

      it("Check podcasts from API") {
         val subscriber = loadPodcastsWithRefresh()
         assertActionSuccess(subscriber) { checkIfPodcastsAreValid(it.result)}
      }

      it("Verify cached entity restored") {
         val subscriber = loadPodcastsWithRefresh()
         verify(mockDb, times(2)).getDownloadMediaEntity(anyString())
      }
   }
}) {
   companion object BaseCompanion {
      lateinit var mockDb: SnappyRepository

      lateinit var stubPodcasts: List<com.worldventures.dreamtrips.api.podcasts.model.Podcast>

      lateinit var podcastsInteractor: PodcastsInteractor

      fun setup() {
         mockDb = spy()
         val cachedEntity = mock<CachedEntity>()
         whenever(mockDb.getDownloadMediaEntity(anyString())).thenReturn(cachedEntity)
         stubPodcasts = makeStubPodcasts()

         val daggerCommandActionService = CommandActionService()
               .wrapCache()
               .wrapDagger()
         val janet = Janet.Builder()
               .addService(daggerCommandActionService)
               .addService(mockHttpService(stubPodcasts))
               .build()

         daggerCommandActionService.registerProvider(Janet::class.java) { janet }
         daggerCommandActionService.registerProvider(SnappyRepository::class.java) { mockDb }
         daggerCommandActionService.registerProvider(PodcastsMapper::class.java) { PodcastsMapper() }

         podcastsInteractor = PodcastsInteractor(SessionActionPipeCreator(janet))
      }

      fun mockHttpService(podcasts: List<com.worldventures.dreamtrips.api.podcasts.model.Podcast>): MockHttpActionService {
         return MockHttpActionService.Builder()
               .bind(MockHttpActionService.Response(200)
                     .body(podcasts)) { it.url.contains("/api/podcasts") }
               .build()
      }

      fun makeStubPodcasts(): List<com.worldventures.dreamtrips.api.podcasts.model.Podcast> {
         val podcasts = ArrayList<com.worldventures.dreamtrips.api.podcasts.model.Podcast>()
         for (i in 1..2) {
            podcasts.add(makeStubPodcast(i))
         }
         return podcasts
      }

      fun makeStubPodcast(i: Int): com.worldventures.dreamtrips.api.podcasts.model.Podcast {
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
         podcastsInteractor.podcastsActionPipe().createObservable(GetPodcastsCommand.refresh()).subscribe(testSub)
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
