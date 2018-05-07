package com.worldventures.dreamtrips.social.ui.bucketlist.service.command;

import com.innahema.collections.query.queriables.Queryable;
import com.worldventures.core.model.EntityStateHolder;
import com.worldventures.dreamtrips.social.ui.bucketlist.model.BucketPhoto;
import com.worldventures.janet.cache.CacheBundle;
import com.worldventures.janet.cache.CacheBundleImpl;
import com.worldventures.janet.cache.CacheOptions;
import com.worldventures.janet.cache.CachedAction;

import java.util.List;

import io.techery.janet.ActionHolder;
import io.techery.janet.Command;
import io.techery.janet.command.annotations.CommandAction;
import rx.functions.Func1;

import static com.worldventures.dreamtrips.social.ui.bucketlist.service.storage.UploadBucketPhotoInMemoryStorage.BUCKET_ID_PARAM;

@CommandAction
public final class UploadPhotoControllerCommand extends Command<List<EntityStateHolder<BucketPhoto>>> implements CachedAction<List<EntityStateHolder<BucketPhoto>>> {

   private final String bucketId;
   private final Func1<List<EntityStateHolder<BucketPhoto>>, List<EntityStateHolder<BucketPhoto>>> actionFunc;
   private List<EntityStateHolder<BucketPhoto>> listOfPhotoHolders;

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
      return new CacheOptions(true, true, true, bundle);
   }

   private static class FetchFunc implements Func1<List<EntityStateHolder<BucketPhoto>>, List<EntityStateHolder<BucketPhoto>>> {
      @Override
      public List<EntityStateHolder<BucketPhoto>> call(List<EntityStateHolder<BucketPhoto>> list) {
         return list;
      }
   }

   private static class CreateInProcessFunc implements Func1<List<EntityStateHolder<BucketPhoto>>, List<EntityStateHolder<BucketPhoto>>> {

      private final EntityStateHolder<BucketPhoto> photoEntityStateHolder;

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
               list.set(list.indexOf(Queryable.from(list)
                     .firstOrDefault(item -> item.entity()
                           .equals(photoEntityStateHolder.entity()))), photoEntityStateHolder);
               break;
            case DONE:
               list.remove(Queryable.from(list)
                     .firstOrDefault(item -> item.entity().equals(photoEntityStateHolder.entity())));
               break;
            default:
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

      private final EntityStateHolder<BucketPhoto> photoEntityStateHolder;

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
