package com.worldventures.dreamtrips.modules.feed.api;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.api.request.Query;
import com.worldventures.dreamtrips.core.utils.DateTimeUtils;

import java.util.Date;

public class MarkAsReadNotificationsCommand extends Query<Void> {

   private Date since;
   private Date before;

   public MarkAsReadNotificationsCommand(Date since, Date before) {
      super(Void.class);
      this.since = since;
      this.before = before;
   }

   @Override
   public Void loadDataFromNetwork() throws Exception {
      return getService().markAsRead(DateTimeUtils.convertDateToUTCString(since), DateTimeUtils.convertDateToUTCString(before));
   }

   @Override
   public int getErrorMessage() {
      return R.string.error_fail_to_mark_notifications;
   }
}
