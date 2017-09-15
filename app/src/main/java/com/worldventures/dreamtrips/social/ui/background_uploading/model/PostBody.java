package com.worldventures.dreamtrips.social.ui.background_uploading.model;

import android.support.annotation.Nullable;

import com.worldventures.dreamtrips.social.ui.feed.bundle.CreateEntityBundle;
import com.worldventures.dreamtrips.social.ui.feed.model.TextualPost;
import com.worldventures.dreamtrips.modules.trips.model.Location;

public interface PostBody {
   @Nullable
   String text();
   @Nullable
   Location location();
   @Nullable
   TextualPost createdPost();
   CreateEntityBundle.Origin origin();

   enum Type {
      VIDEO, PHOTO, TEXT
   }

   enum State {
      SCHEDULED, STARTED, UPLOADED, FAILED,
   }
}
