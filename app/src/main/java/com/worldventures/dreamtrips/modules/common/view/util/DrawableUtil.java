package com.worldventures.dreamtrips.modules.common.view.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

public class DrawableUtil {

    private Context context;

    public DrawableUtil(Context context) {
        this.context = context;
    }

    public Drawable copyIntoDrawable(Bitmap bitmap) {
        return new BitmapDrawable(context.getResources(), copyBitmap(bitmap));
    }

    public Bitmap copyBitmap(Bitmap bitmap) {
        return bitmap.copy(bitmap.getConfig(), true);
    }
}
