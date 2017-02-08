package com.worldventures.dreamtrips.modules.dtl.service.action;

import android.util.Log;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.api.action.CommandWithError;
import com.worldventures.dreamtrips.core.janet.JanetModule;
import com.worldventures.dreamtrips.core.janet.cache.CacheBundle;
import com.worldventures.dreamtrips.core.janet.cache.CacheBundleImpl;
import com.worldventures.dreamtrips.core.janet.cache.CacheOptions;
import com.worldventures.dreamtrips.core.janet.cache.CachedAction;
import com.worldventures.dreamtrips.core.janet.cache.ImmutableCacheOptions;
import com.worldventures.dreamtrips.core.janet.cache.storage.PaginatedStorage;
import com.worldventures.dreamtrips.core.janet.dagger.InjectableAction;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.reviews.ReviewsMerchant;
import com.worldventures.dreamtrips.modules.dtl.service.action.bundle.MerchantsActionParams;
import com.worldventures.dreamtrips.modules.dtl.service.action.creator.ReviewsActionCreator;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Named;
import io.techery.janet.ActionHolder;
import io.techery.janet.Command;
import io.techery.janet.Janet;
import io.techery.janet.command.annotations.CommandAction;
import io.techery.mappery.MapperyContext;
import rx.schedulers.Schedulers;

@CommandAction
public class ReviewMerchantsAction extends CommandWithError<ReviewsMerchant>
      implements CachedAction<ReviewsMerchant>, InjectableAction, NewRelicTrackableAction {

   @Inject @Named(JanetModule.JANET_API_LIB) Janet janet;
   @Inject MapperyContext mapperyContext;
   @Inject ReviewsActionCreator reviewsActionCreator;

   private final long startTime = System.currentTimeMillis();
   private final boolean isRefresh;
   private List<ReviewsMerchant> cache = new ArrayList<>();

   public static ReviewMerchantsAction create(MerchantsActionParams params) {
      return new ReviewMerchantsAction(params);
   }

   public ReviewMerchantsAction(MerchantsActionParams params) {
      this.isRefresh = false;//ReviewsActionCreator.calculateOffsetPagination(params.filterData()) == 0;
   }

   @Override
   public int getFallbackErrorMessage() {
      return R.string.dtl_load_error;
   }

   @Override
   public ReviewsMerchant getCacheData() {
      return getResult();
   }

   @Override
   public void onRestore(ActionHolder holder, ReviewsMerchant cache) {

   }

   @Override
   public CacheOptions getCacheOptions() {
      CacheBundle cacheBundle = new CacheBundleImpl();
      cacheBundle.put(PaginatedStorage.BUNDLE_REFRESH, isRefresh);
      return ImmutableCacheOptions.builder().params(cacheBundle).build();
   }

   @Override
   public long getMetricStart() {
      return startTime;
   }

   @Override
   protected void run(CommandCallback<ReviewsMerchant> callback) throws Throwable {
      callback.onProgress(0);
      janet.createPipe(GetReviewsMerchantsHttpAction.class, Schedulers.io())
            .createObservableResult(reviewsActionCreator.createAction(/*actionParams*/null))
            .map(GetReviewsMerchantsHttpAction::reviews)
            //.map(reviews -> mapperyContext.convert(reviews, ReviewsMerchant.class))
            .doOnNext(sizeReviews -> logReview(sizeReviews))
            .subscribe(callback::onSuccess, callback::onFail);
   }

   public boolean isRefresh() {
      return isRefresh;
   }

   private void clearCacheIfNeeded() {
      if (isRefresh()) cache = null;
   }

   private void logReview(ReviewsMerchant reviewsMerchant) {
      if (reviewsMerchant != null) {
         Log.i("---->", "Size of reviews --> " + reviewsMerchant.getReviews().size());
      }
   }
}
