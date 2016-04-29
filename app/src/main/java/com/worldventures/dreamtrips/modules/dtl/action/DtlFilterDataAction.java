package com.worldventures.dreamtrips.modules.dtl.action;

import com.worldventures.dreamtrips.core.api.action.CallableCommandAction;
import com.worldventures.dreamtrips.core.janet.cache.CacheOptions;
import com.worldventures.dreamtrips.core.janet.cache.CachedAction;
import com.worldventures.dreamtrips.core.janet.cache.ImmutableCacheOptions;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.filter.DtlFilterData;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.filter.ImmutableDtlFilterData;

import java.util.ArrayList;
import java.util.List;

import io.techery.janet.ActionHolder;
import io.techery.janet.command.annotations.CommandAction;
import rx.functions.Func1;

@CommandAction
public class DtlFilterDataAction extends CallableCommandAction<DtlFilterData> implements CachedAction<DtlFilterData> {


    private final boolean updating;
    private final Func1<DtlFilterData, DtlFilterData> updateAction;

    private DtlFilterDataAction(Func1<DtlFilterData, DtlFilterData> updateAction, boolean updating) {
        this(ImmutableDtlFilterData.builder().build(), updateAction, updating);
    }

    private DtlFilterDataAction(DtlFilterData data, Func1<DtlFilterData, DtlFilterData> updateAction, boolean updating) {
        super(() -> updateAction != null ? updateAction.call(data) : data);
        this.updateAction = updateAction;
        this.updating = updating;
    }

    public boolean isUpdating() {
        return updating;
    }

    public static DtlFilterDataAction update(Func1<DtlFilterData, DtlFilterData> updateAction) {
        return new DtlFilterDataAction(updateAction, true);
    }

    public static DtlFilterDataAction get() {
        return new DtlFilterDataAction(null, false);
    }

    ///////////////////////////////////////////////////////////////////////////
    // Caching
    ///////////////////////////////////////////////////////////////////////////

    @Override
    public List<DtlFilterData> getData() {
        return new ArrayList<DtlFilterData>() {{
            if (getResult() != null) {
                add(getResult());
            }
        }};
    }

    @Override
    public void onRestore(ActionHolder holder, List<DtlFilterData> cache) {
        holder.newAction(new DtlFilterDataAction(cache.get(0), updateAction, updating));
    }

    @Override
    public CacheOptions getOptions() {
        return ImmutableCacheOptions.builder()
                .restoreFromCache(true)
                .build();
    }
}
