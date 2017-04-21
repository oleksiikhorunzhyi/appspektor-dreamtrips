package com.worldventures.dreamtrips.modules.feed.service.command;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.api.feed.GetTimelineHttpAction;
import com.worldventures.dreamtrips.api.feed.ImmutableGetTimelineHttpAction;

import java.util.Date;

import io.techery.janet.command.annotations.CommandAction;

public class GetAccountTimelineCommand extends BaseGetFeedCommand<GetTimelineHttpAction> {

   public GetAccountTimelineCommand(Date before) {
      super(before);
   }

   @Override
   protected Class<GetTimelineHttpAction> provideHttpActionClass() {
      return GetTimelineHttpAction.class;
   }

   @Override
   protected GetTimelineHttpAction provideRequest() {
      GetTimelineHttpAction.Params params = ImmutableGetTimelineHttpAction.Params.builder()
            .pageSize(TIMELINE_LIMIT)
            .before(before)
            .build();
      return new GetTimelineHttpAction(params);
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
