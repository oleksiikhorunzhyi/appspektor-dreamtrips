package com.worldventures.dreamtrips.social.ui.tripsimages.presenter.ysbh

import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.verify
import com.worldventures.core.janet.SessionActionPipeCreator
import com.worldventures.dreamtrips.social.common.presenter.PresenterBaseSpec
import com.worldventures.dreamtrips.social.ui.tripsimages.model.YSBHPhoto
import com.worldventures.dreamtrips.social.ui.tripsimages.service.TripImagesInteractor
import com.worldventures.dreamtrips.social.ui.tripsimages.service.command.GetYSBHPhotosCommand
import io.techery.janet.CommandActionService
import io.techery.janet.Janet
import io.techery.janet.command.test.Contract
import io.techery.janet.command.test.MockCommandActionService
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class YSBHPresenterSpec : PresenterBaseSpec({

   describe("YouShouldBeHerePresenter") {

      it("should reload and refresh photos, last page is not reached") {
         val stubPhotos = stubPhotosForNotLastPage()
         val contract = Contract.of(GetYSBHPhotosCommand::class.java).result(stubPhotos)
         setup(contract)
         presenter.currentItems = ArrayList()

         presenter.subscribeToNewItems()
         presenter.reload()

         assert(presenter.currentItems.containsAll(stubPhotos))
         assertFalse(presenter.lastPageReached)
         verify(view).updatePhotos(any())
         verify(view).finishLoading()
      }

      it("should reload and refresh photos, last page is reached") {
         val stubPhotos = stubPhotosForLastPage()
         val contract = Contract.of(GetYSBHPhotosCommand::class.java).result(stubPhotos)
         setup(contract)
         presenter.currentItems = ArrayList()

         presenter.subscribeToNewItems()
         presenter.reload()

         assert(presenter.currentItems.containsAll(stubPhotos))
         assertTrue(presenter.lastPageReached)
         verify(view).updatePhotos(any())
         verify(view).finishLoading()
      }

      it("should load photos and refresh photos, last page is not reached") {
         val stubPhotosForSecondPage = stubPhotosForNotLastPage()
         val contract = Contract.of(GetYSBHPhotosCommand::class.java).result(stubPhotosForSecondPage)
         setup(contract)
         val existingPhotos = stubPhotosForNotLastPage()
         presenter.currentItems = ArrayList(existingPhotos)

         presenter.subscribeToNewItems()
         presenter.loadNext()

         assert(presenter.currentItems.containsAll(stubPhotosForSecondPage))
         assertEquals(presenter.currentItems.size, existingPhotos.size + stubPhotosForSecondPage.size)
         assertFalse(presenter.lastPageReached)
         verify(view).updatePhotos(any())
         verify(view).finishLoading()
      }

      it("should load photos and refresh photos, last page reached") {
         val stubPhotosForSecondPage = stubPhotosForLastPage()
         val contract = Contract.of(GetYSBHPhotosCommand::class.java).result(stubPhotosForSecondPage)
         setup(contract)
         val existingPhotos = stubPhotosForNotLastPage()
         presenter.currentItems = ArrayList(existingPhotos)

         presenter.subscribeToNewItems()
         presenter.loadNext()

         assert(presenter.currentItems.containsAll(stubPhotosForSecondPage))
         assertEquals(presenter.currentItems.size, existingPhotos.size + stubPhotosForSecondPage.size)
         assertTrue(presenter.lastPageReached)
         verify(view).updatePhotos(any())
         verify(view).finishLoading()
      }

      it("should open full screen") {
         val photos = ArrayList<YSBHPhoto>()
         val stubPhoto1 = stubPhoto(id = 11)
         photos.add(stubPhoto1)
         photos.add(stubPhoto(id = 22))
         presenter.currentItems = photos
         presenter.lastPageReached = true

         presenter.onItemClick(stubPhoto1)

         verify(view).openFullscreen(photos, true, 0)
      }
   }

}) {
   companion object {

      lateinit var presenter: YSBHPresenter
      lateinit var view: YSBHPresenter.View

      lateinit var tripImagesInteractor: TripImagesInteractor

      fun setup(contract: Contract? = null) {
         presenter = YSBHPresenter()
         view = mock()

         val service = MockCommandActionService.Builder().apply {
            actionService(CommandActionService())
            contract?.let { addContract(contract) }
         }.build()

         val janet = Janet.Builder().addService(service).build()
         val actionPipeCreator = SessionActionPipeCreator(janet)

         tripImagesInteractor = TripImagesInteractor(actionPipeCreator)

         val injector = prepareInjector().apply {
            registerProvider(TripImagesInteractor::class.java, { tripImagesInteractor })
         }
         injector.inject(presenter)
         presenter.takeView(view)
      }
   }
}

