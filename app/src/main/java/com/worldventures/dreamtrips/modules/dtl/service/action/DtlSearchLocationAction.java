package com.worldventures.dreamtrips.modules.dtl.service.action;

import android.support.v4.util.Pair;

import com.innahema.collections.query.queriables.Queryable;
import com.worldventures.dreamtrips.core.janet.cache.CacheOptions;
import com.worldventures.dreamtrips.core.janet.cache.CachedAction;
import com.worldventures.dreamtrips.core.janet.cache.ImmutableCacheOptions;
import com.worldventures.dreamtrips.core.janet.dagger.InjectableAction;
import com.worldventures.dreamtrips.modules.dtl.model.location.DtlExternalLocation;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import io.techery.janet.ActionHolder;
import io.techery.janet.Command;
import io.techery.janet.Janet;
import io.techery.janet.command.annotations.CommandAction;

@CommandAction
public class DtlSearchLocationAction extends Command<List<DtlExternalLocation>> implements CachedAction<Pair<String, List<DtlExternalLocation>>>, InjectableAction {

   private static final int API_SEARCH_QUERY_LENGTH = 3;

   @Inject Janet janet;

   private final String query;
   private String apiQuery;
   private boolean restored;
   private List<DtlExternalLocation> locations = new ArrayList<>();

   public DtlSearchLocationAction(String query) {
      this.query = query;
      if (query.length() >= API_SEARCH_QUERY_LENGTH) {
         this.apiQuery = query.substring(0, API_SEARCH_QUERY_LENGTH).toLowerCase();
      }
   }

   @Override
   protected void run(CommandCallback<List<DtlExternalLocation>> callback) throws Throwable {
      if (needApiRequest()) {
         janet.createPipe(DtlLocationsHttpAction.class)
               .createObservableResult(new DtlLocationsHttpAction(apiQuery))
               .map(DtlLocationsHttpAction::getResponse)
               .subscribe(response -> {
                  locations = response;
                  callback.onSuccess(filter(locations, query));
               }, callback::onFail);
      } else {
         callback.onSuccess(filter(locations, query));
      }
   }

   @Override
   public Pair<String, List<DtlExternalLocation>> getCacheData() {
      return new Pair<>(apiQuery, locations);
   }

   @Override
   public void onRestore(ActionHolder holder, Pair<String, List<DtlExternalLocation>> cache) {
      if (apiQuery != null && apiQuery.equals(cache.first)) {
         locations = cache.second;
         restored = true;
      }
   }

   @Override
   public CacheOptions getCacheOptions() {
      return ImmutableCacheOptions.builder().saveToCache(needApiRequest()).build();
   }

   private boolean needApiRequest() {
      return !restored && apiQuery != null;
   }

   public String getQuery() {
      return query;
   }

   private static List<DtlExternalLocation> filter(List<DtlExternalLocation> result, String query) {
      return Queryable.from(result).filter((element, index) -> element.getLongName()
            .toLowerCase()
            .contains(query.toLowerCase())).sort(DtlExternalLocation.provideComparator(query)).toList();
   }
}
