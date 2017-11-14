package com.worldventures.dreamtrips.social.ui.bucketlist.presenter

import com.nhaarman.mockito_kotlin.*
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
import org.jetbrains.spek.api.dsl.Spec
import org.jetbrains.spek.api.dsl.it

abstract class BucketDetailsBasePresenterSpec<P: BucketDetailsBasePresenter<V, *>, V: BucketDetailsBasePresenter.View<*>,
      T: BucketDetailsBasePresenterSpec.TestBody<P, V>>(testBody: T) : PresenterBaseSpec(testBody.getBody()) {

   abstract class TestBody<P: BucketDetailsBasePresenter<V, *>, V: BucketDetailsBasePresenter.View<*>> {

      lateinit var presenter: P
      lateinit var view: V
      protected lateinit var bucketInfoHelper: BucketItemInfoHelper
      protected lateinit var bucketInteractor: BucketInteractor
      val socialSnappy = mock<SocialSnappyRepository>()

      abstract fun describeTest(): String
      abstract fun createPresenter(): P
      abstract fun createView(): V
      abstract fun onSetupInjector(injector: Injector, pipeCreator: SessionActionPipeCreator)

      fun getBody(): Spec.() -> Unit {
         return {
            for (suite in getSuiteList()) {
               suite.invoke(this)
            }
         }
      }

      fun getSuiteList(): List<Spec.() -> Unit> {
         return listOf(getBaseTestSuite(), getMainTestSuite())
      }

      fun getBaseTestSuite(): Spec.() -> Unit {
         return {
            it("should refresh view in onResume") {
               onResumeTestSetup()

               presenter.onResume()

               verify(presenter).syncUI()
            }

            it("should properly sync ui") {
               setup()

               presenter.syncUI()

               verify(view).setBucketItem(any())
               verify(view).setStatus(any())
               verify(view).setPeople(any())
               verify(view).setTags(any())
               verify(view).setTime(any())
            }
         }
      }

      open fun onResumeTestSetup() {
         setup()
      }

      open fun getMainTestSuite(): Spec.() -> Unit  = {}

      fun setup(contract: Contract) {
         setup(listOf(contract))
      }

      fun setup(contracts: List<Contract> = emptyList()) {
         presenter = spy(createPresenter())
         view = createView()

         val serviceBuilder = MockCommandActionService.Builder()
         for (contract in contracts) {
            serviceBuilder.addContract(contract)
         }
         serviceBuilder.actionService(CommandActionService())

         val janet = Janet.Builder().addService(serviceBuilder.build()).build()
         val sessionPipeCreator = SessionActionPipeCreator(janet)

         val injector = prepareInjector()

         injector.apply {
            bucketInteractor = BucketInteractor(sessionPipeCreator)
            registerProvider(BucketInteractor::class.java, { bucketInteractor })

            registerProvider(SocialSnappyRepository::class.java, { socialSnappy })
            bucketInfoHelper = mock<BucketItemInfoHelper>()
            whenever(bucketInfoHelper.getTime(any())).thenReturn("")
            registerProvider(BucketItemInfoHelper::class.java, { bucketInfoHelper })
         }

         onSetupInjector(injector, sessionPipeCreator)
         injector.inject(presenter)

         presenter.takeView(view)
      }
   }

}