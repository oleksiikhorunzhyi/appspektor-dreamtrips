package com.worldventures.dreamtrips.modules.dtl.service

import com.nhaarman.mockito_kotlin.*
import com.worldventures.dreamtrips.core.repository.SnappyRepository
import com.worldventures.dreamtrips.core.test.AssertUtil.assertActionSuccess
import com.worldventures.dreamtrips.core.test.BaseSpec
import com.worldventures.dreamtrips.core.test.MockHttpActionService
import com.worldventures.dreamtrips.core.test.StubServiceWrapper
import com.worldventures.dreamtrips.modules.dtl.model.merchant.DtlMerchant
import com.worldventures.dreamtrips.modules.dtl.model.merchant.DtlMerchantAttribute
import com.worldventures.dreamtrips.modules.dtl.model.merchant.DtlMerchantType
import com.worldventures.dreamtrips.modules.dtl.service.action.DtlMerchantsAction
import com.worldventures.dreamtrips.modules.trips.model.Location
import io.techery.janet.ActionHolder
import io.techery.janet.ActionState
import io.techery.janet.CommandActionService
import io.techery.janet.Janet
import org.jetbrains.spek.api.DescribeBody
import rx.observers.TestSubscriber

open class DtlBaseMerchantSpec(spekBody: DescribeBody.() -> Unit) : BaseSpec(spekBody) {

    companion object {
        val MERCHANT_ID = "test"
        private val merchant: DtlMerchant = mock()
        private val merchantList = listOf(merchant)

        lateinit var janet: Janet
        lateinit var locationInteractor: DtlLocationInteractor
        lateinit var merchantInteractor: DtlMerchantInteractor
        lateinit var httpStubWrapper: StubServiceWrapper
        lateinit var db: SnappyRepository


        //initialization base objects to send DtlMerchantsAction
        fun initVars() {
            val commandDaggerService = CommandActionService().wrapDagger()
            httpStubWrapper = MockHttpActionService.Builder()
                    .bind(MockHttpActionService.Response(200).body(merchantList))
                    { request -> request.url.contains("/merchants") }
                    .build()
                    .wrapStub()

            janet = Janet.Builder()
                    .addService(commandDaggerService.wrapCache())
                    .addService(httpStubWrapper.wrapCache())
                    .build()


            locationInteractor = DtlLocationInteractor(janet)
            merchantInteractor = DtlMerchantInteractor(janet, locationInteractor)
            db = spy()

            commandDaggerService.registerProvider(Janet::class.java) { janet }
            commandDaggerService.registerProvider(DtlMerchantInteractor::class.java) { merchantInteractor }
            commandDaggerService.registerProvider(SnappyRepository::class.java) { db }

            //mock merchant properties
            whenever(merchant.id).thenReturn(MERCHANT_ID)
            whenever(merchant.coordinates).thenReturn(Location(1.0, 1.0))
            whenever(merchant.analyticsName).thenReturn("test")
            whenever(merchant.amenities).thenReturn(emptyList<DtlMerchantAttribute>())
            whenever(merchant.budget).thenReturn(3)
            whenever(merchant.displayName).thenReturn("test")
            whenever(merchant.merchantType).thenReturn(DtlMerchantType.DINING)
        }

        fun checkMerchantActionLoad() {
            val subscriber = TestSubscriber<ActionState<DtlMerchantsAction>>()
            val spyHttpCallback = httpStubWrapper.spyCallback()
            merchantInteractor.merchantsActionPipe()
                    .createObservable(DtlMerchantsAction.load(mock()))
                    .subscribe(subscriber)
            assertActionSuccess(subscriber) { action -> action.result.isNotEmpty() && action.isFromApi }
            verify(spyHttpCallback).onSend(any<ActionHolder<Any>>())
        }

        fun checkMerchantActionRestore() {
            val subscriber = TestSubscriber<ActionState<DtlMerchantsAction>>()
            val spyHttpCallback = httpStubWrapper.spyCallback()
            merchantInteractor.merchantsActionPipe()
                    .createObservable(DtlMerchantsAction.restore())
                    .subscribe(subscriber)
            assertActionSuccess<DtlMerchantsAction>(subscriber) { action -> action.result.isNotEmpty() && !action.isFromApi }
            verify(spyHttpCallback, never()).onSend(any<ActionHolder<Any>>())
        }

    }

}
