package com.worldventures.dreamtrips.modules.feed.api;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.api.request.Query;
import com.worldventures.dreamtrips.core.utils.DateTimeUtils;
import com.worldventures.dreamtrips.modules.feed.model.feed.base.ParentFeedItem;
import com.worldventures.dreamtrips.modules.feed.model.notification.Notification;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class NotificationsQuery extends Query<ArrayList<ParentFeedItem>> {

    public static final int LIMIT = 10;
    private Date before;

    public NotificationsQuery(Date before) {
        super((Class<ArrayList<ParentFeedItem>>) new ArrayList<Notification>().getClass());
        this.before = before;
    }

    public NotificationsQuery() {
        super((Class<ArrayList<ParentFeedItem>>) new ArrayList<Notification>().getClass());
    }

    @Override
    public ArrayList<ParentFeedItem> loadDataFromNetwork() throws Exception {
        String before = this.before == null ? null : DateTimeUtils.convertDateToUTCString(this.before);
        return getService().getNotifications(LIMIT, before);
    }

    @Override
    public int getErrorMessage() {
        return R.string.error_failed_to_load_notifications;
    }
}
