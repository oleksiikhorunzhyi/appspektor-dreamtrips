package com.worldventures.dreamtrips.modules.dtl.service.action;

import android.location.Location;

import com.worldventures.dreamtrips.api.dtl.merchants.MerchantsHttpAction;
import com.worldventures.dreamtrips.api.dtl.merchants.model.MerchantType;
import com.worldventures.dreamtrips.api.dtl.merchants.model.PartnerStatus;
import com.worldventures.dreamtrips.core.janet.JanetModule;
import com.worldventures.dreamtrips.core.janet.cache.CacheOptions;
import com.worldventures.dreamtrips.core.janet.cache.CachedAction;
import com.worldventures.dreamtrips.core.janet.cache.ImmutableCacheOptions;
import com.worldventures.dreamtrips.core.janet.dagger.InjectableAction;
import com.worldventures.dreamtrips.modules.dtl.model.mapping.MerchantTransformer;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.DtlMerchant;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.inject.Inject;
import javax.inject.Named;

import io.techery.janet.ActionHolder;
import io.techery.janet.Command;
import io.techery.janet.Janet;
import io.techery.janet.command.annotations.CommandAction;
import rx.Observable;

@CommandAction
public class DtlMerchantsAction extends Command<List<DtlMerchant>> implements CachedAction<List<DtlMerchant>>, InjectableAction {

   @Inject @Named(JanetModule.JANET_API_LIB) Janet janet;

   private List<DtlMerchant> cache = new ArrayList<>();

   private final Location location;

   private final long startTime = System.currentTimeMillis();

   private DtlMerchantsAction(Location location) {
      this.location = location;
   }

   @Override
   protected void run(CommandCallback<List<DtlMerchant>> callback) throws Throwable {
      if (location != null) {
         String ll = String.format(Locale.US, "%1$f,%2$f", location.getLatitude(), location.getLongitude());
         janet.createPipe(MerchantsHttpAction.class)
               .createObservableResult(new MerchantsHttpAction(ll))
               .map(MerchantsHttpAction::merchants)
               .flatMap(Observable::from)
               .compose(new MerchantTransformer())
               .filter(merchant -> merchant.getPartnerStatus() != PartnerStatus.UNKNOWN)
               .filter(merchant -> merchant.getType() != MerchantType.UNKNOWN)
               .toList()
               .subscribe(callback::onSuccess, callback::onFail);
      } else {
         callback.onSuccess(cache);
      }
   }

   public boolean isFromApi() {
      return location != null;
   }

   public long getStartTime() {
      return startTime;
   }

   public static DtlMerchantsAction load(Location location) {
      return new DtlMerchantsAction(location);
   }

   public static DtlMerchantsAction restore() {
      return new DtlMerchantsAction(null);
   }

   @Override
   public List<DtlMerchant> getCacheData() {
      return getResult();
   }

   @Override
   public void onRestore(ActionHolder holder, List<DtlMerchant> cache) {
      this.cache = cache;
   }

   @Override
   public CacheOptions getCacheOptions() {
      return ImmutableCacheOptions.builder().restoreFromCache(!isFromApi()).saveToCache(isFromApi()).build();
   }
}
