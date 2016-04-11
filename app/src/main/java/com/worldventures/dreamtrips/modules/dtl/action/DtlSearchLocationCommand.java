package com.worldventures.dreamtrips.modules.dtl.action;

import com.worldventures.dreamtrips.core.api.DtlApi;
import com.worldventures.dreamtrips.modules.dtl.model.location.DtlExternalLocation;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;

import io.techery.janet.command.annotations.CommandAction;

@CommandAction
public class DtlSearchLocationCommand extends CallableCommand<List<DtlExternalLocation>> {

    private final String query;

    private final boolean fromApi;

    private DtlSearchLocationCommand(Callable<List<DtlExternalLocation>> callable, String query, boolean fromApi) {
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

    public static DtlSearchLocationCommand createEmpty(String query) {
        return createWith(Collections.emptyList(), query);
    }

    public static DtlSearchLocationCommand createWith(List<DtlExternalLocation> locations, String query) {
        return new DtlSearchLocationCommand(() -> locations, query, false);
    }

    public static DtlSearchLocationCommand createApiSearch(DtlApi dtlApi, String apiQuery, String query) {
        return new DtlSearchLocationCommand(() -> dtlApi.searchLocations(apiQuery), query, true);
    }
}
