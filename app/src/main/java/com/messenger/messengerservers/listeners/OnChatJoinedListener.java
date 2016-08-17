package com.messenger.messengerservers.listeners;

import com.messenger.messengerservers.model.Participant;

public interface OnChatJoinedListener {
   void onChatJoined(Participant participant, boolean isOnline);
}