package com.worldventures.dreamtrips.modules.gcm.delegate;

import android.os.Bundle;

import com.worldventures.dreamtrips.modules.friends.notification.UserNotificationData;
import com.worldventures.dreamtrips.modules.gcm.model.PushType;

public class NotificationDataParser {

    private static final String EXTRA_USER_ID = "user_id";
    private static final String EXTRA_NOTIFICATION_ID = "notification_id";
    private static final String EXTRA_FIRST_NAME = "first_name";
    private static final String EXTRA_LAST_NAME = "last_name";
    private static final String EXTRA_TYPE = "type";
    private static final String EXTRA_REQUESTS_COUNT = "requests_count";
    private static final String EXTRA_NOTIFICATIONS_COUNT = "notifications_count";

    public PushType obtainPushType(Bundle data){
        return PushType.of(data.getString(EXTRA_TYPE));
    }

    public UserNotificationData parseUserData(Bundle data) {
        String userId = data.getString(EXTRA_USER_ID);
        String notificationId = data.getString(EXTRA_NOTIFICATION_ID);
        return new UserNotificationData(
                data.getString(EXTRA_FIRST_NAME),
                data.getString(EXTRA_LAST_NAME),
                userId == null ? 0 : Integer.valueOf(userId),
                notificationId == null ? 0 : Integer.valueOf(notificationId)
        );
    }

    public int parseFriendRequestsCount(Bundle data) {
        return Integer.parseInt(data.getString(EXTRA_REQUESTS_COUNT));
    }

    public int parseNotificationsCount(Bundle data){
        return Integer.parseInt(data.getString(EXTRA_NOTIFICATIONS_COUNT));
    }
}
