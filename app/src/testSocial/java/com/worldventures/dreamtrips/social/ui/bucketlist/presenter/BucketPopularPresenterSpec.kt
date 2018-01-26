package com.worldventures.dreamtrips.social.ui.bucketlist.presenter

import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.never
import com.nhaarman.mockito_kotlin.spy
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.whenever
import com.worldventures.core.janet.SessionActionPipeCreator
import com.worldventures.dreamtrips.social.common.presenter.PresenterBaseSpec
import com.worldventures.dreamtrips.social.domain.storage.SocialSnappyRepository
import com.worldventures.dreamtrips.social.ui.bucketlist.model.BucketItem
import com.worldventures.dreamtrips.social.ui.bucketlist.model.PopularBucketItem
import com.worldventures.dreamtrips.social.ui.bucketlist.service.BucketInteractor
import com.worldventures.dreamtrips.social.ui.bucketlist.service.action.CreateBucketItemCommand
import com.worldventures.dreamtrips.social.ui.bucketlist.service.command.GetPopularBucketItemSuggestionsCommand
import com.worldventures.dreamtrips.social.ui.bucketlist.service.command.GetPopularBucketItemsCommand
import io.techery.janet.CommandActionService
import io.techery.janet.Janet
import io.techery.janet.command.test.Contract
import io.techery.janet.command.test.MockCommandActionService
import org.jetbrains.spek.api.dsl.SpecBody
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it

class BucketPopularPresenterSpec : PresenterBaseSpec(BucketListTestSuite()) {

   class BucketListTestSuite : TestSuite<BucketPopularComponents>(BucketPopularComponents()) {

      override fun specs(): SpecBody.() -> Unit = {

         with(components) {
            describe("BucketTabsPresenter") {

               describe("Lifecycle actions") {

                  it("should reload in onResume if items are empty") {
                     init(Contract.of(GetPopularBucketItemsCommand::class.java).result(emptyList<PopularBucketItem>()))
                     linkPresenterAndView()

                     whenever(view.itemsCount).thenReturn(0)
                     presenter.onResume()

                     verify(view).finishLoading()
                     verify(view).setItems(any())
                  }

                  it("should not reload items are not empty") {
                     init()
                     linkPresenterAndView()

                     whenever(view.itemsCount).thenReturn(5)
                     presenter.onResume()

                     verify(view, never()).finishLoading()
                     verify(view, never()).setItems(any())
                  }
               }

               describe("Search actions") {
                  it("should not trigger search if search query is too short") {
                     init(Contract.of(GetPopularBucketItemSuggestionsCommand::class.java)
                           .result(emptyList<PopularBucketItem>()))
                     linkPresenterAndView()

                     presenter.onSearch("11")

                     verify(view, never()).finishLoading()
                     verify(view, never()).setFilteredItems(any())
                  }

                  it("should trigger search") {
                     init(Contract.of(GetPopularBucketItemSuggestionsCommand::class.java)
                           .result(emptyList<PopularBucketItem>()))
                     linkPresenterAndView()

                     presenter.onSearch("111")

                     verify(view).finishLoading()
                     verify(view).setFilteredItems(any())
                  }

                  it("should refresh view on search success") {
                     init(Contract.of(GetPopularBucketItemSuggestionsCommand::class.java).result(emptyList<PopularBucketItem>()))
                     linkPresenterAndView()

                     presenter.searchPopularItems("111")

                     verify(view).finishLoading()
                     verify(view).setFilteredItems(any())
                  }

                  it("should process error if search failed") {
                     init(Contract.of(GetPopularBucketItemSuggestionsCommand::class.java).exception(Exception()))
                     linkPresenterAndView()

                     presenter.searchPopularItems("111")

                     verify(view).finishLoading()
                  }

                  it("should flush filter on search closed") {
                     init()
                     linkPresenterAndView()

                     presenter.searchClosed()

                     verify(view).flushFilter()
                  }
               }

               describe("working with data actions") {

                  it("should refresh view on reloading") {
                     init(Contract.of(GetPopularBucketItemsCommand::class.java).result(emptyList<PopularBucketItem>()))
                     linkPresenterAndView()

                     presenter.reload()

                     verify(view).finishLoading()
                     verify(view).setItems(any())
                  }

                  it("should refresh view after adding bucket item from popular") {
                     init(Contract.of(CreateBucketItemCommand::class.java).result(stubBucketItem()))
                     linkPresenterAndView()

                     presenter.add(stubPopularBucketItem(), true)

                     verify(view).notifyItemWasAddedToBucketList(any())
                     verify(view).removeItem(any())
                  }

                  it("should process error if adding bucket from popular failed") {
                     init(Contract.of(CreateBucketItemCommand::class.java).exception(Exception()))
                     linkPresenterAndView()

                     presenter.add(stubPopularBucketItem(), true)

                     verify(view).notifyItemsChanged()
                  }
               }
            }
         }
      }
   }

   class BucketPopularComponents : TestComponents<BucketPopularPresenter, BucketPopularPresenter.View>() {

      fun init(contract: Contract? = null) {
         presenter = BucketPopularPresenter(BucketItem.BucketType.ACTIVITY)
         view = spy()

         val janetBuilder = Janet.Builder()
         if (contract != null) {
            janetBuilder.addService(MockCommandActionService.Builder()
                  .actionService(CommandActionService())
                  .addContract(contract).build())
         }
         val janet = janetBuilder.build()
         val actionPipeCreator = SessionActionPipeCreator(janet)
         val bucketInteractor = BucketInteractor(actionPipeCreator)

         prepareInjector().apply {
            registerProvider(Janet::class.java, { janet })
            registerProvider(BucketInteractor::class.java, { bucketInteractor })
            registerProvider(SocialSnappyRepository::class.java, { spy() })
            inject(presenter)
         }
      }

      fun stubPopularBucketItem(): PopularBucketItem {
         val bucket = PopularBucketItem()
         bucket.id = 15
         bucket.name = "bucket"
         return bucket
      }
   }
}
