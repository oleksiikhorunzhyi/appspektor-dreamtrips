package com.worldventures.dreamtrips.modules.dtl.service.action;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.api.dtl.merchants.ThinMerchantsHttpAction;
import com.worldventures.dreamtrips.api.dtl.merchants.model.ImmutableThinMerchantsActionParams;
import com.worldventures.dreamtrips.api.dtl.merchants.model.ThinMerchantsActionParams;
import com.worldventures.dreamtrips.core.api.action.CommandWithError;
import com.worldventures.dreamtrips.core.janet.JanetModule;
import com.worldventures.dreamtrips.core.janet.dagger.InjectableAction;
import com.worldventures.dreamtrips.modules.dtl.helper.FilterHelper;
import com.worldventures.dreamtrips.modules.dtl.model.location.DtlLocation;
import com.worldventures.dreamtrips.modules.dtl.model.mapping.ThinMerchantsTransformer;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.ThinMerchant;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.filter.FilterData;
import com.worldventures.dreamtrips.modules.trips.model.Location;


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

   private final ThinMerchantsActionParams params;

   public static ThinMerchantsCommand create(FilterData filterData, DtlLocation dtlLocation) {
      final ThinMerchantsActionParams params = buildParams(filterData, dtlLocation);
      return new ThinMerchantsCommand(params);
   }

   public ThinMerchantsCommand (ThinMerchantsActionParams params) {
      this.params = params;
   }

   @Override
   protected void run(CommandCallback<List<ThinMerchant>> callback) throws Throwable {
         janet.createPipe(ThinMerchantsHttpAction.class, Schedulers.io())
               .createObservableResult(new ThinMerchantsHttpAction(params))
               .map(ThinMerchantsHttpAction::merchants)
               .compose(ThinMerchantsTransformer.INSTANCE)
               .subscribe(callback::onSuccess, callback::onFail);
   }


   private static String provideFormattedLocation(DtlLocation location) {
      final Location coordinates = location.getCoordinates();
      return String.format(Locale.US, "%1$f,%2$f", coordinates.getLat(), coordinates.getLng());
   }

   private static ThinMerchantsActionParams buildParams(FilterData filterData, DtlLocation dtlLocation) {
      final String coordinates = provideFormattedLocation(dtlLocation);
      return ImmutableThinMerchantsActionParams.builder()
            .coordinates(coordinates)
            .radius(FilterHelper.provideDistanceByIndex(filterData.distanceType(), filterData.distanceMaxIndex()))
            .limit(FilterData.LIMIT)
            .offset(filterData.page() * FilterData.LIMIT)
            .build();
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
