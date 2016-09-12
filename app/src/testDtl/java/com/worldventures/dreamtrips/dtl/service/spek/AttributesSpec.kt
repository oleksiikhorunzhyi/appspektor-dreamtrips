package com.worldventures.dreamtrips.dtl.service.spek

import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.whenever
import com.worldventures.dreamtrips.AssertUtil
import com.worldventures.dreamtrips.BaseSpec
import com.worldventures.dreamtrips.api.dtl.attributes.model.Attribute
import com.worldventures.dreamtrips.api.dtl.attributes.model.AttributeType
import com.worldventures.dreamtrips.janet.StubServiceWrapper
import com.worldventures.dreamtrips.modules.dtl.service.AttributesInteractor
import com.worldventures.dreamtrips.modules.dtl.service.action.AttributesAction
import io.techery.janet.ActionHolder
import io.techery.janet.ActionState
import io.techery.janet.CommandActionService
import io.techery.janet.Janet
import io.techery.janet.http.test.MockHttpActionService
import rx.observers.TestSubscriber

class AttributesSpec : BaseSpec({

   val loadAmenitiesSubscriber = TestSubscriber<ActionState<AttributesAction>>()

   given("Setup") {
      on("init basic variables") {
         initVars()
      }
      on("subscribe amenities pipe") {
         janet.createPipe(AttributesAction::class.java).observe()
               .subscribe(loadAmenitiesSubscriber)
      }
   }

   describe("Loading amenities with AtributesAction") {
      it("should load attributes list") {
         checkAttributesActionLoad()
      }
   }
}) {
   companion object {

      private val attribute: Attribute = mock()
      private val attributesList = listOf(attribute)

      lateinit var janet: Janet
      lateinit var httpStubWrapper: StubServiceWrapper
      lateinit var attributesInteractor: AttributesInteractor

      fun initVars() {
         val commandDaggerService = CommandActionService().wrapDagger()
         httpStubWrapper = MockHttpActionService.Builder()
               .bind(MockHttpActionService
                     .Response(200)
                     .body(attributesList)) {
                  it.url.contains("/attributes")
               }
               .build()
               .wrapStub()

         janet = Janet.Builder()
               .addService(commandDaggerService.wrapCache())
               .addService(httpStubWrapper.wrapCache())
               .build()

         attributesInteractor = AttributesInteractor(janet)
         commandDaggerService.registerProvider(Janet::class.java) { janet }

         whenever(attribute.id()).thenReturn(1)
         whenever(attribute.type()).thenReturn(AttributeType.AMENITY)
         whenever(attribute.name()).thenReturn("name")
         whenever(attribute.displayName()).thenReturn("displayName")
         whenever(attribute.merchantCount()).thenReturn(5)
         whenever(attribute.partnerCount()).thenReturn(5)
      }

      fun checkAttributesActionLoad() {
         val subscriber = TestSubscriber<ActionState<AttributesAction>>()
         val spyHttpCallback = httpStubWrapper.spyCallback()

         attributesInteractor.attributesPipe()
               .createObservable(AttributesAction())
               .subscribe(subscriber)

         AssertUtil.assertActionSuccess(subscriber) { action -> action.result.isNotEmpty() }
         verify(spyHttpCallback).onSend(any<ActionHolder<Any>>())
      }
   }
}
