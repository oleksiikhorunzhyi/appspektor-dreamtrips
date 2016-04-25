package com.worldventures.dreamtrips.modules.dtl.action;

import com.worldventures.dreamtrips.core.janet.cache.CacheOptions;
import com.worldventures.dreamtrips.core.janet.cache.CachedAction;
import com.worldventures.dreamtrips.core.janet.cache.ImmutableCacheOptions;
import com.worldventures.dreamtrips.core.repository.SnappyRepository;
import com.worldventures.dreamtrips.modules.dtl.model.LocationSourceType;
import com.worldventures.dreamtrips.modules.dtl.model.location.DtlLocation;

import java.util.ArrayList;
import java.util.List;

import io.techery.janet.ActionHolder;
import io.techery.janet.command.annotations.CommandAction;

@CommandAction
public class DtlLocationCommand extends CallableCommand<DtlLocation> implements CachedAction<DtlLocation> {

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
    public List<DtlLocation> getData() {
        return new ArrayList<DtlLocation>() {{
            if (getResult() != null) {
                add(getResult());
            }
        }};
    }

    @Override
    public void onRestore(ActionHolder holder, List<DtlLocation> cache) {
        DtlLocation location = cache.get(0);
        if (location.getLocationSourceType() != LocationSourceType.UNDEFINED) {
            holder.newAction(new DtlLocationCommand(location));
        }
    }

    @Override
    public CacheOptions getOptions() {
        return ImmutableCacheOptions.builder()
                .restoreFromCache(fromDB)
                .build();
    }
}
