package com.worldventures.dreamtrips.social.ui.video.presenter

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
import com.worldventures.core.modules.video.service.MemberVideosInteractor
import com.worldventures.core.modules.video.utils.CachedModelHelper
import com.worldventures.core.service.CachedEntityDelegate
import com.worldventures.core.service.CachedEntityInteractor
import com.worldventures.core.ui.util.permission.PermissionConstants
import com.worldventures.core.ui.util.permission.PermissionDispatcher
import com.worldventures.core.ui.util.permission.PermissionsResult
import com.worldventures.dreamtrips.social.common.presenter.PresenterBaseSpec
import com.worldventures.dreamtrips.social.ui.video.service.VideoHelperInteractor
import com.worldventures.dreamtrips.social.ui.video.service.command.DetermineHeadersCommand
import io.techery.janet.CommandActionService
import io.techery.janet.Janet
import io.techery.janet.command.test.Contract
import io.techery.janet.command.test.MockCommandActionService
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import org.mockito.ArgumentMatchers
import rx.Observable

class PresentationVideosPresenterSpec : PresenterBaseSpec({
   describe("Presentation videos presenter") {

      it("Take view: should listen caching statuses of cached models") {
         setup()
         presenter.takeView(view)

         verify(presenter).subscribeToVideosPipe()
         verify(presenter).subscribeToCachingStatusUpdates()
      }

      it("On Reload: should update items with headers") {
         setup()
         presenter.takeView(view)
         presenter.reload()

         verify(presenter).fetchHeaders(any())
         verify(view).setItems(any())
      }

      it("Loading videos flow successfully: should update items on view and fetch header") {
         setup()
         presenter.takeView(view)
         presenter.onResume()

         verify(presenter).fetchHeaders(any())
         verify(view).setItems(any())
      }

      it("Loading videos flow not successfully: shouldn't update items on view and fetch header") {
         setup(mockVideosCommand(false))
         presenter.takeView(view)
         presenter.onResume()

         verify(presenter, times(0)).fetchHeaders(any())
         verify(view, times(0)).setItems(any())
      }

      describe("Download video flow") {
         it("Shouldn't download video without permission") {
            setup(permissionGranted = false)
            val video = stubVideo()

            presenter.downloadVideoRequired(video)

            verify(permissionDispatcher).requestPermission(PermissionConstants.WRITE_EXTERNAL_STORAGE, false)
            verify(cachedEntityDelegate, times(0)).startCaching(any(), any())
         }

         it("Should download video with permission") {
            setup()
            val video = stubVideo()

            presenter.downloadVideoRequired(video)

            verify(permissionDispatcher).requestPermission(PermissionConstants.WRITE_EXTERNAL_STORAGE, false)
            verify(cachedEntityDelegate).startCaching(any(), any())
         }
      }

      it("Play video: should open video player") {
         setup()

         presenter.onPlayVideo(stubVideo())
         verify(activityRouter).openPlayerActivity(anyOrNull(), any(), any(), any())
      }

      it("Delete cached video: should remove cache") {
         setup()
         presenter.deleteAccepted(cachedModel)

         verify(cachedEntityDelegate).deleteCache(cachedModel, videoPath)
      }
   }

}) {

   companion object {
      val videoPath = "testVideoPath"
      val cachedModel: CachedModel = mock()

      lateinit var permissionDispatcher: PermissionDispatcher
      lateinit var cachedEntityDelegate: CachedEntityDelegate

      lateinit var presenter: PresentationVideosPresenter
      lateinit var view: PresentationVideosPresenter.View

      fun setup(contract: Contract? = null, permissionGranted: Boolean = true) {
         presenter = spy(PresentationVideosPresenter())
         view = mock()

         val service = MockCommandActionService.Builder().apply {
            actionService(CommandActionService())
            addContract(Contract.of(DetermineHeadersCommand::class.java).result(ArrayList<Any>()))
         }.addContract(contract ?: mockVideosCommand()).build()

         val janet = Janet.Builder().addService(service).build()
         val sessionPipeCreator = SessionActionPipeCreator(janet)
         val cachedEntityInteractor = CachedEntityInteractor(sessionPipeCreator)
         val headerInteractor = VideoHelperInteractor(sessionPipeCreator)
         val memberVideosInteractor = MemberVideosInteractor(sessionPipeCreator)

         permissionDispatcher = mock()
         val grantResult = if (permissionGranted) PackageManager.PERMISSION_GRANTED else PackageManager.PERMISSION_DENIED
         val permissionResult = PermissionsResult(-1, PermissionConstants.WRITE_EXTERNAL_STORAGE, grantResult)
         whenever(permissionDispatcher.requestPermission(any(), ArgumentMatchers.anyBoolean())).thenReturn(Observable.just(permissionResult))

         cachedEntityDelegate = mock()
         val cachedModelHelper: CachedModelHelper = mock()
         whenever(cachedModelHelper.isCached(any())).thenReturn(false)
         whenever(cachedModelHelper.getFilePath(any())).thenReturn(videoPath)
         whenever(cachedModel.url).thenReturn(videoPath)

         prepareInjector().apply {
            registerProvider(CachedEntityInteractor::class.java, { cachedEntityInteractor })
            registerProvider(MemberVideosInteractor::class.java, { memberVideosInteractor })
            registerProvider(VideoHelperInteractor::class.java, { headerInteractor })
            registerProvider(CachedModelHelper::class.java, { cachedModelHelper })
            registerProvider(PermissionDispatcher::class.java, { permissionDispatcher })
            registerProvider(CachedEntityDelegate::class.java, { cachedEntityDelegate })

            inject(presenter)
         }
      }
   }
}