package com.worldventures.dreamtrips.modules.feed.api.response;

import com.worldventures.dreamtrips.core.repository.SnappyRepository;

public class NotificationCountResponseListener extends RequestCountResponseListener {

    public NotificationCountResponseListener(SnappyRepository db) {
        super(SnappyRepository.NOTIFICATIONS_COUNT, db);
    }

}
