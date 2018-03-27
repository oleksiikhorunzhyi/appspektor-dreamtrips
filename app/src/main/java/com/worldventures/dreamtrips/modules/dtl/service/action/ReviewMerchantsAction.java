package com.worldventures.dreamtrips.modules.dtl.service.action;

import com.worldventures.core.janet.CommandWithError;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.api.dtl.merchants.GetReviewsMerchantsHttpAction;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.reviews.Reviews;
import com.worldventures.dreamtrips.modules.dtl.service.action.bundle.ReviewsMerchantsActionParams;
import com.worldventures.dreamtrips.modules.dtl.service.action.creator.ReviewsActionCreator;
import com.worldventures.janet.cache.CacheBundle;
import com.worldventures.janet.cache.CacheBundleImpl;
import com.worldventures.janet.cache.CacheOptions;
import com.worldventures.janet.cache.CachedAction;
import com.worldventures.janet.cache.storage.PaginatedStorage;
import com.worldventures.janet.injection.InjectableAction;

import javax.inject.Inject;

import io.techery.janet.ActionHolder;
import io.techery.janet.Janet;
import io.techery.janet.command.annotations.CommandAction;
import io.techery.mappery.MapperyContext;
import rx.schedulers.Schedulers;

@CommandAction
public class ReviewMerchantsAction extends CommandWithError<Reviews>
      implements CachedAction<Reviews>, InjectableAction, NewRelicTrackableAction {

   @Inject Janet janet;
   @Inject MapperyContext mapperyContext;
   @Inject ReviewsActionCreator reviewsActionCreator;

   private final long startTime = System.currentTimeMillis();
   private final boolean isRefresh;
   private final ReviewsMerchantsActionParams actionParams;

   public static ReviewMerchantsAction create(ReviewsMerchantsActionParams params) {
      return new ReviewMerchantsAction(params);
   }

   public ReviewMerchantsAction(ReviewsMerchantsActionParams params) {
      this.actionParams = params;
      this.isRefresh = false;
   }

   @Override
   public int getFallbackErrorMessage() {
      return R.string.dtl_load_error;
   }

   @Override
   public Reviews getCacheData() {
      return getResult();
   }

   @Override
   public void onRestore(ActionHolder holder, Reviews cache) {

   }

   @Override
   public CacheOptions getCacheOptions() {
      CacheBundle cacheBundle = new CacheBundleImpl();
      cacheBundle.put(PaginatedStorage.BUNDLE_REFRESH, isRefresh);
      return new CacheOptions(true, true, true, cacheBundle);
   }

   @Override
   public long getMetricStart() {
      return startTime;
   }

   @Override
   protected void run(CommandCallback<Reviews> callback) throws Throwable {
      callback.onProgress(0);
      janet.createPipe(GetReviewsMerchantsHttpAction.class, Schedulers.io())
            .createObservableResult(reviewsActionCreator.createAction(actionParams))
            .map(GetReviewsMerchantsHttpAction::response)
            .map(reviews -> mapperyContext.convert(reviews, Reviews.class))
            .subscribe(callback::onSuccess, callback::onFail);
   }

   public boolean isRefresh() {
      return isRefresh;
   }
}
