package com.worldventures.dreamtrips.modules.feed.service.command;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.feed.service.api.GetAccountFeedHttpAction;

import java.util.Date;

import io.techery.janet.command.annotations.CommandAction;

public class GetAccountFeedCommand extends BaseGetFeedCommand<GetAccountFeedHttpAction> {

   private String circleId;

   public GetAccountFeedCommand(String circleId, Date before) {
      super(before);
      this.circleId = circleId;
   }

   @Override
   protected Class<GetAccountFeedHttpAction> provideHttpActionClass() {
      return GetAccountFeedHttpAction.class;
   }

   @Override
   protected GetAccountFeedHttpAction provideRequest() {
      return new GetAccountFeedHttpAction(circleId, FEED_LIMIT, before);
   }

   @Override
   public int getFallbackErrorMessage() {
      return R.string.error_fail_to_load_feed;
   }

   @CommandAction
   public static class LoadNext extends GetAccountFeedCommand {
      public LoadNext(String circleId, Date before) {
         super(circleId, before);
      }
   }

   @CommandAction
   public static class Refresh extends GetAccountFeedCommand {
      public Refresh(String circleId) {
         super(circleId, null);
      }
   }
}
