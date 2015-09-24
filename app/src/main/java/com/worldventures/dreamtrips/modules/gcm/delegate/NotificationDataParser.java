package com.worldventures.dreamtrips.modules.gcm.delegate;

import android.os.Bundle;

import com.worldventures.dreamtrips.modules.gcm.model.UserNotificationData;

public class NotificationDataParser {

    public UserNotificationData parseUserData(Bundle data) {
        String userId = data.getString("user_id");
        String notificationId = data.getString("notification_id");
        return new UserNotificationData(
                data.getString("first_name"),
                data.getString("last_name"),
                userId == null ? 0 : Integer.valueOf(userId),
                notificationId == null ? 0 : Integer.valueOf(notificationId)
        );
    }
}
