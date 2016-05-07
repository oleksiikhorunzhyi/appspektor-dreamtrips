package com.worldventures.dreamtrips.modules.dtl.action;

import com.worldventures.dreamtrips.core.api.DtlApi;
import com.worldventures.dreamtrips.core.api.action.CallableCommandAction;
import com.worldventures.dreamtrips.core.janet.cache.CacheOptions;
import com.worldventures.dreamtrips.core.janet.cache.CachedAction;
import com.worldventures.dreamtrips.core.janet.cache.ImmutableCacheOptions;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.DtlMerchant;

import java.util.Collections;
import java.util.List;

import io.techery.janet.ActionHolder;
import io.techery.janet.command.annotations.CommandAction;

@CommandAction
public class DtlMerchantsAction extends CallableCommandAction<List<DtlMerchant>> implements CachedAction<List<DtlMerchant>> {

    private boolean restoreFromCache, fromApi;

    private DtlMerchantsAction(DtlApi dtlApi, String location) {
        super(() -> dtlApi.getNearbyDtlMerchants(location));
        fromApi = true;
    }

    private DtlMerchantsAction() {
        super(Collections::emptyList);
        restoreFromCache = true;
    }

    private DtlMerchantsAction(List<DtlMerchant> merchants) {
        super(() -> merchants);
    }

    public boolean isFromApi() {
        return fromApi;
    }

    public static DtlMerchantsAction fromApi(DtlApi dtlApi, String location) {
        return new DtlMerchantsAction(dtlApi, location);
    }

    public static DtlMerchantsAction fromCache() {
        return new DtlMerchantsAction();
    }

    @Override
    public List<DtlMerchant> getData() {
        return getResult();
    }

    @Override
    public void onRestore(ActionHolder holder, List<DtlMerchant> cache) {
        holder.newAction(new DtlMerchantsAction(cache));
    }

    @Override
    public CacheOptions getOptions() {
        return ImmutableCacheOptions.builder()
                .restoreFromCache(restoreFromCache)
                .build();
    }
}
