package com.messenger.messengerservers.model;

import org.immutables.value.Value;

@Value.Immutable
public interface DeletedMessage {
    String messageId();
    String source();
}
