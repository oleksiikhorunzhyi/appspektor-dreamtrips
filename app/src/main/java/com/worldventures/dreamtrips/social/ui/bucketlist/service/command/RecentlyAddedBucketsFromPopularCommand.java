package com.worldventures.dreamtrips.social.ui.bucketlist.service.command;

import android.support.v4.util.Pair;

import com.worldventures.dreamtrips.social.ui.bucketlist.model.BucketItem;
import com.worldventures.janet.cache.CacheBundle;
import com.worldventures.janet.cache.CacheBundleImpl;
import com.worldventures.janet.cache.CacheOptions;
import com.worldventures.janet.cache.CachedAction;

import java.util.List;

import io.techery.janet.ActionHolder;
import io.techery.janet.Command;
import io.techery.janet.command.annotations.CommandAction;
import rx.functions.Func1;

import static com.worldventures.dreamtrips.social.ui.bucketlist.model.BucketItem.BucketType;

@CommandAction
public final class RecentlyAddedBucketsFromPopularCommand extends Command<Pair<BucketType, List<BucketItem>>> implements CachedAction<List<BucketItem>> {

   public static final String BUCKET_TYPE_EXTRA = "bucket_type_extra";

   private final BucketType bucketType;
   private final Func1<List<BucketItem>, List<BucketItem>> func;
   private List<BucketItem> recentPopularBucketListByType;

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

      return new CacheOptions(true, true, true, bundle);
   }

   private static final class AddFunc implements Func1<List<BucketItem>, List<BucketItem>> {

      private final BucketItem item;

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
