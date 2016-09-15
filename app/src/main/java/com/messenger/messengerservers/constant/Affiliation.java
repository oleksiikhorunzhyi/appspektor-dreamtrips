package com.messenger.messengerservers.constant;

import android.support.annotation.StringDef;

@StringDef({Affiliation.MEMBER, Affiliation.NONE, Affiliation.OWNER})
public @interface Affiliation {
   String MEMBER = "member";
   String OWNER = "owner";
   String NONE = "none";
}
