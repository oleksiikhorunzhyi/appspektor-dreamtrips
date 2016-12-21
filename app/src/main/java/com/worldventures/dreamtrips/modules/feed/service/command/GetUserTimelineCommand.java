package com.worldventures.dreamtrips.modules.feed.service.command;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.api.feed.GetUserTimelineHttpAction;
import com.worldventures.dreamtrips.api.feed.ImmutableGetUserTimelineHttpAction;

import java.util.Date;

import io.techery.janet.command.annotations.CommandAction;

public class GetUserTimelineCommand extends BaseGetFeedCommand<GetUserTimelineHttpAction> {

   private int userId;

   public GetUserTimelineCommand(int userId, Date before) {
      super(before);
      this.userId = userId;
   }

   @Override
   protected Class<GetUserTimelineHttpAction> provideHttpActionClass() {
      return GetUserTimelineHttpAction.class;
   }

   @Override
   protected GetUserTimelineHttpAction provideRequest() {
      GetUserTimelineHttpAction.Params params = ImmutableGetUserTimelineHttpAction.Params.builder()
            .pageSize(TIMELINE_LIMIT)
            .before(before)
            .userId(userId)
            .build();
      return new GetUserTimelineHttpAction(params);
   }

   @Override
   public int getFallbackErrorMessage() {
      return R.string.error_fail_to_load_timeline;
   }

   @CommandAction
   public static class LoadNext extends GetUserTimelineCommand {
      public LoadNext(int userId, Date before) {
         super(userId, before);
      }
   }

   @CommandAction
   public static class Refresh extends GetUserTimelineCommand {
      public Refresh(int userId) {
         super(userId, null);
      }
   }
}
