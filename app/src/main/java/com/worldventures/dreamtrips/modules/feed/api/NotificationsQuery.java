package com.worldventures.dreamtrips.modules.feed.api;

import com.worldventures.dreamtrips.core.api.request.Query;
import com.worldventures.dreamtrips.core.utils.DateTimeUtils;
import com.worldventures.dreamtrips.modules.feed.model.feed.base.ParentFeedModel;
import com.worldventures.dreamtrips.modules.feed.model.notification.Notification;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class NotificationsQuery extends Query<ArrayList<ParentFeedModel>> {

    public static final int LIMIT = 10;
    private Date before;

    public NotificationsQuery(Date before) {
        super((Class<ArrayList<ParentFeedModel>>) new ArrayList<Notification>().getClass());
        this.before = before;
    }

    public NotificationsQuery() {
        super((Class<ArrayList<ParentFeedModel>>) new ArrayList<Notification>().getClass());
        this.before = Calendar.getInstance().getTime();
    }

    @Override
    public ArrayList<ParentFeedModel> loadDataFromNetwork() throws Exception {
        return getService().getNotifications(LIMIT, DateTimeUtils.convertDateToUTCString(before));
    }
}
