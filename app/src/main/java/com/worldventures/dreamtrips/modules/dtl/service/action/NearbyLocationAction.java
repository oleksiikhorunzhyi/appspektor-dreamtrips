package com.worldventures.dreamtrips.modules.dtl.service.action;

import com.worldventures.core.janet.dagger.InjectableAction;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.api.dtl.locations.LocationsHttpAction;
import com.worldventures.core.janet.CommandWithError;
import com.worldventures.dreamtrips.modules.dtl.model.location.DtlLocation;
import com.worldventures.dreamtrips.modules.dtl.service.action.bundle.LocationsActionParams;
import com.worldventures.dreamtrips.modules.dtl.service.action.creator.LocationsActionCreator;

import java.util.List;

import javax.inject.Inject;

import io.techery.janet.Janet;
import io.techery.janet.command.annotations.CommandAction;
import io.techery.mappery.MapperyContext;
import rx.schedulers.Schedulers;

@CommandAction
public class NearbyLocationAction extends CommandWithError<List<DtlLocation>> implements InjectableAction {

   private final LocationsActionParams params;

   @Inject Janet janet;
   @Inject MapperyContext mapperyContext;
   @Inject LocationsActionCreator locationsActionCreator;

   public static NearbyLocationAction create(LocationsActionParams params) {
      return new NearbyLocationAction(params);
   }

   public NearbyLocationAction(LocationsActionParams params) {
      this.params = params;
   }

   @Override
   protected void run(CommandCallback<List<DtlLocation>> callback) throws Throwable {
      callback.onProgress(0);
      janet.createPipe(LocationsHttpAction.class, Schedulers.io())
            .createObservableResult(locationsActionCreator.createAction(params))
            .map(LocationsHttpAction::locations)
            .map(locations -> mapperyContext.convert(locations, DtlLocation.class))
            .subscribe(callback::onSuccess, callback::onFail);
   }

   @Override
   public int getFallbackErrorMessage() {
      return R.string.dtl_load_error;
   }
}
