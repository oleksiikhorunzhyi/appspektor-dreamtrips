package com.worldventures.dreamtrips.modules.dtl.service.action;

import android.location.Location;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.api.dtl.merchants.ThinMerchantsHttpAction;
import com.worldventures.dreamtrips.core.api.action.CommandWithError;
import com.worldventures.dreamtrips.core.janet.JanetModule;
import com.worldventures.dreamtrips.core.janet.dagger.InjectableAction;
import com.worldventures.dreamtrips.modules.dtl.model.location.DtlLocation;
import com.worldventures.dreamtrips.modules.dtl.model.mapping.ThinMerchantsTransformer;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.ThinMerchant;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.filter.FilterData;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.inject.Inject;
import javax.inject.Named;

import io.techery.janet.Janet;
import io.techery.janet.command.annotations.CommandAction;
import rx.schedulers.Schedulers;

@CommandAction
public class ThinMerchantsCommand extends CommandWithError<List<ThinMerchant>>
      implements InjectableAction, NewRelicTrackableAction {

   private final long startTime = System.currentTimeMillis();

   @Inject @Named(JanetModule.JANET_API_LIB) Janet janet;

   private List<ThinMerchant> cache = new ArrayList<>();

   private final Location coordinates;

   public static ThinMerchantsCommand create(FilterData filterData, DtlLocation dtlLocation) {
      return create(dtlLocation); // TODO :: 26.09.16
   }

   public static ThinMerchantsCommand create(DtlLocation dtlLocation) {
      return create(dtlLocation.getCoordinates().asAndroidLocation());
   }

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

   @Override
   public long getMetricStart() {
      return startTime;
   }
}
