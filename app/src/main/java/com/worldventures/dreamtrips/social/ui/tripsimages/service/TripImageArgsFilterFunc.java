package com.worldventures.dreamtrips.social.ui.tripsimages.service;

import com.worldventures.dreamtrips.social.ui.tripsimages.service.command.BaseMediaCommand;
import com.worldventures.dreamtrips.social.ui.tripsimages.view.args.TripImagesArgs;

import io.techery.janet.ActionState;
import rx.functions.Func1;

public class TripImageArgsFilterFunc implements Func1<ActionState<BaseMediaCommand>, Boolean> {

   private TripImagesArgs currentArgs;

   public TripImageArgsFilterFunc(TripImagesArgs currentArgs) {
      this.currentArgs = currentArgs;
   }

   @Override
   public Boolean call(ActionState<BaseMediaCommand> actionState) {
      return actionState.action.getArgs()
            .equals(currentArgs) && (actionState.action.isLoadMore() || actionState.action.isReload());
   }
}
