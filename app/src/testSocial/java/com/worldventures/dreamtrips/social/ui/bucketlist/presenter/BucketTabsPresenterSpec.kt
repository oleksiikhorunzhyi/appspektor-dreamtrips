package com.worldventures.dreamtrips.social.ui.bucketlist.presenter

import com.nhaarman.mockito_kotlin.argumentCaptor
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.spy
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.whenever
import com.worldventures.core.janet.SessionActionPipeCreator
import com.worldventures.core.model.User
import com.worldventures.core.model.session.SessionHolder
import com.worldventures.core.model.session.UserSession
import com.worldventures.core.storage.complex_objects.Optional
import com.worldventures.dreamtrips.social.common.presenter.PresenterBaseSpec
import com.worldventures.dreamtrips.social.domain.storage.SocialSnappyRepository
import com.worldventures.dreamtrips.social.ui.bucketlist.model.BucketItem
import com.worldventures.dreamtrips.social.ui.bucketlist.service.BucketInteractor
import com.worldventures.dreamtrips.social.ui.bucketlist.service.command.BucketListCommand
import io.techery.janet.CommandActionService
import io.techery.janet.Janet
import io.techery.janet.command.test.Contract
import io.techery.janet.command.test.MockCommandActionService
import org.jetbrains.spek.api.dsl.SpecBody
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import rx.observers.TestSubscriber
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class BucketTabsPresenterSpec : PresenterBaseSpec(BucketTabsTestSuites()) {

   class BucketTabsTestSuites : TestSuite<BucketTabsComponents>(BucketTabsComponents()) {

      override fun specs(): SpecBody.() -> Unit = {

         with(components) {
            describe("Bucket Tabs Presenter") {

               it("should set tabs") {
                  init()
                  linkPresenterAndView()

                  presenter.setTabs()

                  val expectedList = listOf(BucketItem.BucketType.LOCATION, BucketItem.BucketType.ACTIVITY,
                        BucketItem.BucketType.DINING)
                  val argCaptor = argumentCaptor<List<BucketItem.BucketType>>()
                  verify(view).setTypes(argCaptor.capture())
                  verify(view).updateSelection()

                  val actualBucketTypesList = argCaptor.firstValue

                  assertEquals(actualBucketTypesList.size, expectedList.size)
                  assertTrue(actualBucketTypesList.containsAll(expectedList) && expectedList.containsAll(actualBucketTypesList))
               }

               it("should load bucket list") {
                  init(Contract.of(BucketListCommand::class.java).result(emptyList<BucketItem>()))

                  val subscriber: TestSubscriber<BucketListCommand> = TestSubscriber()
                  bucketInteractor.bucketListActionPipe().observeSuccess().subscribe(subscriber)
                  presenter.loadBucketList()

                  subscriber.assertValueCount(1)
               }
            }
         }
      }
   }

   class BucketTabsComponents : TestComponents<BucketTabsPresenter, BucketTabsPresenter.View>() {

      lateinit var bucketInteractor: BucketInteractor

      fun init(contract: Contract? = null) {
         presenter = BucketTabsPresenter()
         view = spy()

         val janetBuilder = Janet.Builder()
         if (contract != null) {
            janetBuilder.addService(MockCommandActionService.Builder()
                  .actionService(CommandActionService())
                  .addContract(contract).build())
         }
         val actionPipeCreator = SessionActionPipeCreator(janetBuilder.build())
         bucketInteractor = BucketInteractor(actionPipeCreator)

         prepareInjector(makeSessionHolder()).apply {
            registerProvider(BucketInteractor::class.java, { bucketInteractor })
            registerProvider(SocialSnappyRepository::class.java, { spy() })
            inject(presenter)
         }
      }

      private fun makeSessionHolder(): SessionHolder {
         val sessionHolder = mock<SessionHolder>()
         val user = User()
         user.id = 11
         val userSession = mock<UserSession>()
         whenever(userSession.user()).thenReturn(user)
         whenever(sessionHolder.get()).thenReturn(Optional.of(userSession))
         return sessionHolder
      }

   }
}
