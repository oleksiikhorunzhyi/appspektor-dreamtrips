package com.worldventures.dreamtrips.social.ui.tripsimages.presenter.ysbh

import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.verify
import com.worldventures.core.janet.SessionActionPipeCreator
import com.worldventures.dreamtrips.social.common.presenter.PresenterBaseSpec
import com.worldventures.dreamtrips.social.ui.tripsimages.model.YSBHPhoto
import com.worldventures.dreamtrips.social.ui.tripsimages.presenter.BaseImageViewPagerPresenter
import com.worldventures.dreamtrips.social.ui.tripsimages.service.TripImagesInteractor
import com.worldventures.dreamtrips.social.ui.tripsimages.service.command.GetYSBHPhotosCommand
import com.worldventures.dreamtrips.social.ui.tripsimages.view.args.YsbhPagerArgs
import io.techery.janet.CommandActionService
import io.techery.janet.Janet
import io.techery.janet.command.test.Contract
import io.techery.janet.command.test.MockCommandActionService
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class YSBHViewPagerPresenterSpec : PresenterBaseSpec ({

   describe("YsbhViewPagerPresenter") {

      it("should init fragments correctly") {
         val selectedPosition = 1
         val args = stubArgs(stubPhotos(), currentItemPosition = selectedPosition)
         setup(args, Contract.of(GetYSBHPhotosCommand::class.java).result(emptyList<YSBHPhoto>()))

         presenter.initItems()

         verify(view).setItems(any())
         verify(view).setSelectedPosition(selectedPosition)
      }

      it("should subscribe to new items and refresh view on success, last page is not reached") {
         val stubbedPhotos = stubPhotos(size = GetYSBHPhotosCommand.PER_PAGE)
         setup(stubArgs(stubPhotos()), Contract.of(GetYSBHPhotosCommand::class.java).result(stubbedPhotos))

         presenter.subscribeToNewItems()
         tripImagesInteractor.ysbhPhotosPipe.send(GetYSBHPhotosCommand.cachedCommand())

         assertFalse(presenter.lastPageReached)
         assertFalse(presenter.loading)
         assert(presenter.currentItems.containsAll(stubbedPhotos))
         assert(presenter.currentItems.size == stubbedPhotos.size)
      }

      it("should subscribe to new items and refresh view on success, last page is reached") {
         val stubbedPhotos = stubPhotos(size = GetYSBHPhotosCommand.PER_PAGE - 1)
         setup(stubArgs(stubPhotos()), Contract.of(GetYSBHPhotosCommand::class.java).result(stubbedPhotos))

         presenter.subscribeToNewItems()
         tripImagesInteractor.ysbhPhotosPipe.send(GetYSBHPhotosCommand.cachedCommand())

         assertTrue(presenter.lastPageReached)
         assertFalse(presenter.loading)
         assert(presenter.currentItems.containsAll(stubbedPhotos))
         assert(presenter.currentItems.size == stubbedPhotos.size)
      }

      it ("should load next page and refresh view, last page is not reached") {
         val stubbedPhotosPage1 = stubPhotos(size = GetYSBHPhotosCommand.PER_PAGE)
         val stubbedPhotosPage2 = stubPhotos(size = GetYSBHPhotosCommand.PER_PAGE)
         val stubbedPhotosPage1And2 = arrayListOf<YSBHPhoto>()
         stubbedPhotosPage1And2.addAll(stubbedPhotosPage1)
         stubbedPhotosPage1And2.addAll(stubbedPhotosPage2)
         setup(stubArgs(), Contract.of(GetYSBHPhotosCommand::class.java).result(stubbedPhotosPage2))

         presenter.currentItems = ArrayList(stubbedPhotosPage1)
         presenter.subscribeToNewItems()
         tripImagesInteractor.ysbhPhotosPipe.send(GetYSBHPhotosCommand.commandForPage(2))

         assertFalse(presenter.lastPageReached)
         assertFalse(presenter.loading)
         assert(presenter.currentItems.containsAll(stubbedPhotosPage1And2))
         assert(presenter.currentItems.size == stubbedPhotosPage1And2.size)
      }

      it ("should load next page and refresh view, last page is reached") {
         val stubbedPhotosPage1 = stubPhotos(size = GetYSBHPhotosCommand.PER_PAGE)
         val stubbedPhotosPage2 = stubPhotos(size = GetYSBHPhotosCommand.PER_PAGE - 1)
         val stubbedPhotosPage1And2 = arrayListOf<YSBHPhoto>()
         stubbedPhotosPage1And2.addAll(stubbedPhotosPage1)
         stubbedPhotosPage1And2.addAll(stubbedPhotosPage2)
         setup(stubArgs(), Contract.of(GetYSBHPhotosCommand::class.java).result(stubbedPhotosPage2))

         presenter.currentItems = ArrayList(stubbedPhotosPage1)
         presenter.subscribeToNewItems()
         tripImagesInteractor.ysbhPhotosPipe.send(GetYSBHPhotosCommand.commandForPage(2))

         assertTrue(presenter.lastPageReached)
         assertFalse(presenter.loading)
         assert(presenter.currentItems.containsAll(stubbedPhotosPage1And2))
         assert(presenter.currentItems.size == stubbedPhotosPage1And2.size)
      }
   }
}) {
   companion object {

      lateinit var presenter: YSBHViewPagerPresenter
      lateinit var view: BaseImageViewPagerPresenter.View

      lateinit var tripImagesInteractor: TripImagesInteractor

      fun setup(args: YsbhPagerArgs, contract: Contract? = null) {
         presenter = YSBHViewPagerPresenter(args)

         val service = MockCommandActionService.Builder().apply {
            actionService(CommandActionService())
            contract?.let { addContract(contract) }
         }.build()
         val janet = Janet.Builder().addService(service).build()
         val sessionPipeCreator = SessionActionPipeCreator(janet)

         tripImagesInteractor = TripImagesInteractor(sessionPipeCreator)

         prepareInjector().apply {
            registerProvider(TripImagesInteractor::class.java, { tripImagesInteractor })
            inject(presenter)
         }

         view = mock()
         presenter.takeView(view)
      }

      fun stubArgs(list: MutableList<YSBHPhoto> = mutableListOf(),
                   lastPageReached: Boolean = false,
                   currentItemPosition: Int = 0) : YsbhPagerArgs {
         return YsbhPagerArgs(list, lastPageReached, currentItemPosition)
      }
   }
}
