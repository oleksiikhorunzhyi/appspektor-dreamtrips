package com.worldventures.dreamtrips.social.ui.background_uploading.model;

public enum  CompoundOperationState {
   SCHEDULED,
   STARTED,
   FINISHED,
   PAUSED,
   FAILED,
   PROCESSING, //used for video transcoding status check
   FAILED_PROCESSING, //used for video transcoding status check
}
