package com.worldventures.dreamtrips.social.service.bucketlist.command

import com.nhaarman.mockito_kotlin.spy
import com.nhaarman.mockito_kotlin.whenever
import com.worldventures.core.test.AssertUtil
import com.worldventures.dreamtrips.BaseSpec.Companion.any
import com.worldventures.dreamtrips.BaseSpec.Companion.bindStorageSet
import com.worldventures.dreamtrips.BaseSpec.Companion.wrapCache
import com.worldventures.dreamtrips.BaseSpec.Companion.wrapDagger
import com.worldventures.dreamtrips.social.common.base.BaseBodySpec
import com.worldventures.dreamtrips.social.ui.bucketlist.model.BucketItem
import com.worldventures.dreamtrips.social.ui.bucketlist.service.command.RecentlyAddedBucketsFromPopularCommand
import com.worldventures.dreamtrips.social.ui.bucketlist.service.storage.RecentlyAddedBucketItemStorage
import com.worldventures.janet.cache.CacheBundle
import io.techery.janet.ActionState
import io.techery.janet.CommandActionService
import org.jetbrains.spek.api.dsl.Spec
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import rx.observers.TestSubscriber
import java.util.Random

class RecentlyAddedBucketsFromPopularCommandSpec : BaseBodySpec(object : BaseBucketListCommandTestBody() {

   private val bucketItems get() = (1..3).map { mockBucketItem("$it") }

   private val bucketItemType = BucketItem.BucketType.values().let {
      it[Random().nextInt(it.size - 1)]
   }

   private val storage
      get() = RecentlyAddedBucketItemStorage().apply {
         val bundle: CacheBundle = spy()
         whenever(bundle.contains(any())).thenReturn(true)
         whenever(bundle.get<BucketItem.BucketType>(any())).thenReturn(bucketItemType)
         save(bundle, bucketItems)
      }

   override fun create(): Spec.() -> Unit = {
      describe("Success while executing command") {
         beforeEachTest { setup() }

         it("Should add item") {
            AssertUtil.assertActionSuccess(sendCommand(RecentlyAddedBucketsFromPopularCommand.add(
                  bucketItem.apply { this.type = bucketItemType.getName() })
            )) {
               it.result.second?.contains(bucketItem)
            }
         }

         it("Should clear items list") {
            AssertUtil.assertActionSuccess(sendCommand(RecentlyAddedBucketsFromPopularCommand.clear(bucketItemType)))
            {
               it.result.second?.isEmpty()
            }
         }

         it("Should return bucket list without changes") {
            AssertUtil.assertActionSuccess(sendCommand(RecentlyAddedBucketsFromPopularCommand.get(bucketItemType)))
            {
               it.result.second?.size == bucketItems.size
            }
         }
      }
   }

   override fun mockDaggerActionService() = CommandActionService()
         .wrapCache().bindStorageSet(setOf(storage))
         .wrapDagger()

   private fun sendCommand(command: RecentlyAddedBucketsFromPopularCommand) =
         TestSubscriber<ActionState<RecentlyAddedBucketsFromPopularCommand>>().apply {
            bucketInteractor.recentlyAddedBucketsFromPopularCommandPipe()
                  .createObservable(command)
                  .subscribe(this)
         }
})
