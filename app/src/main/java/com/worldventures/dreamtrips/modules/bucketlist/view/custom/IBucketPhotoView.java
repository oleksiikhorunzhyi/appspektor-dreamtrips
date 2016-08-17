package com.worldventures.dreamtrips.modules.bucketlist.view.custom;

import com.worldventures.dreamtrips.modules.bucketlist.model.BucketPhoto;
import com.worldventures.dreamtrips.modules.bucketlist.service.model.EntityStateHolder;

import java.util.List;

public interface IBucketPhotoView {
   void addItemInProgressState(EntityStateHolder<BucketPhoto> photoStateHolder);

   void changeItemState(EntityStateHolder<BucketPhoto> photoEntityStateHolder);

   void removeItem(EntityStateHolder<BucketPhoto> photo);

   void setImages(List<EntityStateHolder<BucketPhoto>> images);
}