package com.worldventures.dreamtrips.modules.dtl.action;

import com.worldventures.dreamtrips.core.api.action.CallableCommandAction;
import com.worldventures.dreamtrips.core.janet.cache.CacheOptions;
import com.worldventures.dreamtrips.core.janet.cache.CachedAction;
import com.worldventures.dreamtrips.core.janet.cache.ImmutableCacheOptions;
import com.worldventures.dreamtrips.core.repository.SnappyRepository;
import com.worldventures.dreamtrips.modules.dtl.model.LocationSourceType;
import com.worldventures.dreamtrips.modules.dtl.model.location.DtlLocation;

import io.techery.janet.ActionHolder;
import io.techery.janet.command.annotations.CommandAction;

@CommandAction
public class DtlLocationCommand extends CallableCommandAction<DtlLocation> implements CachedAction<DtlLocation> {

    private boolean fromDB;

    public DtlLocationCommand(SnappyRepository db) {
        super(db::getDtlLocation);
        fromDB = true;
    }

    public boolean isResultDefined() {
        return getResult() != null && getResult().getLocationSourceType() != LocationSourceType.UNDEFINED;
    }

    public DtlLocationCommand(DtlLocation location) {
        super(() -> location);
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
                .restoreFromCache(fromDB)
                .build();
    }
}
