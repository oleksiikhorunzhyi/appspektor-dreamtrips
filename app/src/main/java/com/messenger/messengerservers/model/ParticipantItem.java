package com.messenger.messengerservers.model;

import org.immutables.value.Value;

@Value.Immutable
public interface ParticipantItem {
    String getAffiliation();
    String getUserId();
}
