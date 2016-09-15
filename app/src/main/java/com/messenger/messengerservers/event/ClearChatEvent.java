package com.messenger.messengerservers.event;

import org.immutables.value.Value;

@Value.Immutable
public interface ClearChatEvent {

   String getConversationId();

   long clearTime();
}
