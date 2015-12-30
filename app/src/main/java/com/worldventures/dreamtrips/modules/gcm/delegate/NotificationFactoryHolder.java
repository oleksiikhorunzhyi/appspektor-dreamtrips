package com.worldventures.dreamtrips.modules.gcm.delegate;

import com.worldventures.dreamtrips.modules.friends.notification.FriendNotificationFactory;

public class NotificationFactoryHolder {

    private FriendNotificationFactory friendNotificationFactory;
    private PhotoNotificationFactory photoNotificationFactory;

    public NotificationFactoryHolder(FriendNotificationFactory friendNotificationFactory, PhotoNotificationFactory photoNotificationFactory) {
        this.friendNotificationFactory = friendNotificationFactory;
        this.photoNotificationFactory = photoNotificationFactory;
    }

    public FriendNotificationFactory getFriendNotificationFactory() {
        return friendNotificationFactory;
    }

    public PhotoNotificationFactory getPhotoNotificationFactory() {
        return photoNotificationFactory;
    }
}
