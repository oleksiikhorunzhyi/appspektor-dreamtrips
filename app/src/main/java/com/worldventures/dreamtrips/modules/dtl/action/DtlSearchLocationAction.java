package com.worldventures.dreamtrips.modules.dtl.action;

import com.worldventures.dreamtrips.core.api.DtlApi;
import com.worldventures.dreamtrips.core.api.action.CallableCommandAction;
import com.worldventures.dreamtrips.modules.dtl.model.location.DtlExternalLocation;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;

import io.techery.janet.command.annotations.CommandAction;

@CommandAction
public class DtlSearchLocationAction extends CallableCommandAction<List<DtlExternalLocation>> {

    private final String query;

    private final boolean fromApi;

    private DtlSearchLocationAction(Callable<List<DtlExternalLocation>> callable, String query, boolean fromApi) {
        super(callable);
        this.query = query;
        this.fromApi = fromApi;
    }

    public String getQuery() {
        return query;
    }

    public boolean isFromApi() {
        return fromApi;
    }

    public static DtlSearchLocationAction createEmpty(String query) {
        return createWith(Collections.emptyList(), query);
    }

    public static DtlSearchLocationAction createWith(List<DtlExternalLocation> locations, String query) {
        return new DtlSearchLocationAction(() -> locations, query, false);
    }

    public static DtlSearchLocationAction createApiSearch(DtlApi dtlApi, String apiQuery, String query) {
        return new DtlSearchLocationAction(() -> dtlApi.searchLocations(apiQuery), query, true);
    }
}
