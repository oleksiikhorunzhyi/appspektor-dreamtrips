package com.messenger.notification.model;

import com.messenger.entities.DataUser;

import org.immutables.value.Value;

import java.util.List;

@Value.Immutable
public interface GroupNotificationData extends NotificationData {
   List<DataUser> getParticipants();
}
