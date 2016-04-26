package com.messenger.messengerservers.listeners;

import org.jivesoftware.smack.packet.Presence;

public interface PresenceInterceptor {
    void onOutgoingPresence(Presence presence);
}
