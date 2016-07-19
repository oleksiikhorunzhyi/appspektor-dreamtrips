package com.messenger.messengerservers.model;

import android.support.annotation.Nullable;

import com.messenger.messengerservers.constant.ConversationStatus;
import com.messenger.messengerservers.constant.ConversationType;

import org.immutables.value.Value;

import java.util.List;

@Value.Immutable
public interface Conversation {

    String getId();

    @Nullable String getSubject();

    @Nullable String getAvatar();

    @ConversationType.Type String getType();

    int getUnreadMessageCount();

    long getLeftTime();

    @ConversationStatus.Status String getStatus();

    @Nullable List<Participant> getParticipants();

    @Nullable Message getLastMessage();

    long getLastActiveDate();

    @Nullable String getOwnerId();

    @Nullable Long getClearDate();
}
