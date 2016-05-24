package com.worldventures.dreamtrips.modules.dtl.service.action;


import android.support.v4.util.Pair;

import com.innahema.collections.query.queriables.Queryable;
import com.worldventures.dreamtrips.core.api.action.AuthorizedHttpAction;
import com.worldventures.dreamtrips.core.janet.cache.CacheOptions;
import com.worldventures.dreamtrips.core.janet.cache.CachedAction;
import com.worldventures.dreamtrips.core.janet.cache.ImmutableCacheOptions;
import com.worldventures.dreamtrips.modules.dtl.model.location.DtlExternalLocation;

import java.util.ArrayList;
import java.util.List;

import io.techery.janet.ActionHolder;
import io.techery.janet.http.annotations.HttpAction;
import io.techery.janet.http.annotations.Query;
import io.techery.janet.http.annotations.Response;

@HttpAction("/api/dtl/v2/locations")
public class DtlSearchLocationAction extends AuthorizedHttpAction implements CachedAction<Pair<String, List<DtlExternalLocation>>> {

    private static final int API_SEARCH_QUERY_LENGTH = 3;

    @Query("query")
    String apiQuery;

    @Response
    List<DtlExternalLocation> response = new ArrayList<>();

    private final String query;
    private boolean restored;
    private List<DtlExternalLocation> filteredResponse;

    public DtlSearchLocationAction(String query) {
        this.query = query;
        if (query.length() >= API_SEARCH_QUERY_LENGTH) {
            this.apiQuery = query.substring(0, API_SEARCH_QUERY_LENGTH).toLowerCase();
        }
    }

    public List<DtlExternalLocation> getResult() {
        if (filteredResponse == null) {
            filteredResponse = filter(response, query);
        }
        return filteredResponse;
    }

    public String getQuery() {
        return query;
    }

    @Override
    public Pair<String, List<DtlExternalLocation>> getCacheData() {
        return new Pair<>(apiQuery, response);
    }

    @Override
    public void onRestore(ActionHolder holder, Pair<String, List<DtlExternalLocation>> cache) {
        if (apiQuery != null && apiQuery.equals(cache.first)) {
            response = cache.second;
            getResult();
            restored = true;
        }
    }

    @Override
    public CacheOptions getCacheOptions() {
        boolean needToSend = apiQuery != null && !restored;
        return ImmutableCacheOptions.builder()
                .sendAfterRestore(needToSend)
                .saveToCache(needToSend)
                .build();
    }

    private static List<DtlExternalLocation> filter(List<DtlExternalLocation> result, String query) {
        return Queryable.from(result)
                .filter((element, index) -> element.getLongName().toLowerCase().contains(query.toLowerCase()))
                .sort(DtlExternalLocation.provideComparator(query))
                .toList();
    }
}
