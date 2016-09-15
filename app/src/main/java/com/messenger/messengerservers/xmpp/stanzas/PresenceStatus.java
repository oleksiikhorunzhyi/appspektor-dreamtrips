package com.messenger.messengerservers.xmpp.stanzas;

import android.support.annotation.StringDef;

@StringDef({PresenceStatus.INVITED})
public @interface PresenceStatus {
   String INVITED = "invited";
}
