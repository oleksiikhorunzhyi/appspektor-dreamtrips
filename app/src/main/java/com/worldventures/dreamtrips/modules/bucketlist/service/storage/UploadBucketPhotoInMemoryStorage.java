package com.worldventures.dreamtrips.modules.bucketlist.service.storage;

import android.support.annotation.Nullable;
import android.support.v4.util.ArrayMap;

import com.worldventures.dreamtrips.core.janet.cache.CacheBundle;
import com.worldventures.dreamtrips.core.janet.cache.CachedAction;
import com.worldventures.dreamtrips.core.janet.cache.storage.ActionStorage;
import com.worldventures.dreamtrips.core.janet.cache.storage.ClearableStorage;
import com.worldventures.dreamtrips.modules.bucketlist.model.BucketPhoto;
import com.worldventures.dreamtrips.modules.bucketlist.service.command.UploadPhotoControllerCommand;
import com.worldventures.dreamtrips.modules.common.model.EntityStateHolder;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class UploadBucketPhotoInMemoryStorage implements ActionStorage<List<EntityStateHolder<BucketPhoto>>>,
      ClearableStorage {
   public static final String BUCKET_ID_PARAM = "bucket_id";

   private Map<String, List<EntityStateHolder<BucketPhoto>>> mapOfBucketPhoto = new ArrayMap<>();

   @Override
   public synchronized void save(@Nullable CacheBundle params, List<EntityStateHolder<BucketPhoto>> listOfPhotoEntityStateHolder) {
      checkBundle(params);
      String bucketUid = params.get(BUCKET_ID_PARAM);

      mapOfBucketPhoto.put(bucketUid, listOfPhotoEntityStateHolder);
   }

   @Override
   public synchronized List<EntityStateHolder<BucketPhoto>> get(@Nullable CacheBundle params) {
      checkBundle(params);
      String bucketUid = params.get(BUCKET_ID_PARAM);

      List<EntityStateHolder<BucketPhoto>> list = mapOfBucketPhoto.get(bucketUid);
      return list == null ? new ArrayList<>() : list;
   }

   @Override
   public Class<? extends CachedAction> getActionClass() {
      return UploadPhotoControllerCommand.class;
   }

   private void checkBundle(@Nullable CacheBundle params) {
      if (params == null) {
         throw new AssertionError("CacheBundle is null");
      }
      if (!params.contains(BUCKET_ID_PARAM)) {
         throw new AssertionError("bucketId key was not found");
      }
   }

   @Override
   public void clearMemory() {
      mapOfBucketPhoto.clear();
   }
}
