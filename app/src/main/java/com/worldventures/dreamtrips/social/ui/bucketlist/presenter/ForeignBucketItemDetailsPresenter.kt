package com.worldventures.dreamtrips.social.ui.bucketlist.presenter

import com.worldventures.dreamtrips.social.ui.bucketlist.model.BucketItem

class ForeignBucketItemDetailsPresenter(type: BucketItem.BucketType, bucketItem: BucketItem, ownerId: Int) :
      BucketItemDetailsPresenter(type, bucketItem, ownerId)
