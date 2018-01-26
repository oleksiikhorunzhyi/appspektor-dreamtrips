package com.worldventures.dreamtrips.social.ui.video.presenter

import android.content.pm.PackageManager
import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.anyOrNull
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.times
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.whenever
import com.worldventures.core.janet.SessionActionPipeCreator
import com.worldventures.core.model.CachedModel
import com.worldventures.core.modules.video.service.MemberVideosInteractor
import com.worldventures.core.modules.video.utils.CachedModelHelper
import com.worldventures.core.service.CachedEntityDelegate
import com.worldventures.core.service.CachedEntityInteractor
import com.worldventures.core.test.common.Injector
import com.worldventures.core.ui.util.permission.PermissionConstants
import com.worldventures.core.ui.util.permission.PermissionDispatcher
import com.worldventures.core.ui.util.permission.PermissionsResult
import com.worldventures.dreamtrips.social.common.presenter.PresenterBaseSpec
import com.worldventures.dreamtrips.social.ui.reptools.delegate.LocaleVideoDelegate
import com.worldventures.dreamtrips.social.ui.video.service.VideoHelperInteractor
import com.worldventures.dreamtrips.social.ui.video.service.command.DetermineHeadersCommand
import io.techery.janet.CommandActionService
import io.techery.janet.Janet
import io.techery.janet.command.test.Contract
import io.techery.janet.command.test.MockCommandActionService
import org.jetbrains.spek.api.dsl.SpecBody
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import org.mockito.ArgumentMatchers
import rx.Observable

abstract class VideoBasePresenterSpec(testSuite: VideoBaseTestSuite<VideoBaseComponents<out VideoBasePresenter<out VideoBasePresenter.View>,
      out VideoBasePresenter.View>>) : PresenterBaseSpec(testSuite) {

   abstract class VideoBaseTestSuite<out C : VideoBaseComponents<out VideoBasePresenter<out VideoBasePresenter.View>,
         out VideoBasePresenter.View>>(components: C) : TestSuite<C>(components) {

      override fun specs(): SpecBody.() -> Unit = {

         with(components) {
            describe("Download video flow") {
               it("Shouldn't download video without permission") {
                  init(permissionGranted = false)
                  val video = stubVideo()

                  presenter.downloadVideoRequired(video)

                  verify(permissionDispatcher).requestPermission(PermissionConstants.WRITE_EXTERNAL_STORAGE, false)
                  verify(cachedEntityDelegate, times(0)).startCaching(any(), any())
               }

               it("Should download video with permission") {
                  init()
                  val video = stubVideo()

                  presenter.downloadVideoRequired(video)

                  verify(permissionDispatcher).requestPermission(PermissionConstants.WRITE_EXTERNAL_STORAGE, false)
                  verify(cachedEntityDelegate).startCaching(any(), any())
               }
            }

            it("Delete cached video: should remove cache") {
               init()
               presenter.deleteAccepted(cachedModel)

               verify(cachedEntityDelegate).deleteCache(cachedModel, videoPath)
            }
         }
      }
   }

   abstract class VideoBaseComponents<P : VideoBasePresenter<V>, V : VideoBasePresenter.View> : TestComponents<P, V>() {

      val videoPath = "testVideoPath"
      val cachedModel: CachedModel = mock()

      lateinit var permissionDispatcher: PermissionDispatcher
      lateinit var cachedEntityDelegate: CachedEntityDelegate
      lateinit var localeVideoDelegate: LocaleVideoDelegate

      fun init(contracts: List<Contract> = listOf(mockVideosCommand(), mockLocalesCommand()), permissionGranted: Boolean = true) {

         val serviceBuilder = mockActionService()
         contracts.forEach { serviceBuilder.addContract(it) }

         val janet = Janet.Builder().addService(serviceBuilder.build()).build()
         val sessionPipeCreator = SessionActionPipeCreator(janet)
         val cachedEntityInteractor = CachedEntityInteractor(sessionPipeCreator)
         val headerInteractor = VideoHelperInteractor(sessionPipeCreator)
         val memberVideosInteractor = MemberVideosInteractor(sessionPipeCreator)

         permissionDispatcher = mock()
         val grantResult = if (permissionGranted) PackageManager.PERMISSION_GRANTED else PackageManager.PERMISSION_DENIED
         val permissionResult = PermissionsResult(-1, PermissionConstants.WRITE_EXTERNAL_STORAGE, grantResult)
         whenever(permissionDispatcher.requestPermission(any(), ArgumentMatchers.anyBoolean())).thenReturn(Observable.just(permissionResult))

         localeVideoDelegate = mock()
         cachedEntityDelegate = mock()
         val cachedModelHelper: CachedModelHelper = mock()
         whenever(cachedModelHelper.isCached(any())).thenReturn(false)
         whenever(cachedModelHelper.getFilePath(any())).thenReturn(videoPath)
         whenever(localeVideoDelegate.fetchVideoLocaleList()).thenReturn(ArrayList(stubLocaleList()))
         whenever(localeVideoDelegate.fetchLocaleAndLanguage(any(), anyOrNull())).thenReturn(Pair(stubVideoLocale(), stubVideoLanguage()))
         whenever(localeVideoDelegate.saveVideoLocaleAndLanguage(any(), any())).then { }
         whenever(cachedModel.url).thenReturn(videoPath)

         val injector = prepareInjector().apply {
            registerProvider(CachedEntityInteractor::class.java, { cachedEntityInteractor })
            registerProvider(MemberVideosInteractor::class.java, { memberVideosInteractor })
            registerProvider(VideoHelperInteractor::class.java, { headerInteractor })
            registerProvider(CachedModelHelper::class.java, { cachedModelHelper })
            registerProvider(PermissionDispatcher::class.java, { permissionDispatcher })
            registerProvider(CachedEntityDelegate::class.java, { cachedEntityDelegate })
            registerProvider(LocaleVideoDelegate::class.java, { localeVideoDelegate })
         }

         onInit(injector, sessionPipeCreator)
      }

      protected open fun mockActionService(): MockCommandActionService.Builder {
         return MockCommandActionService.Builder().apply {
            actionService(CommandActionService())
            addContract(Contract.of(DetermineHeadersCommand::class.java).result(ArrayList<Any>()))
         }
      }

      protected abstract fun onInit(injector: Injector, pipeCreator: SessionActionPipeCreator)
   }
}
