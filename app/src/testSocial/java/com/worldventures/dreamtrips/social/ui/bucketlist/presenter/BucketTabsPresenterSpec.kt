package com.worldventures.dreamtrips.social.ui.bucketlist.presenter

import com.nhaarman.mockito_kotlin.*
import com.worldventures.core.janet.SessionActionPipeCreator
import com.worldventures.core.model.User
import com.worldventures.dreamtrips.social.common.presenter.PresenterBaseSpec
import com.worldventures.dreamtrips.social.domain.storage.SocialSnappyRepository
import com.worldventures.dreamtrips.social.ui.bucketlist.model.BucketItem
import com.worldventures.dreamtrips.social.ui.bucketlist.model.CategoryItem
import com.worldventures.dreamtrips.social.ui.bucketlist.service.BucketInteractor
import com.worldventures.dreamtrips.social.ui.bucketlist.service.command.BucketListCommand
import com.worldventures.dreamtrips.social.ui.bucketlist.service.command.GetCategoriesCommand
import io.techery.janet.CommandActionService
import io.techery.janet.Janet
import io.techery.janet.command.test.Contract
import io.techery.janet.command.test.MockCommandActionService
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import org.mockito.ArgumentCaptor
import rx.observers.TestSubscriber
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class BucketTabsPresenterSpec : PresenterBaseSpec({

   describe("BucketTabsPresenter") {

      it("should do proper initialization on view taken") {
         init()

         doNothing().whenever(presenter).setTabs()
         doNothing().whenever(presenter).loadCategories()
         doNothing().whenever(presenter).loadBucketList()
         doNothing().whenever(presenter).subscribeToErrorUpdates()

         presenter.onViewTaken()

         verify(presenter).setTabs()
         verify(presenter).loadCategories()
         verify(presenter).loadBucketList()
         verify(presenter).subscribeToErrorUpdates()
      }

      it("should set tabs") {
         init()

         presenter.setTabs()

         val listClass = List::class.java as Class<List<BucketItem.BucketType>>
         val argumentCaptor = ArgumentCaptor.forClass(listClass)
         verify(view).setTypes(argumentCaptor.capture())
         verify(view).updateSelection()

         val actualBucketTypesList = argumentCaptor.allValues[0]
         val expectedList = listOf(BucketItem.BucketType.LOCATION, BucketItem.BucketType.ACTIVITY,
               BucketItem.BucketType.DINING)
         assertEquals(actualBucketTypesList.size, expectedList.size)
         assertTrue(actualBucketTypesList.containsAll(expectedList) && expectedList.containsAll(actualBucketTypesList))
      }

      it("should load categories") {
         val categoriesList = listOf(CategoryItem(1, "Test"))
         init(Contract.of(GetCategoriesCommand::class.java).result(categoriesList))

         presenter.loadCategories()

         verify(socialSnappy).saveBucketListCategories(categoriesList)
      }

      it("should load categories and process error properly") {
         init(Contract.of(GetCategoriesCommand::class.java).exception(Exception()))

         presenter.loadCategories()

         verify(presenter).handleError(any(), any())
      }

      it("should load bucket list") {
         init(Contract.of(BucketListCommand::class.java).result(emptyList<BucketItem>()))

         val subscriber: TestSubscriber<BucketListCommand> = TestSubscriber()
         bucketInteractor.bucketListActionPipe().observeSuccess().subscribe(subscriber)
         doReturn(User()).whenever(presenter).account

         presenter.loadBucketList()
      }
   }

}) {
   companion object {
      lateinit var bucketInteractor: BucketInteractor
      lateinit var presenter: BucketTabsPresenter
      lateinit var view: BucketTabsPresenter.View
      val socialSnappy: SocialSnappyRepository = spy()

      fun init(contract: Contract? = null) {
         presenter = spy(BucketTabsPresenter())
         view = spy()

         val janetBuilder = Janet.Builder()
         if (contract != null) {
            janetBuilder.addService(MockCommandActionService.Builder()
                  .actionService(CommandActionService())
                  .addContract(contract).build())
         }
         val actionPipeCreator = SessionActionPipeCreator(janetBuilder.build())
         bucketInteractor = BucketInteractor(actionPipeCreator)

         prepareInjector().apply {
            registerProvider(BucketInteractor::class.java, { bucketInteractor })
            registerProvider(SocialSnappyRepository::class.java, { socialSnappy })
            inject(presenter)
         }

         presenter.takeView(view)
      }
   }
}