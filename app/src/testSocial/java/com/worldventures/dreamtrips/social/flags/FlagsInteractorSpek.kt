package com.worldventures.dreamtrips.social.flags

import com.worldventures.dreamtrips.AssertUtil.assertActionSuccess
import com.worldventures.dreamtrips.BaseSpec
import com.worldventures.dreamtrips.api.flagging.model.FlagReason
import com.worldventures.dreamtrips.api.flagging.model.ImmutableFlagReason
import com.worldventures.dreamtrips.core.janet.SessionActionPipeCreator
import com.worldventures.dreamtrips.modules.flags.command.GetFlagsCommand
import com.worldventures.dreamtrips.modules.flags.service.FlagsInteractor
import com.worldventures.dreamtrips.modules.mapping.converter.FlagConverter
import com.worldventures.dreamtrips.modules.tripsimages.model.Flag
import io.techery.janet.ActionState
import io.techery.janet.CommandActionService
import io.techery.janet.Janet
import io.techery.janet.http.test.MockHttpActionService
import io.techery.mappery.Mappery
import io.techery.mappery.MapperyContext
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import org.junit.Assert
import rx.observers.TestSubscriber
import java.util.*
import kotlin.test.assertEquals

class FlagsInteractorSpek : BaseSpec({

   describe("Getting flags command") {
      setup()

      it ("should get proper flags when calling API") {
         assertActionSuccess(loadFlags()) {
            checkIfFlagsAreValid(it.result)
         }
      }

      it ("should get proper flags from cache without calling API") {
         val testSubscriber = loadFlags()
         assertEquals(apiCallsCount, 1)
         assertActionSuccess(testSubscriber, {
            checkIfFlagsAreValid(it.result)
         })

      }
   }

}) {
   companion object {
      lateinit var flagsInteractor: FlagsInteractor
      lateinit var stubFlags: List<FlagReason>

      var apiCallsCount = 0

      fun setup() {
         stubFlags = makeStubFlagReasons()

         val daggerCommandActionService = CommandActionService()
               .wrapCache()
               .wrapDagger()

         val janet = Janet.Builder()
               .addService(daggerCommandActionService)
               .addService(mockHttpService(stubFlags))
               .build()

         daggerCommandActionService.registerProvider(Janet::class.java) { janet }

         val mapperyBuilder = Mappery.Builder();
         val converter = FlagConverter();
         mapperyBuilder.map(converter.sourceClass()).to(converter.targetClass(), converter)
         val mappery = mapperyBuilder.build();
         daggerCommandActionService.registerProvider(MapperyContext::class.java) { mappery }

         flagsInteractor = FlagsInteractor(SessionActionPipeCreator(janet))
      }

      fun makeStubFlagReasons(): List<FlagReason> {
         val flags = ArrayList<FlagReason>()
         for (i in 1..2) {
            flags.add(makeStubFlagReason(i))
         }
         return flags
      }

      fun makeStubFlagReason(i: Int): FlagReason {
         return ImmutableFlagReason.builder()
            .id(i)
            .name("FlagReason#" + i)
            .requireDescription(i % 2 == 0)
            .build()
      }

      fun mockHttpService(flags: List<FlagReason>): MockHttpActionService {
         return MockHttpActionService.Builder()
               .bind(MockHttpActionService.Response(200).body(flags)) {
                  apiCallsCount++;
                  it.url.contains("/api/flag_reasons")
               }
               .build()
      }

      fun loadFlags(): TestSubscriber<ActionState<GetFlagsCommand>> {
         var subscriber: TestSubscriber<ActionState<GetFlagsCommand>> = TestSubscriber()
         flagsInteractor.flagsPipe.createObservable(GetFlagsCommand()).subscribe(subscriber)
         return subscriber
      }

      fun checkIfFlagsAreValid(responseFlags: List<Flag>): Boolean {
         Assert.assertTrue(stubFlags.size == responseFlags.size)
         for (i in 0..stubFlags.size - 1) {
            val apiFlag = stubFlags[i]
            val mappedFlag = responseFlags[i]
            assertEquals(apiFlag.id(), mappedFlag.id)
            assertEquals(apiFlag.name(), mappedFlag.name)
            assertEquals(apiFlag.requireDescription(), mappedFlag.isRequireDescription)
         }
         return true
      }
   }
}

