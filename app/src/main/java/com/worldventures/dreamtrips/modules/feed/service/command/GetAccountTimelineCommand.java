package com.worldventures.dreamtrips.modules.feed.service.command;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.feed.service.api.GetAccountTimelineHttpAction;

import java.util.Date;

import io.techery.janet.command.annotations.CommandAction;

public class GetAccountTimelineCommand extends BaseGetFeedCommand<GetAccountTimelineHttpAction> {

   public GetAccountTimelineCommand(Date before) {
      super(before);
   }

   @Override
   protected Class<GetAccountTimelineHttpAction> provideHttpActionClass() {
      return GetAccountTimelineHttpAction.class;
   }

   @Override
   protected GetAccountTimelineHttpAction provideRequest() {
      return new GetAccountTimelineHttpAction(TIMELINE_LIMIT, before);
   }

   @Override
   public int getFallbackErrorMessage() {
      return R.string.error_fail_to_load_timeline;
   }

   @CommandAction
   public static class LoadNext extends GetAccountTimelineCommand {
      public LoadNext(Date before) {
         super(before);
      }
   }

   @CommandAction
   public static class Refresh extends GetAccountTimelineCommand {
      public Refresh() {
         super(null);
      }
   }
}
