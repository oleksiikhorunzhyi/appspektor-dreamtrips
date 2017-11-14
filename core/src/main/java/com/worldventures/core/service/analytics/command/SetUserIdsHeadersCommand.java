package com.worldventures.core.service.analytics.command;

import com.worldventures.janet.injection.InjectableAction;
import com.worldventures.core.service.analytics.Tracker;
import com.worldventures.core.service.analytics.TrackerQualifier;

import java.util.HashMap;

import javax.inject.Inject;
import javax.inject.Named;

import io.techery.janet.Command;
import io.techery.janet.command.annotations.CommandAction;

@CommandAction()
public class SetUserIdsHeadersCommand extends Command<Void> implements InjectableAction {

   @Inject @Named(TrackerQualifier.ADOBE_TRACKER) Tracker tracker;

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
