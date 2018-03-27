package com.worldventures.dreamtrips.social.service.bucketlist.command

import com.nhaarman.mockito_kotlin.spy
import com.nhaarman.mockito_kotlin.whenever
import com.worldventures.core.model.EntityStateHolder
import com.worldventures.core.test.AssertUtil
import com.worldventures.dreamtrips.BaseSpec.Companion.any
import com.worldventures.dreamtrips.BaseSpec.Companion.bindStorageSet
import com.worldventures.dreamtrips.BaseSpec.Companion.wrapCache
import com.worldventures.dreamtrips.BaseSpec.Companion.wrapDagger
import com.worldventures.dreamtrips.social.common.base.BaseBodySpec
import com.worldventures.dreamtrips.social.ui.bucketlist.model.BucketPhoto
import com.worldventures.dreamtrips.social.ui.bucketlist.service.command.UploadPhotoControllerCommand
import com.worldventures.dreamtrips.social.ui.bucketlist.service.storage.UploadBucketPhotoInMemoryStorage
import com.worldventures.janet.cache.CacheBundle
import io.techery.janet.ActionState
import io.techery.janet.CommandActionService
import org.jetbrains.spek.api.dsl.Spec
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import rx.observers.TestSubscriber

class UploadPhotoControllerCommandSpec : BaseBodySpec(object : BaseBucketListCommandTestBody() {

   private val storage = UploadBucketPhotoInMemoryStorage()


   override fun create(): Spec.() -> Unit = {
      describe("Success while executing command") {
         beforeEachTest { setup() }

         it("When PROGRESS status should add photo state holder with") {
            val entityStateHolder = EntityStateHolder.create(mockBucketPhoto(1), EntityStateHolder.State.PROGRESS)

            AssertUtil.assertActionSuccess(sendCommand(UploadPhotoControllerCommand.create(BUCKET_UID, entityStateHolder)))
            {
               it.result.contains(entityStateHolder)
            }
         }

         it("When FAIL status should replace old state holder ") {
            val entityStateHolder = EntityStateHolder.create(mockBucketPhoto(1), EntityStateHolder.State.FAIL)
            setUpStorage()

            AssertUtil.assertActionSuccess(sendCommand(UploadPhotoControllerCommand.create(BUCKET_UID, entityStateHolder)))
            { resultCommand ->
               resultCommand.result.firstOrNull { it.entity() == entityStateHolder.entity() }
                     ?.state() == entityStateHolder.state()
            }

         }

         it("When DONE status should remove it from list") {
            val entityStateHolder = EntityStateHolder.create(mockBucketPhoto(1), EntityStateHolder.State.DONE)
            setUpStorage()

            AssertUtil.assertActionSuccess(sendCommand(
                  UploadPhotoControllerCommand.create(BUCKET_UID, entityStateHolder)
            )) {
               it.result.firstOrNull { it.entity() == entityStateHolder.entity() } == null
            }
         }

         it("Cancel func should remove item fromm list ") {
            val entityStateHolder = EntityStateHolder.create(mockBucketPhoto(1), EntityStateHolder.State.PROGRESS)
            setUpStorage()

            AssertUtil.assertActionSuccess(sendCommand(UploadPhotoControllerCommand.cancel(BUCKET_UID, entityStateHolder)))
            {
               !it.result.contains(entityStateHolder)
            }
         }

         it("Should return items list") {
            setUpStorage()

            AssertUtil.assertActionSuccess(sendCommand(UploadPhotoControllerCommand.fetch(BUCKET_UID)))
            {
               it.result.size == ENTITY_STATE_HOLDERS_COUNT
            }
         }
      }
   }

   override fun mockDaggerActionService() = CommandActionService()
         .wrapCache().bindStorageSet(setOf(storage))
         .wrapDagger()

   private fun sendCommand(command: UploadPhotoControllerCommand) =
         TestSubscriber<ActionState<UploadPhotoControllerCommand>>().apply {
            bucketInteractor.uploadControllerCommandPipe()
                  .createObservable(command)
                  .subscribe(this)
         }

   private fun mockBucketPhoto(uid: Int) = BucketPhoto().apply {
      this.uid = "$uid"
   }

   private fun setUpStorage() {
      val bundle: CacheBundle = spy()
      whenever(bundle.contains(any())).thenReturn(true)
      whenever(bundle.get<String>(any())).thenReturn(BUCKET_UID)
      storage.save(bundle, (1..ENTITY_STATE_HOLDERS_COUNT).map { EntityStateHolder.create(mockBucketPhoto(it), EntityStateHolder.State.PROGRESS) }.toList())
   }
}) {
   companion object {
      private const val BUCKET_UID = "test"
      private const val ENTITY_STATE_HOLDERS_COUNT = 3
   }
}
