package com.worldventures.dreamtrips.modules.dtl.service.action;

import android.location.Location;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.api.action.CommandWithError;
import com.worldventures.dreamtrips.core.janet.dagger.InjectableAction;
import com.worldventures.dreamtrips.modules.dtl.model.location.DtlExternalLocation;

import java.util.List;

import javax.inject.Inject;

import io.techery.janet.Janet;
import io.techery.janet.command.annotations.CommandAction;
import rx.schedulers.Schedulers;

@CommandAction
public class DtlNearbyLocationAction extends CommandWithError<List<DtlExternalLocation>> implements InjectableAction {

   private final Location location;

   @Inject Janet janet;

   public DtlNearbyLocationAction(Location location) {
      this.location = location;
   }

   @Override
   protected void run(CommandCallback<List<DtlExternalLocation>> callback) throws Throwable {
      callback.onProgress(0);
      janet.createPipe(NearbyLocationsHttpAction.class, Schedulers.io())
            .createObservableResult(new NearbyLocationsHttpAction(location))
            .map(NearbyLocationsHttpAction::getResult)
            .subscribe(callback::onSuccess, callback::onFail);
   }

   @Override
   public int getFallbackErrorMessage() {
      return R.string.dtl_load_error;
   }
}
