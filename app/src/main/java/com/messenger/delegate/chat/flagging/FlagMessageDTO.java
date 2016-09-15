package com.messenger.delegate.chat.flagging;

import android.support.annotation.Nullable;

import org.immutables.value.Value;

@Value.Immutable
public interface FlagMessageDTO {
   String messageId();
   String reasonId();
   String groupId();
   @Nullable
   String reasonDescription();
   @Nullable
   String result();
}
