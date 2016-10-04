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

import java.util.List;
import java.util.Locale;

import javax.inject.Inject;
import javax.inject.Named;

import io.techery.janet.Janet;
import io.techery.janet.command.annotations.CommandAction;
import rx.schedulers.Schedulers;

@CommandAction
public class MerchantsAction extends CommandWithError<List<ThinMerchant>>
      implements InjectableAction, NewRelicTrackableAction {

   private final long startTime = System.currentTimeMillis();

   @Inject @Named(JanetModule.JANET_API_LIB) Janet janet;

   private final ThinMerchantsActionParams params;
   private final boolean isRefresh;

   public static MerchantsAction create(FilterData filterData, DtlLocation dtlLocation) {
      final ThinMerchantsActionParams params = buildParams(filterData, dtlLocation);
      return new MerchantsAction(params);
   }

   public MerchantsAction(ThinMerchantsActionParams params) {
      this.params = params;
      this.isRefresh = params.offset() == 0;
   }

   @Override
   protected void run(CommandCallback<List<ThinMerchant>> callback) throws Throwable {
      callback.onProgress(0);
      janet.createPipe(ThinMerchantsHttpAction.class, Schedulers.io())
            .createObservableResult(new ThinMerchantsHttpAction(params))
            .map(ThinMerchantsHttpAction::merchants)
            .compose(ThinMerchantsTransformer.INSTANCE)
            .subscribe(callback::onSuccess, callback::onFail);
   }

   private static ThinMerchantsActionParams buildParams(FilterData filterData, DtlLocation dtlLocation) {
      final String coordinates = provideFormattedLocation(dtlLocation);
      return ImmutableThinMerchantsActionParams.builder()
            .radius(FilterHelper.provideDistanceByIndex(filterData.distanceType(), filterData.distanceMaxIndex()))
            .coordinates(coordinates)
            .limit(FilterData.LIMIT)
            .offset(filterData.page() * FilterData.LIMIT)
            .build();
   }

   private static String provideFormattedLocation(DtlLocation location) {
      final Location coordinates = location.getCoordinates();
      return String.format(Locale.US, "%1$f,%2$f", coordinates.getLat(), coordinates.getLng());
   }

   public boolean isRefresh() {
      return isRefresh;
   }

   @Override
   public int getFallbackErrorMessage() {
      return R.string.dtl_load_error;
   }

   @Override
   public long getMetricStart() {
      return startTime;
   }
}
