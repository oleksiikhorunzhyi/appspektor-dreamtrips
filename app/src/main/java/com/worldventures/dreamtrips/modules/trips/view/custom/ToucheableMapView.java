package com.worldventures.dreamtrips.modules.trips.view.custom;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.google.android.gms.maps.MapView;

public class ToucheableMapView extends MapView {

   private MapTouchListener mapTouchListener;

   public ToucheableMapView(Context context) {
      super(context);
   }

   public ToucheableMapView(Context context, AttributeSet attrs) {
      super(context, attrs);
   }

   public ToucheableMapView(Context context, AttributeSet attrs, int defStyle) {
      super(context, attrs, defStyle);
   }

   @Override
   public boolean dispatchTouchEvent(MotionEvent ev) {
      if (mapTouchListener != null) switch (ev.getAction()) {
         case MotionEvent.ACTION_DOWN:
            mapTouchListener.onUpdateMap();
            break;
      }
      return super.dispatchTouchEvent(ev);
   }

   public void setMapTouchListener(MapTouchListener mapTouchListener) {
      this.mapTouchListener = mapTouchListener;
   }

   // Map Activity must implement this interface
   public interface MapTouchListener {
      void onUpdateMap();
   }
}