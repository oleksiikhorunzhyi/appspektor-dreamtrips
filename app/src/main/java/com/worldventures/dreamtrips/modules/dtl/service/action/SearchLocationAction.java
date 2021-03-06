package com.worldventures.dreamtrips.modules.dtl.service.action;

import android.support.v4.util.Pair;

import com.innahema.collections.query.queriables.Queryable;
import com.worldventures.core.janet.CommandWithError;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.api.dtl.locations.LocationsHttpAction;
import com.worldventures.dreamtrips.modules.dtl.helper.comparator.LocationComparator;
import com.worldventures.dreamtrips.modules.dtl.model.location.DtlLocation;
import com.worldventures.dreamtrips.modules.dtl.service.action.bundle.LocationsActionParams;
import com.worldventures.dreamtrips.modules.dtl.service.action.creator.LocationsActionCreator;
import com.worldventures.janet.cache.CacheOptions;
import com.worldventures.janet.cache.CachedAction;
import com.worldventures.janet.injection.InjectableAction;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import io.techery.janet.ActionHolder;
import io.techery.janet.Janet;
import io.techery.janet.command.annotations.CommandAction;
import io.techery.mappery.MapperyContext;
import rx.schedulers.Schedulers;

@CommandAction
public class SearchLocationAction extends CommandWithError<List<DtlLocation>> implements
      CachedAction<Pair<String, List<DtlLocation>>>, InjectableAction {

   @Inject Janet janet;
   @Inject MapperyContext mapperyContext;
   @Inject LocationsActionCreator actionCreator;

   private static final int API_SEARCH_QUERY_LENGTH = 3;

   private final LocationsActionParams actionParams;
   private String apiQuery;
   private boolean restored;
   private List<DtlLocation> locations = new ArrayList<>();

   public static SearchLocationAction create(LocationsActionParams params) {
      return new SearchLocationAction(params);
   }

   public SearchLocationAction(LocationsActionParams params) {
      this.actionParams = params;
      if (params.query().length() >= API_SEARCH_QUERY_LENGTH) {
         this.apiQuery = params.query().substring(0, API_SEARCH_QUERY_LENGTH).toLowerCase();
      }
   }

   @Override
   protected void run(CommandCallback<List<DtlLocation>> callback) throws Throwable {
      callback.onProgress(0);
      if (needApiRequest()) {
         janet.createPipe(LocationsHttpAction.class, Schedulers.io())
               .createObservableResult(actionCreator.createAction(actionParams))
               .map(LocationsHttpAction::locations)
               .map(locations -> mapperyContext.convert(locations, DtlLocation.class))
               .subscribe(response -> {
                  locations = response;
                  callback.onSuccess(filter(locations, actionParams.query()));
               }, callback::onFail);
      } else {
         callback.onSuccess(filter(locations, actionParams.query()));
      }
   }

   @Override
   public Pair<String, List<DtlLocation>> getCacheData() {
      return new Pair<>(apiQuery, locations);
   }

   @Override
   public void onRestore(ActionHolder holder, Pair<String, List<DtlLocation>> cache) {
      if (apiQuery != null && apiQuery.equals(cache.first)) {
         locations = cache.second;
         restored = true;
      }
   }

   @Override
   public CacheOptions getCacheOptions() {
      return new CacheOptions(true, needApiRequest(), true, null);
   }

   private boolean needApiRequest() {
      return !restored && apiQuery != null;
   }

   public String getQuery() {
      return actionParams.query();
   }

   private static List<DtlLocation> filter(List<DtlLocation> result, String query) {
      return Queryable.from(result).filter((element, index) -> element.longName()
            .toLowerCase()
            .contains(query.toLowerCase())).sort(LocationComparator.provideComparator(query)).toList();
   }

   @Override
   public int getFallbackErrorMessage() {
      return R.string.dtl_load_error;
   }
}
