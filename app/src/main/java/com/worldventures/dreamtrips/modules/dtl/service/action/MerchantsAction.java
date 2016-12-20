package com.worldventures.dreamtrips.modules.dtl.service.action;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.api.dtl.merchants.GetThinMerchantsHttpAction;
import com.worldventures.dreamtrips.core.api.action.CommandWithError;
import com.worldventures.dreamtrips.core.janet.JanetModule;
import com.worldventures.dreamtrips.core.janet.cache.CacheBundle;
import com.worldventures.dreamtrips.core.janet.cache.CacheBundleImpl;
import com.worldventures.dreamtrips.core.janet.cache.CacheOptions;
import com.worldventures.dreamtrips.core.janet.cache.CachedAction;
import com.worldventures.dreamtrips.core.janet.cache.ImmutableCacheOptions;
import com.worldventures.dreamtrips.core.janet.cache.storage.PaginatedStorage;
import com.worldventures.dreamtrips.core.janet.dagger.InjectableAction;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.ThinMerchant;
import com.worldventures.dreamtrips.modules.dtl.service.action.bundle.MerchantsActionParams;
import com.worldventures.dreamtrips.modules.dtl.service.action.creator.MerchantsActionCreator;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import io.techery.janet.ActionHolder;
import io.techery.janet.Janet;
import io.techery.janet.command.annotations.CommandAction;
import io.techery.mappery.MapperyContext;
import rx.schedulers.Schedulers;

@CommandAction
public class MerchantsAction extends CommandWithError<List<ThinMerchant>>
      implements CachedAction<List<ThinMerchant>>, InjectableAction, NewRelicTrackableAction {

   @Inject @Named(JanetModule.JANET_API_LIB) Janet janet;
   @Inject MapperyContext mapperyContext;
   @Inject MerchantsActionCreator actionCreator;

   private final long startTime = System.currentTimeMillis();

   private final boolean isRefresh;
   private final MerchantsActionParams actionParams;

   private List<ThinMerchant> cache = new ArrayList<>();

   public static MerchantsAction create(MerchantsActionParams params) {
      return new MerchantsAction(params);
   }

   public MerchantsAction(MerchantsActionParams params) {
      this.actionParams = params;
      this.isRefresh = MerchantsActionCreator.calculateOffsetPagination(params.filterData()) == 0;
   }

   @Override
   protected void run(CommandCallback<List<ThinMerchant>> callback) throws Throwable {
      callback.onProgress(0);
      janet.createPipe(GetThinMerchantsHttpAction.class, Schedulers.io())
            .createObservableResult(actionCreator.createAction(actionParams))
            .map(GetThinMerchantsHttpAction::merchants)
            .map(merchants -> mapperyContext.convert(merchants, ThinMerchant.class))
            .doOnNext(action -> clearCacheIfNeeded())
            .subscribe(callback::onSuccess, callback::onFail);
   }

   public boolean isRefresh() {
      return isRefresh;
   }

   public MerchantsActionParams bundle() {
      return actionParams;
   }

   @Override
   public int getFallbackErrorMessage() {
      return R.string.dtl_load_error;
   }

   @Override
   public long getMetricStart() {
      return startTime;
   }

   @Override
   public List<ThinMerchant> getCacheData() {
      return new ArrayList<>(getResult());
   }

   @Override
   public void onRestore(ActionHolder holder, List<ThinMerchant> cache) {
      this.cache = new ArrayList<>(cache);
   }

   @Override
   public CacheOptions getCacheOptions() {
      CacheBundle cacheBundle = new CacheBundleImpl();
      cacheBundle.put(PaginatedStorage.BUNDLE_REFRESH, isRefresh);
      return ImmutableCacheOptions.builder().params(cacheBundle).build();
   }

   private void clearCacheIfNeeded() {
      if (isRefresh()) cache = null;
   }

   public List<ThinMerchant> merchants() {
      List<ThinMerchant> merchants = new ArrayList<>();
      if (cache != null) merchants.addAll(cache);
      if (getResult() != null) merchants.addAll(getResult());
      return merchants;
   }
}
