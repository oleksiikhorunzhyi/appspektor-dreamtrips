package com.messenger.notification.model;

import com.messenger.entities.DataAttachment;
import com.messenger.entities.DataConversation;
import com.messenger.entities.DataMessage;
import com.messenger.entities.DataUser;

public interface NotificationData {
    DataConversation getConversation();

    DataMessage getMessage();

    DataAttachment getAttachment();

    DataUser getSender();
}
