package com.worldventures.dreamtrips.social.ui.tripsimages.presenter.inspire_me

import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.verify
import com.worldventures.core.janet.SessionActionPipeCreator
import com.worldventures.dreamtrips.social.common.presenter.PresenterBaseSpec
import com.worldventures.dreamtrips.social.ui.tripsimages.model.Inspiration
import com.worldventures.dreamtrips.social.ui.tripsimages.presenter.BaseImageViewPagerPresenter
import com.worldventures.dreamtrips.social.ui.tripsimages.service.TripImagesInteractor
import com.worldventures.dreamtrips.social.ui.tripsimages.service.command.GetInspireMePhotosCommand
import com.worldventures.dreamtrips.social.ui.tripsimages.view.args.InspireMeViewPagerArgs
import io.techery.janet.CommandActionService
import io.techery.janet.Janet
import io.techery.janet.command.test.Contract
import io.techery.janet.command.test.MockCommandActionService
import org.jetbrains.spek.api.dsl.SpecBody
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class InspireMeViewPagerPresenterSpec : PresenterBaseSpec(InspireMeViewPagerTestSuite()) {

   class InspireMeViewPagerTestSuite : TestSuite<InspireMeViewPagerComponents>(InspireMeViewPagerComponents()) {

      override fun specs(): SpecBody.() -> Unit = {

         with(components) {
            describe("InspireMeViewPagerPresenter") {

               it("should init fragments correctly") {
                  val selectedPosition = 1
                  val args = stubArgs(stubPhotos(), currentItemPosition = selectedPosition)
                  val contract = Contract.of(GetInspireMePhotosCommand::class.java).result(emptyList<Inspiration>())
                  init(args, contract)
                  linkPresenterAndView()

                  presenter.initItems()

                  verify(view).setItems(any())
                  verify(view).setSelectedPosition(selectedPosition)
               }

               it("should subscribe to new items and refresh view on success, last page is not reached") {
                  val stubbedPhotos = stubPhotos(size = GetInspireMePhotosCommand.PER_PAGE)
                  val contract = Contract.of(GetInspireMePhotosCommand::class.java).result(stubbedPhotos)
                  init(stubArgs(stubPhotos()), contract)
                  linkPresenterAndView()

                  val presenter = presenter
                  presenter.subscribeToNewItems()
                  tripImagesInteractor.inspireMePhotosPipe.send(
                        GetInspireMePhotosCommand.forPage(RANDOM_SEED, 1))

                  assertFalse(presenter.lastPageReached)
                  assertFalse(presenter.loading)
                  assert(presenter.currentItems.containsAll(stubbedPhotos))
                  assert(presenter.currentItems.size == stubbedPhotos.size)
               }

               it("should subscribe to new items and refresh view on success, last page is reached") {
                  val stubbedPhotos = stubPhotos(size = GetInspireMePhotosCommand.PER_PAGE - 1)
                  val contract = Contract.of(GetInspireMePhotosCommand::class.java).result(stubbedPhotos)
                  init(stubArgs(stubPhotos()), contract)
                  linkPresenterAndView()

                  val presenter = presenter
                  presenter.subscribeToNewItems()
                  tripImagesInteractor.inspireMePhotosPipe.send(
                        GetInspireMePhotosCommand.forPage(RANDOM_SEED, 1))

                  assertTrue(presenter.lastPageReached)
                  assertFalse(presenter.loading)
                  assert(presenter.currentItems.containsAll(stubbedPhotos))
                  assert(presenter.currentItems.size == stubbedPhotos.size)
               }

               it("should load next page and refresh view, last page is not reached") {
                  val stubbedPhotosPage1 = stubPhotos(size = GetInspireMePhotosCommand.PER_PAGE)
                  val stubbedPhotosPage2 = stubPhotos(size = GetInspireMePhotosCommand.PER_PAGE)
                  val stubbedPhotosPage1And2 = arrayListOf<Inspiration>()
                  stubbedPhotosPage1And2.addAll(stubbedPhotosPage1)
                  stubbedPhotosPage1And2.addAll(stubbedPhotosPage2)
                  val contract = Contract.of(GetInspireMePhotosCommand::class.java).result(stubbedPhotosPage2)
                  init(stubArgs(), contract)
                  linkPresenterAndView()

                  val presenter = presenter
                  presenter.currentItems = ArrayList(stubbedPhotosPage1)
                  presenter.subscribeToNewItems()
                  tripImagesInteractor.inspireMePhotosPipe.send(GetInspireMePhotosCommand.forPage(RANDOM_SEED, 2))

                  assertFalse(presenter.lastPageReached)
                  assertFalse(presenter.loading)
                  assert(presenter.currentItems.containsAll(stubbedPhotosPage1And2))
                  assert(presenter.currentItems.size == stubbedPhotosPage1And2.size)
               }

               it("should load next page and refresh view, last page is reached") {
                  val stubbedPhotosPage1 = stubPhotos(size = GetInspireMePhotosCommand.PER_PAGE)
                  val stubbedPhotosPage2 = stubPhotos(size = GetInspireMePhotosCommand.PER_PAGE - 1)
                  val stubbedPhotosPage1And2 = arrayListOf<Inspiration>()
                  stubbedPhotosPage1And2.addAll(stubbedPhotosPage1)
                  stubbedPhotosPage1And2.addAll(stubbedPhotosPage2)
                  val contract = Contract.of(GetInspireMePhotosCommand::class.java).result(stubbedPhotosPage2)
                  init(stubArgs(), contract)
                  linkPresenterAndView()

                  val presenter = presenter
                  presenter.currentItems = ArrayList(stubbedPhotosPage1)
                  presenter.subscribeToNewItems()
                  tripImagesInteractor.inspireMePhotosPipe.send(GetInspireMePhotosCommand.forPage(RANDOM_SEED, 2))

                  assertTrue(presenter.lastPageReached)
                  assertFalse(presenter.loading)
                  assert(presenter.currentItems.containsAll(stubbedPhotosPage1And2))
                  assert(presenter.currentItems.size == stubbedPhotosPage1And2.size)
               }
            }
         }
      }
   }

   class InspireMeViewPagerComponents : TestComponents<InspireMeViewPagerPresenter, BaseImageViewPagerPresenter.View>() {

      val RANDOM_SEED = 0.1
      lateinit var tripImagesInteractor: TripImagesInteractor

      fun init(args: InspireMeViewPagerArgs, contract: Contract?) {
         presenter = InspireMeViewPagerPresenter(args)
         view = mock()

         val service = MockCommandActionService.Builder().apply {
            actionService(CommandActionService())
            if (contract != null) {
               addContract(contract)
            }
         }.build()
         val janet = Janet.Builder().addService(service).build()
         val sessionPipeCreator = SessionActionPipeCreator(janet)

         tripImagesInteractor = TripImagesInteractor(sessionPipeCreator)

         prepareInjector().apply {
            registerProvider(TripImagesInteractor::class.java, { tripImagesInteractor })
            inject(presenter)
         }
      }

      fun stubArgs(list: MutableList<Inspiration> = mutableListOf(),
                   lastPageReached: Boolean = false,
                   currentItemPosition: Int = 0) =
            InspireMeViewPagerArgs(list, 0.1, lastPageReached, currentItemPosition)
   }
}
