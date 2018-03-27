package com.worldventures.dreamtrips.social.ui.bucketlist.presenter

import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.spy
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.whenever
import com.worldventures.core.janet.SessionActionPipeCreator
import com.worldventures.core.test.common.Injector
import com.worldventures.dreamtrips.social.ui.bucketlist.model.BucketItem
import com.worldventures.dreamtrips.social.ui.bucketlist.model.BucketPhoto
import com.worldventures.dreamtrips.social.ui.bucketlist.model.DiningItem
import com.worldventures.dreamtrips.social.ui.bucketlist.service.action.UpdateBucketItemCommand
import com.worldventures.dreamtrips.social.ui.bucketlist.service.command.TranslateBucketItemCommand
import com.worldventures.dreamtrips.social.ui.feed.presenter.delegate.FeedEntityHolderDelegate
import com.worldventures.dreamtrips.social.ui.feed.service.TranslationFeedInteractor
import io.techery.janet.command.test.Contract
import org.jetbrains.spek.api.dsl.SpecBody
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import rx.observers.TestSubscriber
import kotlin.test.assertFalse

class BucketDetailsPresenterSpec : BucketDetailsBasePresenterSpec(BucketDetailsSuite()) {

   class BucketDetailsSuite : BucketBaseDetailsTestSuite<BucketDetailsComponents>(BucketDetailsComponents()) {

      override fun specs(): SpecBody.() -> Unit = {

         with(components) {
            describe("Bucket Details Presenter") {

               super.specs().invoke(this)

               it("should properly init view") {
                  init()
                  linkPresenterAndView()

                  presenter.onViewTaken()

                  verify(feedEntityHolderDelegate).subscribeToUpdates(any(), any(), any())
               }

               it("should properly sync ui in bucket details") {
                  init()
                  linkPresenterAndView()

                  val presenter = presenter
                  val view = view
                  presenter.bucketItem.photos = arrayListOf(BucketPhoto())
                  presenter.bucketItem.dining = DiningItem()
                  whenever(bucketInfoHelper.getPlace(any())).thenReturn("")

                  presenter.syncUI()

                  verify(view).setImages(any())
                  verify(view).setCategory(any())
                  verify(view).setPlace(any())
                  verify(view).setupDiningView(any())
                  verify(view).setGalleryEnabled(any())
               }

               it("should translate item if it is not translated") {
                  initWithContract(Contract.of(TranslateBucketItemCommand::class.java).result(stubBucketItem()))
                  linkPresenterAndView()

                  val presenter = presenter
                  presenter.bucketItem.isTranslated = false
                  val subscriber = TestSubscriber<TranslateBucketItemCommand>()
                  translationInteractor.translateBucketItemPipe().observeSuccess().subscribe(subscriber)

                  presenter.subscribeToTranslations()
                  presenter.onTranslateClicked()

                  subscriber.assertNoErrors()
                  verify(view).setBucketItem(any())
               }

               it("should not translate item if it's translated already") {
                  init()
                  linkPresenterAndView()

                  presenter.bucketItem.isTranslated = true
                  presenter.onTranslateClicked()

                  assertFalse(presenter.bucketItem.isTranslated)
                  verify(view).setBucketItem(any())
               }

               it("should update bucket if new status is done") {
                  initWithContract(Contract.of(UpdateBucketItemCommand::class.java).result(stubBucketItem()))
                  linkPresenterAndView()

                  val presenter = presenter
                  presenter.bucketItem.status = BucketItem.NEW
                  val subscriber = TestSubscriber<UpdateBucketItemCommand>()
                  bucketInteractor.updatePipe().observeSuccess().subscribe(subscriber)

                  presenter.onStatusUpdated(true)
                  subscriber.assertNoErrors()
                  subscriber.assertValueCount(1)
                  verify(view).enableMarkAsDone()
               }

               it("should update bucket item status on error") {
                  initWithContract(Contract.of(UpdateBucketItemCommand::class.java).exception(Exception()))
                  linkPresenterAndView()

                  presenter.onStatusUpdated(true)
                  verify(view).enableMarkAsDone()
               }
            }
         }
      }
   }

   class BucketDetailsComponents : BucketBaseDetailsComponents<BucketItemDetailsPresenter, BucketItemDetailsPresenter.View>() {

      val feedEntityHolderDelegate = mock<FeedEntityHolderDelegate>()
      lateinit var translationInteractor: TranslationFeedInteractor

      override fun onInit(injector: Injector, pipeCreator: SessionActionPipeCreator) {
         view = mock()
         presenter = spy(BucketItemDetailsPresenter(BucketItem.BucketType.ACTIVITY, stubBucketItem(), 11))

         translationInteractor = TranslationFeedInteractor(pipeCreator)
         injector.apply {
            registerProvider(TranslationFeedInteractor::class.java, { translationInteractor })
            registerProvider(FeedEntityHolderDelegate::class.java, { feedEntityHolderDelegate })
            inject(presenter)
         }
      }
   }
}
