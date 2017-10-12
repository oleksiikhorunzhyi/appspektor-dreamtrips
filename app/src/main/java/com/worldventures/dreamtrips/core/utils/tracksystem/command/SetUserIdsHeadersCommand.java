package com.worldventures.dreamtrips.core.utils.tracksystem.command;

import com.worldventures.dreamtrips.core.janet.dagger.InjectableAction;
import com.worldventures.dreamtrips.core.module.AnalyticsModule;
import com.worldventures.dreamtrips.core.utils.tracksystem.Tracker;

import java.util.HashMap;

import javax.inject.Inject;
import javax.inject.Named;

import io.techery.janet.Command;
import io.techery.janet.command.annotations.CommandAction;

@CommandAction()
public class SetUserIdsHeadersCommand extends Command<Void> implements InjectableAction {

   @Inject @Named(AnalyticsModule.ADOBE_TRACKER) Tracker tracker;

   private String username;
   private String userId;

   public SetUserIdsHeadersCommand(String username, String userId) {
      this.username = username;
      this.userId = userId;
   }

   @Override
   protected void run(CommandCallback<Void> commandCallback) throws Throwable {
      HashMap<String, Object> headerData = new HashMap<>();
      headerData.put("member_id", username);
      headerData.put("old_member_id", userId);
      tracker.setHeaderData(headerData);
   }
}
