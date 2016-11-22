package com.worldventures.dreamtrips.modules.bucketlist.service.command;

import android.support.annotation.NonNull;
import android.support.v4.util.Pair;
import android.text.TextUtils;

import com.innahema.collections.query.queriables.Queryable;
import com.techery.spares.session.SessionHolder;
import com.worldventures.dreamtrips.api.bucketlist.GetBucketItemsForUserHttpAction;
import com.worldventures.dreamtrips.api.bucketlist.ImmutableGetBucketItemsForUserHttpAction;
import com.worldventures.dreamtrips.core.janet.JanetModule;
import com.worldventures.dreamtrips.core.janet.cache.CacheBundle;
import com.worldventures.dreamtrips.core.janet.cache.CacheBundleImpl;
import com.worldventures.dreamtrips.core.janet.cache.CacheOptions;
import com.worldventures.dreamtrips.core.janet.cache.CachedAction;
import com.worldventures.dreamtrips.core.janet.cache.ImmutableCacheOptions;
import com.worldventures.dreamtrips.core.janet.dagger.InjectableAction;
import com.worldventures.dreamtrips.core.session.UserSession;
import com.worldventures.dreamtrips.modules.bucketlist.model.BucketItem;
import com.worldventures.dreamtrips.modules.bucketlist.service.BucketInteractor;
import com.worldventures.dreamtrips.modules.bucketlist.service.common.BucketUtility;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import io.techery.janet.ActionHolder;
import io.techery.janet.Command;
import io.techery.janet.Janet;
import io.techery.janet.command.annotations.CommandAction;
import io.techery.mappery.MapperyContext;
import rx.Observable;
import rx.functions.Func2;

import static com.worldventures.dreamtrips.modules.bucketlist.service.storage.BucketListDiskStorage.USER_ID_EXTRA;

@CommandAction
public class BucketListCommand extends Command<List<BucketItem>> implements InjectableAction, CachedAction<List<BucketItem>> {

   public static final int USER_ID_WAS_NOT_PROVIDED = -1;

   @Inject BucketInteractor bucketInteractor;
   @Inject SessionHolder<UserSession> sessionHolder;
   @Inject @Named(JanetModule.JANET_API_LIB) Janet janet;
   @Inject MapperyContext mapperyContext;

   private Func2<BucketInteractor, List<BucketItem>, Observable<List<BucketItem>>> operationFunc;

   private List<BucketItem> cachedItems = new ArrayList<>();

   private boolean force;
   private boolean isFromCache;
   private int userId = USER_ID_WAS_NOT_PROVIDED;

   public static BucketListCommand fetch(boolean force) {
      return fetch(-1, force);
   }

   public static BucketListCommand fetch(int userId, boolean force) {
      return new BucketListCommand(userId, force);
   }

   public static BucketListCommand createItem(BucketItem item) {
      return new BucketListCommand(new CreateItemFunc(item));
   }

   public static BucketListCommand updateItem(BucketItem item) {
      return new BucketListCommand(new UpdateItemFunc(item));
   }

   public static BucketListCommand deleteItem(String bucketItemId) {
      return new BucketListCommand(new DeleteItemFunc(bucketItemId));
   }

   public static BucketListCommand move(int from, int to, @NonNull BucketItem.BucketType bucketType) {
      return new BucketListCommand(new MoveItemFunc(from, to, bucketType));
   }

   private BucketListCommand(Func2<BucketInteractor, List<BucketItem>, Observable<List<BucketItem>>> operationFunc) {
      this.operationFunc = operationFunc;

   }

   public BucketListCommand(int userId, boolean force) {
      this(StubOperationFunc.INSTANCE);
      this.userId = userId;
      this.force = force;
   }

   @Override
   protected void run(CommandCallback<List<BucketItem>> callback) throws Throwable {
      Observable<List<BucketItem>> networkObservable = janet.createPipe(GetBucketItemsForUserHttpAction.class)
            .createObservableResult(
                  new GetBucketItemsForUserHttpAction(ImmutableGetBucketItemsForUserHttpAction
                        .Params.of(userId)))
            .map(action ->  mapperyContext.convert(action.response(), BucketItem.class));

      if (force) {
         networkObservable.subscribe(callback::onSuccess, callback::onFail);
         return;
      }

      Observable.concat(Observable.just(cachedItems)
            .flatMap(bucketItems -> operationFunc.call(bucketInteractor, bucketItems))
            .filter(bucketItems -> !bucketItems.isEmpty()), networkObservable)
            .take(1)
            .subscribe(callback::onSuccess, callback::onFail);
   }

   @Override
   public List<BucketItem> getCacheData() {
      return getResult();
   }

   @Override
   public void onRestore(ActionHolder holder, List<BucketItem> cache) {
      cachedItems = cache;
      isFromCache = true;
   }

   @Override
   public CacheOptions getCacheOptions() {
      CacheBundle bundle = new CacheBundleImpl();
      bundle.put(USER_ID_EXTRA, userId());

      return ImmutableCacheOptions.builder().params(bundle).build();
   }

   public boolean isFromCache() {
      return isFromCache;
   }

   /////////////////////////
   /// Operation actions
   /////////////////////////
   private static final class StubOperationFunc implements Func2<BucketInteractor, List<BucketItem>, Observable<List<BucketItem>>> {
      static final Func2<BucketInteractor, List<BucketItem>, Observable<List<BucketItem>>> INSTANCE = new StubOperationFunc();

      @Override
      public Observable<List<BucketItem>> call(BucketInteractor interactor, List<BucketItem> bucketItems) {
         return Observable.just(bucketItems);
      }
   }

   private static abstract class ItemTransformer implements Func2<BucketInteractor, List<BucketItem>, Observable<List<BucketItem>>> {
      protected BucketItem item;

      ItemTransformer(@NonNull BucketItem item) {
         this.item = item;
      }
   }

   private static class CreateItemFunc extends ItemTransformer {
      CreateItemFunc(@NonNull BucketItem itemToAdd) {
         super(itemToAdd);
      }

      @Override
      public Observable<List<BucketItem>> call(BucketInteractor interactor, List<BucketItem> bucketItems) {
         bucketItems.add(0, item);
         return Observable.just(bucketItems);
      }
   }

   private static class UpdateItemFunc extends ItemTransformer {
      UpdateItemFunc(@NonNull BucketItem item) {
         super(item);
      }

      @Override
      public Observable<List<BucketItem>> call(BucketInteractor interactor, List<BucketItem> bucketItems) {
         BucketItem oldItem = Queryable.from(bucketItems).filter((element, index) -> element.getUid().equals(item.getUid())).firstOrDefault();
         int oldPosition = bucketItems.indexOf(oldItem);
         int newPosition = (oldItem.isDone() && !item.isDone()) ? 0 : oldPosition;

         bucketItems.remove(oldPosition);
         bucketItems.add(newPosition, item);

         if (item.getOwner() == null) {
            item.setOwner(oldItem.getOwner());
         }

         return Observable.just(bucketItems);
      }
   }

   private static class DeleteItemFunc implements Func2<BucketInteractor, List<BucketItem>, Observable<List<BucketItem>>> {
      private String uid;

      DeleteItemFunc(String uid) {
         this.uid = uid;
      }

      @Override
      public Observable<List<BucketItem>> call(BucketInteractor interactor, List<BucketItem> bucketItems) {
         if (TextUtils.isEmpty(uid)) {
            throw new AssertionError("Bucket id is empty");
         }

         bucketItems.remove(findBucketItemById(bucketItems, uid));
         return Observable.just(bucketItems);
      }

      private BucketItem findBucketItemById(List<BucketItem> source, String bucketId) {
         return Queryable.from(source).firstOrDefault(element -> element.getUid().equalsIgnoreCase(bucketId));
      }
   }

   private static class MoveItemFunc implements Func2<BucketInteractor, List<BucketItem>, Observable<List<BucketItem>>> {
      private int from, to;
      private BucketItem.BucketType type;

      MoveItemFunc(int from, int to, BucketItem.BucketType type) {
         this.from = from;
         this.to = to;
         this.type = type;
      }

      @Override
      public Observable<List<BucketItem>> call(BucketInteractor interactor, List<BucketItem> bucketItems) {
         if (interactor == null) {
            throw new AssertionError("BucketInteractor == null");
         }

         return Observable.just(bucketItems)
               .compose(BucketUtility.disJoinByType(type))
               .flatMap(listOfItems -> interactor.movePipe()
                     .createObservableResult(new ChangeBucketListOrderCommand(listOfItems.get(from).getUid(), to))
                     .map(changeOrderAction -> {
                        BucketItem fromItem = listOfItems.get(from);
                        BucketItem toItem = listOfItems.get(to);

                        return new Pair<>(fromItem, toItem);
                     }))
               .map(movedItemsPair -> {
                  swapItems(bucketItems, movedItemsPair.first, movedItemsPair.second);
                  return bucketItems;
               });
      }

      private void swapItems(@NonNull List<BucketItem> bucketItemList, @NonNull BucketItem fromItem, @NonNull BucketItem toItem) {
         int index = bucketItemList.indexOf(toItem);

         bucketItemList.remove(fromItem);
         bucketItemList.add(index, fromItem);
      }
   }

   /////////////////////////
   /// Common
   ////////////////////////
   private int userId() {
      return userId == -1 ? sessionHolder.get().get().getUser().getId() : userId;
   }
}