package com.worldventures.dreamtrips.util;

import android.content.Intent;
import android.graphics.drawable.Drawable;

public class ImageTextItem {
    public final String text;
    public final Drawable icon;
    public final Intent intent;

    public ImageTextItem(String text, Drawable icon, Intent intent) {
        this.text = text;
        this.icon = icon;
        this.intent = intent;
    }
}
