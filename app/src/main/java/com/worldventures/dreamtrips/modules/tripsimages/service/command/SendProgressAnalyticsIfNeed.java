package com.worldventures.dreamtrips.modules.tripsimages.service.command;

import com.worldventures.dreamtrips.core.utils.tracksystem.BaseAnalyticsAction;

import io.techery.janet.Command;
import io.techery.janet.command.annotations.CommandAction;

@CommandAction
public abstract class SendProgressAnalyticsIfNeed<E extends BaseAnalyticsAction> extends Command<Integer> {

   private int expectedAnalyticStep;
   private final long currentProgress;
   private final long totalLength;

   public SendProgressAnalyticsIfNeed(int expectedAnalyticStep, long currentProgress, long totalLength) {
      this.expectedAnalyticStep = expectedAnalyticStep;
      this.currentProgress = currentProgress;
      this.totalLength = totalLength;
   }

   @Override
   protected void run(CommandCallback<Integer> callback) throws Throwable {
      int percent = (int) ((currentProgress * 100) / totalLength);
      int currentStep = percent / 25;
      E action = chooseAnalyticAction(currentStep, expectedAnalyticStep);
      if (action != null) {
         sendAnalyticAction(action);
         callback.onSuccess(expectedAnalyticStep + 1);
      } else
         callback.onSuccess(expectedAnalyticStep);
   }

   protected abstract E chooseAnalyticAction(int currentStep, int expectedAnalyticStep);

   protected abstract void sendAnalyticAction(E action);
}
