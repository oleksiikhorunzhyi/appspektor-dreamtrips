package com.worldventures.dreamtrips.modules.gcm.delegate;

import android.os.Bundle;

import com.worldventures.dreamtrips.modules.gcm.model.UserNotificationData;

public class NotificationDataParser {

    public UserNotificationData parseUserData(Bundle data) {
        return new UserNotificationData(
                data.getString("first_name"),
                data.getString("last_name"),
                Integer.valueOf(data.getString("user_id")).intValue(),
                Integer.valueOf(data.getString("notification_id")).intValue()
        );
    }
}
