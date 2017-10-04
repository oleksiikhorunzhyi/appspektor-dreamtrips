package com.worldventures.dreamtrips.social.ui.share;

import android.support.annotation.StringDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.SOURCE)
@StringDef({ShareType.FACEBOOK, ShareType.TWITTER, ShareType.EXTERNAL_STORAGE})
public @interface ShareType {
   String FACEBOOK = "facebook";
   String TWITTER = "twitter";
   String EXTERNAL_STORAGE = "external_storage";
}
