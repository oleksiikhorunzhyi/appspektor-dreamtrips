package com.worldventures.dreamtrips.modules.video.model;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public final class Status {
   public static final int STARTED = 0;
   public static final int IN_PROGRESS = 1;
   public static final int SUCCESS = 2;
   public static final int FAILED = 3;

   @Retention(RetentionPolicy.SOURCE)
   @IntDef({STARTED, IN_PROGRESS, SUCCESS, FAILED})
   public @interface CacheStatus {}
}

