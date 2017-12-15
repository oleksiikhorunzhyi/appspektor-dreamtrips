package com.worldventures.dreamtrips.social.ui.bucketlist.presenter

import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.mock
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
import org.jetbrains.spek.api.dsl.Spec
import org.jetbrains.spek.api.dsl.it
import rx.observers.TestSubscriber
import kotlin.test.assertFalse

class BucketItemDetailsPresenterSpec : BucketDetailsBasePresenterSpec<BucketItemDetailsPresenter, BucketItemDetailsPresenter.View,
      BucketItemDetailsPresenterSpec.DetailsTestBody>(DetailsTestBody()) {

   class DetailsTestBody: BucketDetailsBasePresenterSpec.TestBody<BucketItemDetailsPresenter, BucketItemDetailsPresenter.View>() {
      val feedEntityHolderDelegate = mock<FeedEntityHolderDelegate>()
      lateinit var translationInteractor: TranslationFeedInteractor

      override fun describeTest(): String = BucketItemDetailsPresenter::class.java.simpleName

      override fun createPresenter(): BucketItemDetailsPresenter = BucketItemDetailsPresenter(BucketItem.BucketType.ACTIVITY, stubBucketItem(), 11)

      override fun createView(): BucketItemDetailsPresenter.View = mock()

      override fun onSetupInjector(injector: Injector, pipeCreator: SessionActionPipeCreator) {
         translationInteractor = TranslationFeedInteractor(pipeCreator)
         injector.registerProvider(TranslationFeedInteractor::class.java, { translationInteractor })
         injector.registerProvider(FeedEntityHolderDelegate::class.java, { feedEntityHolderDelegate })
      }

      override fun getMainTestSuite(): Spec.() -> Unit {
         return {
            it("should properly init view") {
               setup()

               presenter.onViewTaken()

               verify(feedEntityHolderDelegate).subscribeToUpdates(any(), any(), any())
            }

            it("should properly sync ui in bucket details") {
               setup()
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
               setup(Contract.of(TranslateBucketItemCommand::class.java).result(presenter.bucketItem))
               presenter.bucketItem.isTranslated = false
               val subscriber = TestSubscriber<TranslateBucketItemCommand>()
               translationInteractor.translateBucketItemPipe().observeSuccess().subscribe(subscriber)

               presenter.subscribeToTranslations()
               presenter.onTranslateClicked()

               subscriber.assertNoErrors()
               verify(view).setBucketItem(any())
            }

            it("should not translate item if it's translated already") {
               setup()
               presenter.bucketItem.isTranslated = true

               presenter.onTranslateClicked()

               assertFalse(presenter.bucketItem.isTranslated)
               verify(view).setBucketItem(any())
            }

            it("should update bucket if new status is done") {
               setup(Contract.of(UpdateBucketItemCommand::class.java).result(presenter.bucketItem))
               presenter.bucketItem.status = BucketItem.NEW
               val subscriber = TestSubscriber<UpdateBucketItemCommand>()
               bucketInteractor.updatePipe().observeSuccess().subscribe(subscriber)

               presenter.onStatusUpdated(true)
               subscriber.assertNoErrors()
               subscriber.assertValueCount(1)
               verify(view).enableMarkAsDone()
            }

            it("should update bucket item status on error") {
               setup(Contract.of(UpdateBucketItemCommand::class.java).exception(Exception()))

               presenter.onStatusUpdated(true)

               verify(view).enableMarkAsDone()
            }
         }
      }
   }
}
