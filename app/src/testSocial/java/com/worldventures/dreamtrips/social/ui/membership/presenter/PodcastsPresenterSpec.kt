package com.worldventures.dreamtrips.social.ui.membership.presenter

import android.content.pm.PackageManager
import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.anyOrNull
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.spy
import com.nhaarman.mockito_kotlin.times
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.whenever
import com.worldventures.core.janet.SessionActionPipeCreator
import com.worldventures.core.model.CachedModel
import com.worldventures.core.modules.video.utils.CachedModelHelper
import com.worldventures.core.service.CachedEntityDelegate
import com.worldventures.core.service.CachedEntityInteractor
import com.worldventures.core.ui.util.permission.PermissionConstants
import com.worldventures.core.ui.util.permission.PermissionDispatcher
import com.worldventures.core.ui.util.permission.PermissionsResult
import com.worldventures.dreamtrips.social.common.presenter.PresenterBaseSpec
import com.worldventures.dreamtrips.social.ui.membership.model.Podcast
import com.worldventures.dreamtrips.social.ui.membership.service.PodcastsInteractor
import com.worldventures.dreamtrips.social.ui.membership.service.command.GetPodcastsCommand
import io.techery.janet.CommandActionService
import io.techery.janet.Janet
import io.techery.janet.command.test.BaseContract
import io.techery.janet.command.test.Contract
import io.techery.janet.command.test.MockCommandActionService
import org.jetbrains.spek.api.dsl.SpecBody
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import org.mockito.ArgumentMatchers.anyBoolean
import rx.Observable

class PodcastsPresenterSpec : PresenterBaseSpec(PodcastTestSuite()) {

   class PodcastTestSuite : TestSuite<PodcastsComponents>(PodcastsComponents()) {

      override fun specs(): SpecBody.() -> Unit = {

         with(components) {
            describe("Podcasts Presenter") {

               describe("load podcasts") {

                  it("successfully") {
                     init()
                     linkPresenterAndView()

                     val view = view
                     verify(view).finishLoading(any())
                     verify(view).setItems(any())
                     verify(view, times(0)).informUser(anyOrNull<String>())
                  }

                  it("not successfully") {
                     init(fetchPodcastsContract = podcastNotSuccessContract())
                     linkPresenterAndView()

                     val view = view
                     verify(view).finishLoading(any())
                     verify(view, times(0)).setItems(any())
                     verify(view).informUser(anyOrNull<String>())
                  }
               }

               describe("download podcast") {

                  it("with permission") {
                     init()
                     val podcast = testPodcast()

                     presenter.onDownloadPodcastRequired(podcast)

                     verify(permissionDispatcher).requestPermission(any(), anyBoolean())
                     verify(cachedEntityDelegate).startCaching(any(), any())
                  }

                  it("without permission") {
                     init(permissionGranted = false)
                     val podcast = testPodcast()

                     presenter.onDownloadPodcastRequired(podcast)

                     verify(permissionDispatcher).requestPermission(any(), anyBoolean())
                     verify(cachedEntityDelegate, times(0)).startCaching(any(), any())
                  }
               }

               describe("manage podcast") {

                  beforeEachTest {
                     init()
                     linkPresenterAndView()
                  }

                  val podcast = testPodcast()

                  it("cancel download podcast") {
                     presenter.onCancelPodcastRequired(podcast)
                     presenter.onCancelPodcastAccepted(podcast)

                     verify(view).onCancelDialog(podcast)
                     verify(cachedEntityDelegate).cancelCaching(any(), any())
                  }

                  it("delete podcast") {
                     presenter.onDeletePodcastRequired(podcast)
                     presenter.onDeletePodcastAccepted(podcast)

                     verify(view).showDeleteDialog(podcast)
                     verify(cachedEntityDelegate).deleteCache(any(), any())
                  }

                  it("play podcast") {
                     presenter.play(podcast)

                     verify(activityRouter).openPodcastPlayer(any(), anyOrNull())
                  }
               }
            }
         }
      }
   }

   class PodcastsComponents : TestComponents<PodcastsPresenter<PodcastsPresenter.View>, PodcastsPresenter.View>() {

      lateinit var cachedEntityDelegate: CachedEntityDelegate
      lateinit var permissionDispatcher: PermissionDispatcher

      fun init(fetchPodcastsContract: Contract = podcastSuccessContract(), permissionGranted: Boolean = true) {
         view = spy()
         presenter = PodcastsPresenter()
         cachedEntityDelegate = mock()

         val service = MockCommandActionService.Builder().apply {
            actionService(CommandActionService())
                  .addContract(fetchPodcastsContract)
         }.build()
         val janet = Janet.Builder().addService(service).build()
         val sessionPipeCreator = SessionActionPipeCreator(janet)
         val podcastsInteractor = PodcastsInteractor(sessionPipeCreator)
         val cachedEntityInteractor = CachedEntityInteractor(sessionPipeCreator)

         val cachedModelHelper = mock<CachedModelHelper>()
         whenever(cachedModelHelper.isCachedPodcast(any())).thenReturn(true)
         whenever(cachedModelHelper.getPodcastPath(any())).thenReturn("testPath")

         permissionDispatcher = mock()
         val grantResult = if (permissionGranted) PackageManager.PERMISSION_GRANTED else PackageManager.PERMISSION_DENIED
         val permissionResult = PermissionsResult(-1, PermissionConstants.WRITE_EXTERNAL_STORAGE, grantResult)
         whenever(permissionDispatcher.requestPermission(any(), anyBoolean())).thenReturn(Observable.just(permissionResult))

         whenever(context.getString(any())).thenReturn("test")

         prepareInjector().apply {
            registerProvider(PodcastsInteractor::class.java, { podcastsInteractor })
            registerProvider(CachedEntityInteractor::class.java, { cachedEntityInteractor })
            registerProvider(CachedEntityDelegate::class.java, { cachedEntityDelegate })
            registerProvider(CachedModelHelper::class.java, { cachedModelHelper })
            registerProvider(PermissionDispatcher::class.java, { permissionDispatcher })
            inject(presenter)
         }
      }

      fun testPodcast(): Podcast {
         val cachedModel = CachedModel("testUrl", "testId", "testName")
         val podcast = Podcast()
         podcast.cachedModel = cachedModel
         return podcast
      }

      fun podcastNotSuccessContract() = BaseContract.of(GetPodcastsCommand::class.java).exception(RuntimeException())

      private fun podcastSuccessContract() = BaseContract.of(GetPodcastsCommand::class.java).result(listOf(testPodcast()))
   }
}
