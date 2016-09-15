package com.worldventures.dreamtrips.modules.membership.model;

import android.net.Uri;

public class TemplatePhoto {

   private Uri path;

   public TemplatePhoto(Uri path) {
      this.path = path;
   }

   public Uri getPath() {
      return path;
   }
}
