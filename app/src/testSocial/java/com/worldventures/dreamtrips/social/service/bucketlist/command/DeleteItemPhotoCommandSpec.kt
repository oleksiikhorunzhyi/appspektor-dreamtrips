package com.worldventures.dreamtrips.social.service.bucketlist.command

import com.worldventures.core.test.AssertUtil
import com.worldventures.dreamtrips.social.common.base.BaseBodySpec
import com.worldventures.dreamtrips.social.ui.bucketlist.model.BucketPhoto
import com.worldventures.dreamtrips.social.ui.bucketlist.service.command.DeleteItemPhotoCommand
import io.techery.janet.ActionState
import io.techery.janet.http.test.MockHttpActionService
import org.jetbrains.spek.api.dsl.Spec
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import rx.observers.TestSubscriber

class DeleteItemPhotoCommandSpec : BaseBodySpec(object : BaseBucketListCommandTestBody() {

   override fun create(): Spec.() -> Unit = {
      describe("Success while executing command") {
         beforeEachTest { setup(mockHttpServiceForSuccess()) }

         it("Should remove BucketPhoto") {
            val testSubscriber = TestSubscriber<ActionState<DeleteItemPhotoCommand>>()
            val bucketPhoto = mockBucketPhoto("2")

            bucketInteractor.deleteItemPhotoPipe()
                  .createObservable(DeleteItemPhotoCommand(bucketItem, bucketPhoto))
                  .subscribe(testSubscriber)
            AssertUtil.assertActionSuccess(testSubscriber) { it.result.photos.none { it.uid == bucketPhoto.uid } }
         }

         it("Should remove cover photo and set first photo as cover") {
            val testSubscriber = TestSubscriber<ActionState<DeleteItemPhotoCommand>>()
            val deletedPhoto = bucketItem.coverPhoto
            bucketInteractor.deleteItemPhotoPipe()
                  .createObservable(DeleteItemPhotoCommand(bucketItem, deletedPhoto))
                  .subscribe(testSubscriber)

            AssertUtil.assertActionSuccess(testSubscriber) {
               it.result.photos.none { deletedPhoto.uid == it.uid }
                     && it.result.coverPhoto.uid == it.result.firstPhoto.uid
            }
         }
      }

      describe("Error while executing command") {
         it("Should throw error") {
            setup(mockHttpServiceForError())
            val testSubscriber = TestSubscriber<ActionState<DeleteItemPhotoCommand>>()

            bucketInteractor.deleteItemPhotoPipe()
                  .createObservable(DeleteItemPhotoCommand(bucketItem, mockBucketPhoto("1")))
                  .subscribe(testSubscriber)

            AssertUtil.assertActionFail(testSubscriber) { it != null }
         }
      }
   }

   override fun mockBucketItem(uid: String) = super.mockBucketItem(uid).apply {
      photos.addAll((1..3).map { mockBucketPhoto("$it") })
      coverPhoto = photos[0]
   }

   fun mockBucketPhoto(uid: String) = BucketPhoto().apply {
      this.uid = uid
   }

   override fun mockHttpServiceForSuccess() = MockHttpActionService.Builder()
         .bind(MockHttpActionService.Response(200)) {
            it.url.contains("/api/bucket_list_items/")
                  && it.url.contains("photos/")
         }
         .build()

   override fun mockHttpServiceForError() = MockHttpActionService.Builder()
         .bind(MockHttpActionService.Response(400)) {
            it.url.contains("/api/bucket_list_items/")
                  && it.url.contains("photos/")
         }
         .build()
})
