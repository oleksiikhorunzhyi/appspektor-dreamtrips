package com.worldventures.dreamtrips.social.ui.feed.model;

import android.net.Uri;
import android.support.annotation.Nullable;

import com.worldventures.dreamtrips.modules.media_picker.model.VideoMetadata;

import org.immutables.value.Value;

@Value.Immutable
public abstract class VideoCreationModel {

   public abstract Uri uri();
   @Nullable
   public abstract VideoMetadata videoMetadata();
   public abstract State state();
   public abstract boolean canDelete();

   @Override
   public boolean equals(Object obj) {
      if (obj == null || !(obj instanceof VideoCreationModel)) { //NOPMD
         return false;
      }
      VideoCreationModel videoCreationModel = (VideoCreationModel) obj;
      return videoCreationModel.uri().equals(uri());
   }

   @Override
   public int hashCode() {
      return uri().hashCode();
   }

   public enum State {
      LOCAL, UPLOADED
   }
}
