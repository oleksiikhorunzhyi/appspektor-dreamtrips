package com.worldventures.dreamtrips.modules.dtl.service.action;

import com.innahema.collections.query.queriables.Queryable;
import com.worldventures.dreamtrips.core.janet.cache.CacheOptions;
import com.worldventures.dreamtrips.core.janet.cache.CachedAction;
import com.worldventures.dreamtrips.core.janet.cache.ImmutableCacheOptions;
import com.worldventures.dreamtrips.core.janet.dagger.InjectableAction;
import com.worldventures.dreamtrips.core.repository.SnappyRepository;
import com.worldventures.dreamtrips.modules.dtl.model.DistanceType;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.DtlMerchantAttribute;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.filter.DtlFilterData;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.filter.DtlFilterParameters;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.filter.ImmutableDtlFilterData;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.filter.ImmutableDtlFilterParameters;
import com.worldventures.dreamtrips.modules.settings.model.Setting;
import com.worldventures.dreamtrips.modules.settings.util.SettingsFactory;

import java.util.List;

import javax.inject.Inject;

import io.techery.janet.ActionHolder;
import io.techery.janet.Command;
import io.techery.janet.command.annotations.CommandAction;
import rx.functions.Func2;

@CommandAction
public class DtlFilterDataAction extends Command<DtlFilterData> implements CachedAction<DtlFilterData>, InjectableAction {

    @Inject
    SnappyRepository db;

    private DtlFilterData data;
    private final Func2<SnappyRepository, DtlFilterData, DtlFilterData> updateFunc;

    private DtlFilterDataAction(Func2<SnappyRepository, DtlFilterData, DtlFilterData> updateFunc) {
        this(ImmutableDtlFilterData.builder().build(), updateFunc);
    }

    private DtlFilterDataAction(DtlFilterData data, Func2<SnappyRepository, DtlFilterData, DtlFilterData> updateFunc) {
        this.data = data;
        this.updateFunc = updateFunc;
    }

    @Override
    protected void run(CommandCallback<DtlFilterData> callback) throws Throwable {
        callback.onSuccess(updateFunc.call(db, data));
    }

    ///////////////////////////////////////////////////////////////////////////
    // Actions
    ///////////////////////////////////////////////////////////////////////////

    public static DtlFilterDataAction init() {
        return new DtlFilterDataAction((db, data) -> {
            Setting distanceSetting = Queryable.from(db.getSettings()).filter(setting ->
                    setting.getName().equals(SettingsFactory.DISTANCE_UNITS)).firstOrDefault();
            return ImmutableDtlFilterData.copyOf(data)
                    .withDistanceType(DistanceType.provideFromSetting(distanceSetting))
                    .withIsOffersOnly(db.getLastSelectedOffersOnlyToggle());
        });
    }

    public static DtlFilterDataAction reset() {
        return new DtlFilterDataAction((db, data) ->
                DtlFilterData.merge(
                        ImmutableDtlFilterParameters.builder()
                                .selectedAmenities(db.getAmenities())
                                .build(),
                        data)
        );
    }

    public static DtlFilterDataAction amenitiesUpdate(List<DtlMerchantAttribute> amenities) {
        return new DtlFilterDataAction((db, data) ->
                DtlFilterData.merge(
                        ImmutableDtlFilterParameters.builder()
                                .selectedAmenities(amenities)
                                .build(),
                        ImmutableDtlFilterData.copyOf(data)
                                .withAmenities(amenities)
                                .withSelectedAmenities(amenities)));
    }

    public static DtlFilterDataAction applyParams(DtlFilterParameters filterParameters) {
        return new DtlFilterDataAction((db, data) -> {
            data = DtlFilterData.merge(filterParameters, data);
//            TrackingHelper.dtlMerchantFilter(data);
            return data;
        });
    }

    public static DtlFilterDataAction applySearch(String query) {
        return new DtlFilterDataAction((db, data) ->
                ImmutableDtlFilterData.copyOf(data)
                        .withSearchQuery(query)
        );
    }

    public static DtlFilterDataAction applyOffersOnly(boolean offersOnly) {
        return new DtlFilterDataAction((db, data) -> {
            db.saveLastSelectedOffersOnlyToogle(offersOnly);
            return ImmutableDtlFilterData.copyOf(data)
                    .withIsOffersOnly(offersOnly);
        });
    }

    ///////////////////////////////////////////////////////////////////////////
    // Caching
    ///////////////////////////////////////////////////////////////////////////

    @Override
    public DtlFilterData getCacheData() {
        return getResult();
    }

    @Override
    public void onRestore(ActionHolder holder, DtlFilterData cache) {
        this.data = cache;
    }

    @Override
    public CacheOptions getCacheOptions() {
        return ImmutableCacheOptions.builder()
                .build();
    }

}
