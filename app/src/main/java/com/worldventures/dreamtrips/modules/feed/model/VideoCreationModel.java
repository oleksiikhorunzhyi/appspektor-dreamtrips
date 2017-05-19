package com.worldventures.dreamtrips.modules.feed.model;

import android.net.Uri;

import org.immutables.value.Value;

@Value.Immutable
public abstract class VideoCreationModel {

   public abstract Uri uri();
   public abstract State state();

   @Override
   public boolean equals(Object obj) {
      if (obj == null || !(obj instanceof VideoCreationModel)) return false;
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
