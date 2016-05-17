package com.worldventures.dreamtrips.modules.dtl.action;

import com.worldventures.dreamtrips.core.api.action.AuthorizedHttpAction;
import com.worldventures.dreamtrips.core.janet.cache.CacheOptions;
import com.worldventures.dreamtrips.core.janet.cache.CachedAction;
import com.worldventures.dreamtrips.core.janet.cache.ImmutableCacheOptions;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.DtlMerchant;

import java.util.List;

import io.techery.janet.ActionHolder;
import io.techery.janet.http.annotations.HttpAction;
import io.techery.janet.http.annotations.Query;
import io.techery.janet.http.annotations.Response;

@HttpAction("/api/dtl/v2/merchants")
public class DtlMerchantsAction extends AuthorizedHttpAction implements CachedAction<List<DtlMerchant>> {

    @Query("ll")
    String ll;

    @Response
    List<DtlMerchant> merchants;

    private boolean fromCache;

    private DtlMerchantsAction(String location) {
        this.ll = location;
    }

    private DtlMerchantsAction() {
        fromCache = true;
    }

    public boolean isFromApi() {
        return !fromCache;
    }

    public static DtlMerchantsAction fromApi(String location) {
        return new DtlMerchantsAction(location);
    }

    public static DtlMerchantsAction fromCache() {
        return new DtlMerchantsAction();
    }

    @Override
    public List<DtlMerchant> getCacheData() {
        return this.merchants;
    }

    @Override
    public void onRestore(ActionHolder holder, List<DtlMerchant> cache) {
        this.merchants = cache;
    }

    @Override
    public CacheOptions getCacheOptions() {
        return ImmutableCacheOptions.builder()
                .restoreFromCache(fromCache)
                .sendAfterRestore(false)
                .build();
    }
}
