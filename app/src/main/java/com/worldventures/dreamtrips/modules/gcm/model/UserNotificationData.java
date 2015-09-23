package com.worldventures.dreamtrips.modules.gcm.model;

public class UserNotificationData {
    public final String firstName;
    public final String lastName;
    public final int userId;
    public final int notificationId;

    public UserNotificationData(String firstName, String lastName, int userId, int notificationId) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.userId = userId;
        this.notificationId = notificationId;
    }
}
