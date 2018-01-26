package com.worldventures.dreamtrips.social.ui.bucketlist.presenter

import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.whenever
import com.worldventures.core.janet.SessionActionPipeCreator
import com.worldventures.core.test.common.Injector
import com.worldventures.dreamtrips.social.common.presenter.PresenterBaseSpec
import com.worldventures.dreamtrips.social.domain.storage.SocialSnappyRepository
import com.worldventures.dreamtrips.social.ui.bucketlist.service.BucketInteractor
import com.worldventures.dreamtrips.social.ui.bucketlist.util.BucketItemInfoHelper
import io.techery.janet.CommandActionService
import io.techery.janet.Janet
import io.techery.janet.command.test.Contract
import io.techery.janet.command.test.MockCommandActionService
import org.jetbrains.spek.api.dsl.SpecBody
import org.jetbrains.spek.api.dsl.it

abstract class BucketDetailsBasePresenterSpec(suite: BucketBaseDetailsTestSuite<BucketBaseDetailsComponents<out BucketDetailsBasePresenter<out BucketDetailsBasePresenter.View<*>, *>,
      out BucketDetailsBasePresenter.View<*>>>) : PresenterBaseSpec(suite) {

   abstract class BucketBaseDetailsTestSuite<out C : BucketBaseDetailsComponents<out BucketDetailsBasePresenter<out BucketDetailsBasePresenter.View<*>, *>,
         out BucketDetailsBasePresenter.View<*>>>(components: C) : TestSuite<C>(components) {

      override fun specs(): SpecBody.() -> Unit = {

         with(components) {

            it("should refresh view in onResume") {
               init()
               linkPresenterAndView()

               presenter.onResume()

               verify(presenter).syncUI()
            }

            it("should properly sync ui") {
               init()
               linkPresenterAndView()

               presenter.syncUI()

               val view = view
               verify(view).setBucketItem(any())
               verify(view).setStatus(any())
               verify(view).setPeople(any())
               verify(view).setTags(any())
               verify(view).setTime(any())
            }
         }
      }
   }

   abstract class BucketBaseDetailsComponents<P : BucketDetailsBasePresenter<V, *>, V : BucketDetailsBasePresenter.View<*>> :
         PresenterBaseSpec.TestComponents<P, V>() {

      val bucketInfoHelper: BucketItemInfoHelper = mock()
      lateinit var bucketInteractor: BucketInteractor

      fun init(contracts: List<Contract> = emptyList()) {
         val serviceBuilder = MockCommandActionService.Builder()
         for (contract in contracts) {
            serviceBuilder.addContract(contract)
         }
         serviceBuilder.actionService(CommandActionService())

         val janet = Janet.Builder().addService(serviceBuilder.build()).build()
         val sessionPipeCreator = SessionActionPipeCreator(janet)

         bucketInteractor = BucketInteractor(sessionPipeCreator)
         whenever(bucketInfoHelper.getTime(any())).thenReturn("")

         val injector = prepareInjector().apply {
            registerProvider(BucketInteractor::class.java, { bucketInteractor })
            registerProvider(SocialSnappyRepository::class.java, { mock() })
            registerProvider(BucketItemInfoHelper::class.java, { bucketInfoHelper })
         }

         onInit(injector, sessionPipeCreator)
      }

      fun initWithContract(contract: Contract) = init(listOf(contract))

      protected abstract fun onInit(injector: Injector, pipeCreator: SessionActionPipeCreator)
   }
}
