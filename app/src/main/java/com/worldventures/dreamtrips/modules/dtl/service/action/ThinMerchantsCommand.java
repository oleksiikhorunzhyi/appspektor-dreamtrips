package com.worldventures.dreamtrips.modules.dtl.service.action;

import android.location.Location;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.api.dtl.merchants.ThinMerchantsHttpAction;
import com.worldventures.dreamtrips.core.api.action.CommandWithError;
import com.worldventures.dreamtrips.core.janet.JanetModule;
import com.worldventures.dreamtrips.core.janet.cache.CacheOptions;
import com.worldventures.dreamtrips.core.janet.cache.CachedAction;
import com.worldventures.dreamtrips.core.janet.cache.ImmutableCacheOptions;
import com.worldventures.dreamtrips.core.janet.dagger.InjectableAction;
import com.worldventures.dreamtrips.modules.dtl.model.mapping.ThinMerchantsTransformer;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.ThinMerchant;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.inject.Inject;
import javax.inject.Named;

import io.techery.janet.ActionHolder;
import io.techery.janet.Janet;
import io.techery.janet.command.annotations.CommandAction;
import rx.schedulers.Schedulers;

@CommandAction
public class ThinMerchantsCommand extends CommandWithError<List<ThinMerchant>> implements CachedAction<List<ThinMerchant>>, InjectableAction {

   @Inject @Named(JanetModule.JANET_API_LIB) Janet janet;

   private List<ThinMerchant> cache = new ArrayList<>();

   private final Location coordinates;

   public static ThinMerchantsCommand create(Location coordinates) {
      return new ThinMerchantsCommand(coordinates);
   }

   public ThinMerchantsCommand(Location coordinates) {
      this.coordinates = coordinates;
   }

   @Override
   protected void run(CommandCallback<List<ThinMerchant>> callback) throws Throwable {
      if (coordinates != null) {
         callback.onProgress(0);
         String ll = String.format(Locale.US, "%1$f,%2$f", coordinates.getLatitude(), coordinates.getLongitude());
         janet.createPipe(ThinMerchantsHttpAction.class, Schedulers.io())
               .createObservableResult(new ThinMerchantsHttpAction(ll))
               .map(ThinMerchantsHttpAction::merchants)
               .compose(ThinMerchantsTransformer.INSTANCE)
               .subscribe(callback::onSuccess, callback::onFail);
      } else callback.onSuccess(cache);
   }

   @Override
   public int getFallbackErrorMessage() {
      return R.string.dtl_load_merchant_error;
   }

   public boolean isFromApi() {
      return coordinates != null;
   }

   public static ThinMerchantsCommand load(Location location) {
      return new ThinMerchantsCommand(location);
   }

   public static ThinMerchantsCommand restore() {
      return new ThinMerchantsCommand(null);
   }

   @Override
   public List<ThinMerchant> getCacheData() {
      return getResult();
   }

   @Override
   public void onRestore(ActionHolder holder, List<ThinMerchant> cache) {
      this.cache = cache;
   }

   @Override
   public CacheOptions getCacheOptions() {
      return ImmutableCacheOptions.builder().restoreFromCache(!isFromApi()).saveToCache(isFromApi()).build();
   }
}
