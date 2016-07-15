package com.messenger.messengerservers;

import com.messenger.messengerservers.event.ClearChatEvent;
import com.messenger.messengerservers.event.RevertClearingEvent;

import rx.Observable;

public interface ChatExtensions {

    Observable<ClearChatEvent> clearChat(String chatId, long clearToDate);

    Observable<RevertClearingEvent> revertChatClearing(String chatId);
}
