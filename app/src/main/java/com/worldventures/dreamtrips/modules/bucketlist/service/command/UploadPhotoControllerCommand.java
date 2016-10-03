package com.worldventures.dreamtrips.modules.bucketlist.service.command;

import com.worldventures.dreamtrips.core.janet.cache.CacheBundle;
import com.worldventures.dreamtrips.core.janet.cache.CacheBundleImpl;
import com.worldventures.dreamtrips.core.janet.cache.CacheOptions;
import com.worldventures.dreamtrips.core.janet.cache.CachedAction;
import com.worldventures.dreamtrips.core.janet.cache.ImmutableCacheOptions;
import com.worldventures.dreamtrips.modules.bucketlist.model.BucketPhoto;
import com.worldventures.dreamtrips.modules.common.model.EntityStateHolder;

import java.util.List;

import io.techery.janet.ActionHolder;
import io.techery.janet.Command;
import io.techery.janet.command.annotations.CommandAction;
import rx.functions.Func1;

import static com.worldventures.dreamtrips.modules.bucketlist.service.storage.UploadBucketPhotoInMemoryStorage.BUCKET_ID_PARAM;

@CommandAction
public class UploadPhotoControllerCommand extends Command<List<EntityStateHolder<BucketPhoto>>> implements CachedAction<List<EntityStateHolder<BucketPhoto>>> {
   private String bucketId;

   private List<EntityStateHolder<BucketPhoto>> listOfPhotoHolders;

   private Func1<List<EntityStateHolder<BucketPhoto>>, List<EntityStateHolder<BucketPhoto>>> actionFunc;

   public static UploadPhotoControllerCommand create(String bucketUid, EntityStateHolder<BucketPhoto> photoStateHolder) {
      return new UploadPhotoControllerCommand(bucketUid, new CreateInProcessFunc(photoStateHolder));
   }

   public static UploadPhotoControllerCommand cancel(String bucketUid, EntityStateHolder<BucketPhoto> photoEntityStateHolder) {
      return new UploadPhotoControllerCommand(bucketUid, new CancelFunc(photoEntityStateHolder));
   }

   public static UploadPhotoControllerCommand fetch(String bucketId) {
      return new UploadPhotoControllerCommand(bucketId, new FetchFunc());
   }

   private UploadPhotoControllerCommand(String bucketId, Func1<List<EntityStateHolder<BucketPhoto>>, List<EntityStateHolder<BucketPhoto>>> actionFunc) {
      this.bucketId = bucketId;
      this.actionFunc = actionFunc;
   }

   @Override
   protected void run(CommandCallback<List<EntityStateHolder<BucketPhoto>>> callback) throws Throwable {
      callback.onSuccess(actionFunc.call(listOfPhotoHolders));
   }

   @Override
   public List<EntityStateHolder<BucketPhoto>> getCacheData() {
      return listOfPhotoHolders;
   }

   @Override
   public void onRestore(ActionHolder holder, List<EntityStateHolder<BucketPhoto>> cache) {
      listOfPhotoHolders = cache;
   }

   @Override
   public CacheOptions getCacheOptions() {
      CacheBundle bundle = new CacheBundleImpl();
      bundle.put(BUCKET_ID_PARAM, bucketId);

      return ImmutableCacheOptions.builder().params(bundle).build();
   }

   private static class FetchFunc implements Func1<List<EntityStateHolder<BucketPhoto>>, List<EntityStateHolder<BucketPhoto>>> {
      @Override
      public List<EntityStateHolder<BucketPhoto>> call(List<EntityStateHolder<BucketPhoto>> list) {
         return list;
      }
   }

   private static class CreateInProcessFunc implements Func1<List<EntityStateHolder<BucketPhoto>>, List<EntityStateHolder<BucketPhoto>>> {
      private EntityStateHolder<BucketPhoto> photoEntityStateHolder;

      CreateInProcessFunc(EntityStateHolder<BucketPhoto> photoEntityStateHolder) {
         this.photoEntityStateHolder = photoEntityStateHolder;
      }

      @Override
      public List<EntityStateHolder<BucketPhoto>> call(List<EntityStateHolder<BucketPhoto>> list) {
         EntityStateHolder.State state = photoEntityStateHolder.state();

         switch (state) {
            case PROGRESS:
               addOrSetProgress(list);
               break;
            case FAIL:
               list.set(list.indexOf(photoEntityStateHolder), photoEntityStateHolder);
               break;
            case DONE:
               list.remove(photoEntityStateHolder);
               break;
         }

         return list;
      }

      private void addOrSetProgress(List<EntityStateHolder<BucketPhoto>> list) {
         int index = list.indexOf(photoEntityStateHolder);

         if (index > 0) {
            list.set(index, photoEntityStateHolder);
         } else {
            list.add(photoEntityStateHolder);
         }
      }
   }

   private static class CancelFunc implements Func1<List<EntityStateHolder<BucketPhoto>>, List<EntityStateHolder<BucketPhoto>>> {
      private EntityStateHolder<BucketPhoto> photoEntityStateHolder;

      CancelFunc(EntityStateHolder<BucketPhoto> photoEntityStateHolder) {
         this.photoEntityStateHolder = photoEntityStateHolder;
      }

      @Override
      public List<EntityStateHolder<BucketPhoto>> call(List<EntityStateHolder<BucketPhoto>> list) {
         list.remove(photoEntityStateHolder);
         return list;
      }
   }
}