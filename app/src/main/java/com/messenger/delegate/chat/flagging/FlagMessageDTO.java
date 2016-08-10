package com.messenger.delegate.chat.flagging;

import org.immutables.value.Value;
import android.support.annotation.Nullable;

@Value.Immutable
public interface FlagMessageDTO {
    String messageId();
    String reasonId();
    String groupId();
    @Nullable String reasonDescription();
    @Nullable String result();
}
