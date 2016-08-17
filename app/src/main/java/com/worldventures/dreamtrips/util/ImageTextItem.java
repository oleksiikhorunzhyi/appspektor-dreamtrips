package com.worldventures.dreamtrips.util;

import android.content.Intent;
import android.graphics.drawable.Drawable;

public class ImageTextItem {

   public final String text;
   public final Drawable icon;
   public final Intent intent;
   public final Type type;

   public ImageTextItem(String text, Drawable icon, Intent intent, Type type) {
      this.text = text;
      this.icon = icon;
      this.intent = intent;
      this.type = type;
   }

   public enum Type {
      ADDRESS, PHONE_NUMBER, WEBSITE_URL
   }
}
