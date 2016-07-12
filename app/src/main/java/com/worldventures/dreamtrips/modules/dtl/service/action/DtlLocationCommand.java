package com.worldventures.dreamtrips.modules.dtl.service.action;

import com.worldventures.dreamtrips.core.api.action.ValueCommandAction;
import com.worldventures.dreamtrips.core.janet.cache.CacheOptions;
import com.worldventures.dreamtrips.core.janet.cache.CachedAction;
import com.worldventures.dreamtrips.core.janet.cache.ImmutableCacheOptions;
import com.worldventures.dreamtrips.modules.dtl.model.LocationSourceType;
import com.worldventures.dreamtrips.modules.dtl.model.location.DtlLocation;
import com.worldventures.dreamtrips.modules.dtl.model.location.DtlManualLocation;
import com.worldventures.dreamtrips.modules.dtl.model.location.ImmutableDtlManualLocation;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.DtlMerchant;

import io.techery.janet.ActionHolder;
import io.techery.janet.command.annotations.CommandAction;

@CommandAction
public class DtlLocationCommand extends ValueCommandAction<DtlLocation> implements CachedAction<DtlLocation> {

    private boolean fromCache;

    private DtlLocationCommand() {
        super(DtlLocation.UNDEFINED);
        fromCache = true;
    }

    private DtlLocationCommand(DtlLocation location) {
        super(location);
    }

    public static DtlLocationCommand last() {
        return new DtlLocationCommand();
    }

    public static DtlLocationCommand change(DtlLocation location) {
        return new DtlLocationCommand(location);
    }

    public boolean isResultDefined() {
        return getResult() != null && getResult().getLocationSourceType() != LocationSourceType.UNDEFINED;
    }

    ///////////////////////////////////////////////////////////////////////////
    // Caching
    ///////////////////////////////////////////////////////////////////////////

    @Override
    public DtlLocation getCacheData() {
        return getResult();
    }

    @Override
    public void onRestore(ActionHolder holder, DtlLocation cache) {
        if (cache.getLocationSourceType() != LocationSourceType.UNDEFINED) {
            holder.newAction(new DtlLocationCommand(cache));
        }
    }

    @Override
    public CacheOptions getCacheOptions() {
        return ImmutableCacheOptions.builder()
                .restoreFromCache(fromCache)
                .saveToCache(!fromCache)
                .build();
    }
}
