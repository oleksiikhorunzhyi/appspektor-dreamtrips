package com.worldventures.dreamtrips.social.service.bucketlist.command

import com.worldventures.core.test.AssertUtil
import com.worldventures.dreamtrips.api.bucketlist.model.ImmutableBucketCoverPhoto
import com.worldventures.dreamtrips.api.bucketlist.model.ImmutableBucketListActivity
import com.worldventures.dreamtrips.api.bucketlist.model.ImmutableBucketListDining
import com.worldventures.dreamtrips.api.bucketlist.model.ImmutableBucketListLocation
import com.worldventures.dreamtrips.social.common.base.BaseBodySpec
import com.worldventures.dreamtrips.social.ui.bucketlist.model.BucketItem
import com.worldventures.dreamtrips.social.ui.bucketlist.model.converter.PopularBucketItemFromActivityConverter
import com.worldventures.dreamtrips.social.ui.bucketlist.model.converter.PopularBucketItemFromDinningConverter
import com.worldventures.dreamtrips.social.ui.bucketlist.model.converter.PopularBucketItemFromLocationConverter
import com.worldventures.dreamtrips.social.ui.bucketlist.service.command.GetPopularBucketItemsCommand
import io.techery.janet.ActionState
import io.techery.janet.http.test.MockHttpActionService
import org.jetbrains.spek.api.dsl.Spec
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import rx.observers.TestSubscriber
import java.util.Random

class GetPopularBucketItemsCommandSpec : BaseBodySpec(object : BaseBucketListCommandTestBody() {

   override fun create(): Spec.() -> Unit = {
      describe("Success while executing command") {
         beforeEachTest { setup(mockHttpServiceForSuccess()) }

         it("Should receive popular bucket items from \"LOCATION\" bucket list activity") {
            AssertUtil.assertActionSuccess(sendCommand(BucketItem.BucketType.LOCATION)) {
               it.result.size == COUNT_OF_LOCATION_ITEMS
            }
         }

         it("Should receive  popular bucket items from \"ACTIVITY\" bucket list activity") {
            AssertUtil.assertActionSuccess(sendCommand(BucketItem.BucketType.ACTIVITY)) {
               it.result.size == COUNT_OF_ACTIVITY_ITEMS
            }
         }

         it("Should receive  popular bucket items from \"DINING\" bucket list activity") {
            AssertUtil.assertActionSuccess(sendCommand(BucketItem.BucketType.DINING)) {
               it.result.size == COUNT_OF_DINING_ITEMS
            }
         }
      }

      describe("Error while executing command") {
         it("Should throw exception") {
            setup(mockHttpServiceForError())
            AssertUtil.assertActionFail(sendCommand(BucketItem.BucketType.values().let {
               it[Random().nextInt(it.size - 1)]
            })) { it != null }
         }
      }
   }

   private fun sendCommand(type: BucketItem.BucketType) = TestSubscriber<ActionState<GetPopularBucketItemsCommand>>()
         .apply {
            bucketInteractor.popularBucketItemsPipe
                  .createObservable(GetPopularBucketItemsCommand(type))
                  .subscribe(this)
         }

   override fun mockHttpServiceForSuccess() = MockHttpActionService.Builder()
         .bind(MockHttpActionService.Response(200).body((1..COUNT_OF_LOCATION_ITEMS).map { mockBucketListLocation(it) }.toList())) {
            it.url.contains("/api/bucket_list/locations")
         }
         .bind(MockHttpActionService.Response(200).body((1..COUNT_OF_ACTIVITY_ITEMS).map { mockBucketListActivity(it) }.toList())) {
            it.url.contains("/api/bucket_list/activities")
         }
         .bind(MockHttpActionService.Response(200).body((1..COUNT_OF_DINING_ITEMS).map { mockBucketListDinning(it) }.toList())) {
            it.url.contains("/api/bucket_list/dinings")
         }
         .build()

   override fun mockHttpServiceForError() = MockHttpActionService.Builder()
         .bind(MockHttpActionService.Response(400)) {
            it.url.contains("/api/bucket_list/locations")
         }
         .bind(MockHttpActionService.Response(400)) {
            it.url.contains("/api/bucket_list/activities")
         }
         .bind(MockHttpActionService.Response(400)) {
            it.url.contains("/api/bucket_list/dinings")
         }
         .build()

   override fun constructConverters() = super.constructConverters().toMutableList().apply {
      addAll(listOf(
            castConverter(PopularBucketItemFromDinningConverter()),
            castConverter(PopularBucketItemFromActivityConverter()),
            castConverter(PopularBucketItemFromLocationConverter())
      ))
   }

   private fun mockBucketListLocation(id: Int) = ImmutableBucketListLocation.builder().id(id)
         .coverPhoto(mockBucketCoverPhoto(id))
         .name("Test $id").build()

   private fun mockBucketListActivity(id: Int) = ImmutableBucketListActivity.builder().id(id)
         .coverPhoto(mockBucketCoverPhoto(id))
         .name("Test $id").build()

   private fun mockBucketListDinning(id: Int) = ImmutableBucketListDining.builder().id(id)
         .coverPhoto(mockBucketCoverPhoto(id))
         .name("Test $id").build()

   private fun mockBucketCoverPhoto(uid: Int) = ImmutableBucketCoverPhoto.builder()
         .id(uid).uid("$uid").url("test").build()
}) {
   companion object {
      private const val COUNT_OF_LOCATION_ITEMS = 5
      private const val COUNT_OF_ACTIVITY_ITEMS = 4
      private const val COUNT_OF_DINING_ITEMS = 3
   }
}
