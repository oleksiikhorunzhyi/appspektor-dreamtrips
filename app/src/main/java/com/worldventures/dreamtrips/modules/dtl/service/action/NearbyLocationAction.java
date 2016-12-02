package com.worldventures.dreamtrips.modules.dtl.service.action;

import android.location.Location;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.api.dtl.locations.LocationsHttpAction;
import com.worldventures.dreamtrips.core.api.action.CommandWithError;
import com.worldventures.dreamtrips.core.janet.JanetModule;
import com.worldventures.dreamtrips.core.janet.dagger.InjectableAction;
import com.worldventures.dreamtrips.modules.dtl.model.location.DtlLocation;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import io.techery.janet.Janet;
import io.techery.janet.command.annotations.CommandAction;
import io.techery.mappery.MapperyContext;
import rx.schedulers.Schedulers;

@CommandAction
public class NearbyLocationAction extends CommandWithError<List<DtlLocation>> implements InjectableAction {

   private final Location coordinates;

   @Inject @Named(JanetModule.JANET_API_LIB) Janet janet;
   @Inject MapperyContext mapperyContext;

   public NearbyLocationAction(Location location) {
      this.coordinates = location;
   }

   @Override
   protected void run(CommandCallback<List<DtlLocation>> callback) throws Throwable {
      callback.onProgress(0);
      janet.createPipe(LocationsHttpAction.class, Schedulers.io())
            .createObservableResult(HttpActionsCreator.provideNearbyHttpAction(coordinates))
            .map(LocationsHttpAction::locations)
            .map(locations -> mapperyContext.convert(locations, DtlLocation.class))
            .subscribe(callback::onSuccess, callback::onFail);
   }

   @Override
   public int getFallbackErrorMessage() {
      return R.string.dtl_load_error;
   }
}
