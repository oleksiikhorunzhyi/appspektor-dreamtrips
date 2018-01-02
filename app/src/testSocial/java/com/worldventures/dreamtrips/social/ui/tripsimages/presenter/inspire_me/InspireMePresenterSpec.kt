package com.worldventures.dreamtrips.social.ui.tripsimages.presenter.inspire_me

import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.verify
import com.worldventures.core.janet.SessionActionPipeCreator
import com.worldventures.dreamtrips.social.common.presenter.PresenterBaseSpec
import com.worldventures.dreamtrips.social.ui.tripsimages.model.Inspiration
import com.worldventures.dreamtrips.social.ui.tripsimages.service.TripImagesInteractor
import com.worldventures.dreamtrips.social.ui.tripsimages.service.command.GetInspireMePhotosCommand
import io.techery.janet.CommandActionService
import io.techery.janet.Janet
import io.techery.janet.command.test.Contract
import io.techery.janet.command.test.MockCommandActionService
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class InspireMePresenterSpec : PresenterBaseSpec({

   describe("InspireMePresenter") {
      it("should have non zero seed") {
         setup()

         presenter.onViewTaken()

         assert(presenter.randomSeed != 0.0)
      }

      it("should change random seed when refreshing images") {
         setup()
         val randomSeed = 0.3156
         presenter.randomSeed = randomSeed

         presenter.reload()

         assert(presenter.randomSeed != randomSeed)
      }

      it("should reload and refresh photos, last page is not reached") {
         val stubPhotos = stubPhotosForNotLastPage()
         val contract = Contract.of(GetInspireMePhotosCommand::class.java).result(stubPhotos)
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
         val contract = Contract.of(GetInspireMePhotosCommand::class.java).result(stubPhotos)
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
         val contract = Contract.of(GetInspireMePhotosCommand::class.java).result(stubPhotosForSecondPage)
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
         val contract = Contract.of(GetInspireMePhotosCommand::class.java).result(stubPhotosForSecondPage)
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
         val photos = ArrayList<Inspiration>()
         val stubPhoto1 = stubPhoto(id = 11)
         photos.add(stubPhoto1)
         photos.add(stubPhoto(id = 22))
         presenter.currentItems = photos
         presenter.lastPageReached = true
         val randomSeed = 0.111
         presenter.randomSeed = randomSeed

         presenter.onItemClick(stubPhoto1)

         verify(view).openFullscreen(photos,  randomSeed,true, 0)
      }
   }

}) {
   companion object {

      lateinit var presenter: InspireMePresenter
      lateinit var view: InspireMePresenter.View

      lateinit var tripImagesInteractor: TripImagesInteractor

      fun setup(contract: Contract? = null) {
         val service = MockCommandActionService.Builder().apply {
            actionService(CommandActionService())
            if (contract != null) addContract(contract)
         }.build()

         val janet = Janet.Builder().addService(service).build()
         val pipeCreator = SessionActionPipeCreator(janet)

         tripImagesInteractor = TripImagesInteractor(pipeCreator)

         presenter = InspireMePresenter()
         view = mock()

         prepareInjector().apply {
            registerProvider(TripImagesInteractor::class.java, { tripImagesInteractor })
            inject(presenter)
         }

         presenter.takeView(view)
      }
   }
}