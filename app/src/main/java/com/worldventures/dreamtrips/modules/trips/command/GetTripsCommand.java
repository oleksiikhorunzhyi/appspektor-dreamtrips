package com.worldventures.dreamtrips.modules.trips.command;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.api.action.CommandWithError;
import com.worldventures.dreamtrips.core.janet.dagger.InjectableAction;
import com.worldventures.dreamtrips.modules.trips.api.GetTripsHttpAction;
import com.worldventures.dreamtrips.modules.trips.model.TripModel;
import com.worldventures.dreamtrips.modules.trips.model.TripQueryData;

import java.util.List;

import javax.inject.Inject;

import io.techery.janet.Janet;
import io.techery.janet.command.annotations.CommandAction;

@CommandAction
public class GetTripsCommand extends CommandWithError<List<TripModel>> implements InjectableAction {

   @Inject Janet janet;

   private final TripQueryData requestParams;

   public GetTripsCommand(TripQueryData requestParams) {
      this.requestParams = requestParams;
   }

   @Override
   protected void run(CommandCallback<List<TripModel>> callback) throws Throwable {
      janet.createPipe(GetTripsHttpAction.class)
            .createObservableResult(new GetTripsHttpAction(requestParams))
            .map(GetTripsHttpAction::getResponseItems)
            .subscribe(callback::onSuccess, callback::onFail);
   }

   @Override
   public int getFallbackErrorMessage() {
      return R.string.string_failed_to_load_trips;
   }

   @CommandAction
   public static class LoadNextTripsCommand extends GetTripsCommand {

      public LoadNextTripsCommand(TripQueryData requestParams) {
         super(requestParams);
      }
   }

   @CommandAction
   public static class ReloadTripsCommand extends GetTripsCommand {

      public ReloadTripsCommand(TripQueryData requestParams) {
         super(requestParams);
      }
   }
}
