package com.worldventures.dreamtrips.social.ui.bucketlist.presenter

import com.worldventures.dreamtrips.social.ui.bucketlist.model.BucketItem

fun stubBucketItem(isDone: Boolean = false): BucketItem {
   val bucket = BucketItem()
   bucket.uid = Math.random().toString()
   bucket.isDone = isDone
   bucket.type = BucketItem.BucketType.ACTIVITY.toString().toLowerCase()
   bucket.photos = emptyList()
   return bucket
}