package com.worldventures.dreamtrips.social.background_uploading.spec

import com.innahema.collections.query.queriables.Queryable
import com.worldventures.dreamtrips.AssertUtil
import com.worldventures.dreamtrips.modules.background_uploading.model.CompoundOperationState
import com.worldventures.dreamtrips.modules.background_uploading.service.command.CompoundOperationsCommand
import com.worldventures.dreamtrips.modules.background_uploading.service.command.RestoreCompoundOperationsCommand
import com.worldventures.dreamtrips.modules.background_uploading.service.command.UpdateCompoundOperationsCommand
import io.techery.janet.ActionState
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.api.dsl.xdescribe
import rx.observers.TestSubscriber

class RestoreCompoundOperationsSpec : BaseUploadingInteractorSpec({
   describe("Restore compound operations command") {
      initJanet(queryCompoundOperationsNotEmptyContract())

      val testSubscribeRestore = TestSubscriber<ActionState<RestoreCompoundOperationsCommand>>()
      val testSubscriberUpdate = TestSubscriber<ActionState<CompoundOperationsCommand>>()

      compoundOperationsInteractor.compoundOperationsPipe()
            .observe()
            .filter { it.action is UpdateCompoundOperationsCommand }
            .subscribe(testSubscriberUpdate)
      backgroundUploadingInteractor.restoreCompoundOperationsPipe()
            .createObservable(RestoreCompoundOperationsCommand())
            .subscribe(testSubscribeRestore)

      it("Should update compound operations and all items must be paused") {
         AssertUtil.assertStatusCount(testSubscriberUpdate, ActionState.Status.START, 1)
         AssertUtil.assertActionSuccess(testSubscribeRestore) {
            Queryable.from(it.result).all { it.state() == CompoundOperationState.PAUSED }
         }
      }
   }
})