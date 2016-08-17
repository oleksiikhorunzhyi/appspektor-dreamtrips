package com.messenger.messengerservers.model;

import com.messenger.messengerservers.constant.Affiliation;

import org.immutables.value.Value;

@Value.Immutable
public interface Participant {

   String getUserId();

   @Affiliation
   String getAffiliation();

   String getConversationId();
}
