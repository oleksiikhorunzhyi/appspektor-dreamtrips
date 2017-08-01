package com.worldventures.dreamtrips.modules.tripsimages.service;

import com.worldventures.dreamtrips.modules.tripsimages.service.command.BaseTripImagesCommand;
import com.worldventures.dreamtrips.modules.tripsimages.view.args.TripImagesArgs;

import io.techery.janet.ActionState;
import rx.functions.Func1;

public class TripImageArgsFilterFunc implements Func1<ActionState<BaseTripImagesCommand>, Boolean> {

   private TripImagesArgs currentArgs;

   public TripImageArgsFilterFunc(TripImagesArgs currentArgs) {
      this.currentArgs = currentArgs;
   }

   @Override
   public Boolean call(ActionState<BaseTripImagesCommand> actionState) {
      return actionState.action.getArgs()
            .equals(currentArgs) && (actionState.action.isLoadMore() || actionState.action.isReload());
   }
}
