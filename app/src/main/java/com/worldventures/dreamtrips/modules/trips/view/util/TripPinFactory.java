package com.worldventures.dreamtrips.modules.trips.view.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.utils.ViewUtils;
import com.worldventures.dreamtrips.modules.trips.model.Pin;

public class TripPinFactory {

   public static Bitmap createPinBitmapFromMapObject(Context context, Pin pin) {
      if (pin.getTripUids().size() > 1)
         return createClusterBitmap(context, R.drawable.cluster_pin, String.valueOf(pin.getTripUids()
               .size() > 99 ? "99+" : pin.getTripUids().size()));
      else return BitmapFactory.decodeResource(context.getResources(), R.drawable.dt_pin_icon);
   }

   public static Bitmap createClusterBitmap(Context context, int drawableId, String text) {
      Bitmap bm = BitmapFactory.decodeResource(context.getResources(), drawableId).copy(Bitmap.Config.ARGB_8888, true);

      Typeface tf = Typeface.create("Helvetica", Typeface.BOLD);

      Paint paint = new Paint();
      paint.setStyle(Paint.Style.FILL);
      paint.setColor(Color.WHITE);
      paint.setTypeface(tf);
      paint.setTextAlign(Paint.Align.CENTER);
      paint.setTextSize(ViewUtils.pxFromDp(context, 16));

      Rect textRect = new Rect();
      paint.getTextBounds(text, 0, text.length(), textRect);

      Canvas canvas = new Canvas(bm);

      if (textRect.width() >= (canvas.getWidth() - 4)) paint.setTextSize(ViewUtils.pxFromDp(context, 7));

      int xPos = (canvas.getWidth() / 2) - 2;

      int yPos = (int) ((canvas.getHeight() / 2) - ((paint.descent() + paint.ascent()) / 2) - ViewUtils.pxFromDp(context, 2));

      canvas.drawText(text, xPos, yPos, paint);

      return bm;
   }

}
