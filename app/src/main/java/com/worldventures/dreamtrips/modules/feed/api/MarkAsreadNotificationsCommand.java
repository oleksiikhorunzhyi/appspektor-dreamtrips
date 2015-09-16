package com.worldventures.dreamtrips.modules.feed.api;

import com.worldventures.dreamtrips.core.api.request.Query;
import com.worldventures.dreamtrips.core.utils.DateTimeUtils;

import java.util.Date;

public class MarkAsreadNotificationsCommand extends Query<Void> {

    private Date from;
    private Date to;

    public MarkAsreadNotificationsCommand(Date from, Date to) {
        super(Void.class);
        this.from = from;
        this.to = to;

    }

    @Override
    public Void loadDataFromNetwork() throws Exception {
        return null;
        //getService().markAsRead(DateTimeUtils.convertDateToUTCString(from), DateTimeUtils.convertDateToUTCString(to));
    }
}
