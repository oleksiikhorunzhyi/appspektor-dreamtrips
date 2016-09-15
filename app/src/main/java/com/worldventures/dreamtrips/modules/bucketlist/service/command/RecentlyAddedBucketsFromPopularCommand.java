package com.worldventures.dreamtrips.modules.bucketlist.service.command;

import android.support.v4.util.Pair;

import com.worldventures.dreamtrips.core.janet.cache.CacheBundle;
import com.worldventures.dreamtrips.core.janet.cache.CacheBundleImpl;
import com.worldventures.dreamtrips.core.janet.cache.CacheOptions;
import com.worldventures.dreamtrips.core.janet.cache.CachedAction;
import com.worldventures.dreamtrips.core.janet.cache.ImmutableCacheOptions;
import com.worldventures.dreamtrips.modules.bucketlist.model.BucketItem;

import java.util.List;

import io.techery.janet.ActionHolder;
import io.techery.janet.Command;
import io.techery.janet.command.annotations.CommandAction;
import rx.functions.Func1;

import static com.worldventures.dreamtrips.modules.bucketlist.model.BucketItem.BucketType;

@CommandAction
public class RecentlyAddedBucketsFromPopularCommand extends Command<Pair<BucketType, List<BucketItem>>> implements CachedAction<List<BucketItem>> {
   public static final String BUCKET_TYPE_EXTRA = "bucket_type_extra";

   private List<BucketItem> recentPopularBucketListByType;
   private BucketType bucketType;

   private Func1<List<BucketItem>, List<BucketItem>> func;

   public static RecentlyAddedBucketsFromPopularCommand add(BucketItem item) {
      return new RecentlyAddedBucketsFromPopularCommand(new AddFunc(item), BucketType.valueOf(item.getType()
            .toUpperCase()));
   }

   public static RecentlyAddedBucketsFromPopularCommand clear(BucketType type) {
      return new RecentlyAddedBucketsFromPopularCommand(new ClearFunc(), type);
   }

   public static RecentlyAddedBucketsFromPopularCommand get(BucketType type) {
      return new RecentlyAddedBucketsFromPopularCommand(new GetFunc(), type);
   }

   private RecentlyAddedBucketsFromPopularCommand(Func1<List<BucketItem>, List<BucketItem>> func, BucketType bucketType) {
      this.func = func;
      this.bucketType = bucketType;
   }

   @Override
   protected void run(CommandCallback<Pair<BucketType, List<BucketItem>>> callback) throws Throwable {
      try {
         callback.onSuccess(Pair.create(bucketType, func.call(recentPopularBucketListByType)));
      } catch (Throwable throwable) {
         callback.onFail(throwable);
      }
   }

   @Override
   public List<BucketItem> getCacheData() {
      return recentPopularBucketListByType;
   }

   @Override
   public void onRestore(ActionHolder holder, List<BucketItem> cache) {
      recentPopularBucketListByType = cache;
   }

   @Override
   public CacheOptions getCacheOptions() {
      CacheBundle bundle = new CacheBundleImpl();
      bundle.put(BUCKET_TYPE_EXTRA, bucketType);

      return ImmutableCacheOptions.builder().params(bundle).build();
   }

   private static final class AddFunc implements Func1<List<BucketItem>, List<BucketItem>> {
      private BucketItem item;

      public AddFunc(BucketItem item) {
         this.item = item;
      }

      @Override
      public List<BucketItem> call(List<BucketItem> bucketItems) {
         bucketItems.add(item);
         return bucketItems;
      }
   }

   private static final class ClearFunc implements Func1<List<BucketItem>, List<BucketItem>> {
      @Override
      public List<BucketItem> call(List<BucketItem> bucketItems) {
         bucketItems.clear();
         return bucketItems;
      }
   }

   public static final class GetFunc implements Func1<List<BucketItem>, List<BucketItem>> {
      @Override
      public List<BucketItem> call(List<BucketItem> bucketItems) {
         return bucketItems;
      }
   }
}