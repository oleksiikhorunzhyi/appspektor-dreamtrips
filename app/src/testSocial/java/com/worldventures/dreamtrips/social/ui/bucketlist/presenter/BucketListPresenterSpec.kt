package com.worldventures.dreamtrips.social.ui.bucketlist.presenter

import com.nhaarman.mockito_kotlin.*
import com.worldventures.core.janet.SessionActionPipeCreator
import com.worldventures.dreamtrips.social.common.presenter.PresenterBaseSpec
import com.worldventures.dreamtrips.social.domain.storage.SocialSnappyRepository
import com.worldventures.dreamtrips.social.ui.bucketlist.model.BucketItem
import com.worldventures.dreamtrips.social.ui.bucketlist.service.BucketInteractor
import com.worldventures.dreamtrips.social.ui.bucketlist.service.command.BucketListCommand
import io.techery.janet.CommandActionService
import io.techery.janet.Janet
import io.techery.janet.command.test.Contract
import io.techery.janet.command.test.MockCommandActionService
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.api.dsl.xit
import org.mockito.ArgumentMatchers.anyInt
import org.mockito.internal.verification.VerificationModeFactory
import kotlin.test.assertEquals

class BucketListPresenterSpec : PresenterBaseSpec({

   describe("BucketTabsPresenter") {

      it("should show loading in onStart") {
         init()

         presenter.onStart()

         verify(view).startLoading()
      }

      it("should load bucket items in onResume") {
         val bucketList = listOf<BucketItem>(stubBucketItem())
         init(Contract.of(BucketListCommand::class.java).result(bucketList))

         presenter.onResume()
         bucketInteractor.bucketListActionPipe().send(BucketListCommand.fetch(true))

         assertEquals(bucketList, presenter.bucketItems)
         verify(view).finishLoading()
         verify(view).setItems(any())
         verify(view).hideEmptyView()
      }

      describe("should correctly set items") {
         beforeEachTest { init() }

         it("on empty items it should hide detail container") {
            presenter.bucketItems = emptyList()

            presenter.refresh()

            verify(view).hideDetailContainer()
            verify(view).setItems(any())
            verify(view, never()).hideEmptyView()
         }

         it("on non empty items it should set items and hide empty view") {
            presenter.bucketItems = listOf(stubBucketItem())

            presenter.refresh()

            verify(view).setItems(any())
            verify(view).putCategoryMarker(anyInt())
            verify(view).hideEmptyView()
         }

         it("should filter out done items correctly") {
            presenter.bucketItems = listOf(stubBucketItem(isDone = true))

            presenter.showCompleted = false
            presenter.refresh()

            assert(presenter.filteredItems.isEmpty())
            verify(view, never()).hideEmptyView()
         }

         it("should filter out todo items correctly") {
            presenter.bucketItems = listOf(stubBucketItem(isDone = false))

            presenter.showToDO = false
            presenter.refresh()

            assert(presenter.filteredItems.isEmpty())
            verify(view, never()).hideEmptyView()
         }

         it("should open last opened bucket item details") {
            val bucket = stubBucketItem()
            presenter.bucketItems = listOf(stubBucketItem(), bucket)
            presenter.lastOpenedBucketItem = bucket
            whenever(view.isTabletLandscape).thenReturn(true)

            presenter.refresh()

            verify(view).openDetails(any())
         }

         it("should not open last opened bucket item details") {
            val bucket = stubBucketItem()
            presenter.lastOpenedBucketItem = bucket
            presenter.bucketItems = listOf(stubBucketItem(), bucket)
            whenever(view.isTabletLandscape).thenReturn(false)

            presenter.refresh()

            verify(view, never()).openDetails(any())
         }
      }
   }

}) {
   companion object {
      lateinit var bucketInteractor: BucketInteractor
      lateinit var presenter: BucketListPresenter
      lateinit var view: BucketListPresenter.View
      val socialSnappy: SocialSnappyRepository = spy()
      val bucketType = BucketItem.BucketType.ACTIVITY

      fun init(contract: Contract? = null) {
         presenter = spy(BucketListPresenter(bucketType))
         view = spy()

         val janetBuilder = Janet.Builder()
         if (contract != null) {
            janetBuilder.addService(MockCommandActionService.Builder()
                  .actionService(CommandActionService())
                  .addContract(contract).build())
         }
         val janet = janetBuilder.build()
         val actionPipeCreator = SessionActionPipeCreator(janet)
         bucketInteractor = BucketInteractor(actionPipeCreator)

         prepareInjector().apply {
            registerProvider(Janet::class.java, { janet })
            registerProvider(BucketInteractor::class.java, { bucketInteractor })
            registerProvider(SocialSnappyRepository::class.java, { socialSnappy })
            inject(presenter)
         }

         presenter.takeView(view)
      }
   }
}