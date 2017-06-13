package com.worldventures.dreamtrips.wallet.analytics.oncard.action;

import com.worldventures.dreamtrips.modules.common.model.User;

import io.techery.janet.smartcard.model.analytics.AnalyticsLog;

public abstract class SmartCardUserAction extends SmartCardAnalyticsAction {

   SmartCardUserAction(AnalyticsLog logEntry) {
      super(logEntry);
   }

   public abstract void setUser(User user);

   @Override
   protected void processLog(int type, AnalyticsLog logEntry) {
      super.processLog(type, logEntry);
      switch (type) {
         case AnalyticsLog.USER_ASSIGNED:
            attributeMap.put("ocassigned", "1");
            break;
         case AnalyticsLog.USER_UNASSIGNED:
            attributeMap.put("ocunassigned ", "1");
            break;
      }
   }

}