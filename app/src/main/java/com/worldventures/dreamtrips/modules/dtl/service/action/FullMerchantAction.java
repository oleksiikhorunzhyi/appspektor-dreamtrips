package com.worldventures.dreamtrips.modules.dtl.service.action;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.api.dtl.merchants.GetMerchantByIdHttpAction;
import com.worldventures.dreamtrips.core.api.action.CommandWithError;
import com.worldventures.dreamtrips.core.janet.cache.CacheBundle;
import com.worldventures.dreamtrips.core.janet.cache.CacheBundleImpl;
import com.worldventures.dreamtrips.core.janet.cache.CacheOptions;
import com.worldventures.dreamtrips.core.janet.cache.CachedAction;
import com.worldventures.dreamtrips.core.janet.cache.ImmutableCacheOptions;
import com.worldventures.dreamtrips.core.janet.dagger.InjectableAction;
import com.worldventures.dreamtrips.modules.dtl.domain.converter.MerchantDistancePatcher;
import com.worldventures.dreamtrips.modules.dtl.domain.storage.FullMerchantStorage;
import com.worldventures.dreamtrips.modules.dtl.model.location.DtlLocation;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.Merchant;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.reviews.ReviewSummary;
import com.worldventures.dreamtrips.modules.dtl.service.action.creator.FullMerchantActionCreator;

import javax.inject.Inject;

import io.techery.janet.ActionHolder;
import io.techery.janet.Janet;
import io.techery.janet.command.annotations.CommandAction;
import io.techery.mappery.MapperyContext;
import rx.schedulers.Schedulers;

@CommandAction
public class FullMerchantAction extends CommandWithError<Merchant> implements InjectableAction, CachedAction<Merchant> {

   @Inject Janet janet;
   @Inject MapperyContext mapperyContext;
   @Inject FullMerchantActionCreator actionCreator;

   private final String offerId;
   private final String merchantId;
   private final DtlLocation dtlLocation;
   private final boolean fromRating;
   private final ReviewSummary review;

   private Merchant cache;

   public static FullMerchantAction create(String merchantId) {
      return create(merchantId, null);
   }

   public static FullMerchantAction create(String merchantId, DtlLocation dtlLocation) {
      return create(merchantId, null, dtlLocation, false);
   }

   public static FullMerchantAction create(String merchantId, String offerId, DtlLocation dtlLocation, boolean fromRating) {
      return create(merchantId, offerId, dtlLocation, fromRating, null);
   }

   public static FullMerchantAction create(String merchantId, String offerId, DtlLocation dtlLocation, boolean fromRating, ReviewSummary review) {
      return new FullMerchantAction(merchantId, offerId, dtlLocation, fromRating, review);
   }

   public FullMerchantAction(String merchantId, String offerId, DtlLocation dtlLocation, boolean fromRating, ReviewSummary review) {
      this.merchantId = merchantId;
      this.offerId = offerId;
      this.dtlLocation = dtlLocation;
      this.fromRating = fromRating;
      this.review = review;
   }

   @Override
   protected void run(CommandCallback<Merchant> callback) throws Throwable {
      if (cache == null || needToForce()) {
         requestMerchant(callback);
      } else {
         callback.onSuccess(cache);
      }
   }

   private boolean needToForce() {
      return review != null &&
            (!review.ratingAverage().equals(cache.reviews().ratingAverage())
                  || !review.total().equals(cache.reviews().total()));
   }

   private void requestMerchant(CommandCallback<Merchant> callback) {
      callback.onProgress(0);
      janet.createPipe(GetMerchantByIdHttpAction.class, Schedulers.io())
            .createObservableResult(new GetMerchantByIdHttpAction(merchantId))
            .map(GetMerchantByIdHttpAction::merchant)
            .map(merchant -> mapperyContext.convert(merchant, Merchant.class))
            .map(MerchantDistancePatcher.create(dtlLocation))
            .subscribe(callback::onSuccess, callback::onFail);
   }

   public String getOfferId() {
      return offerId;
   }

   public String getMerchantId() {
      return merchantId;
   }

   public boolean getFromRating() {
      return fromRating;
   }

   @Override
   public int getFallbackErrorMessage() {
      return R.string.dtl_load_error;
   }

   @Override
   public Merchant getCacheData() {
      return getResult();
   }

   @Override
   public void onRestore(ActionHolder holder, Merchant cache) {
      this.cache = cache;
   }

   @Override
   public CacheOptions getCacheOptions() {
      CacheBundle bundle = new CacheBundleImpl();
      bundle.put(FullMerchantStorage.BUNDLE_MERCHANT_ID, merchantId);
      return ImmutableCacheOptions.builder().sendAfterRestore(cache == null).params(bundle).build();
   }
}
