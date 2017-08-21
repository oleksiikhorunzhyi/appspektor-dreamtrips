package com.worldventures.dreamtrips.modules.trips.view.custom;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.google.android.gms.maps.MapView;

public class ToucheableMapView extends MapView {

   private MapTouchListener mapTouchListener;
   private MapTouchListener2 mapTouchListener2;

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
      //deprecated
      if (mapTouchListener != null) switch (ev.getAction()) {
         case MotionEvent.ACTION_DOWN:
            mapTouchListener.onUpdateMap();
            break;
      }
      //
      if (mapTouchListener2 != null) {
         mapTouchListener2.onUpdateMap(ev);
      }
      return super.dispatchTouchEvent(ev);
   }

   @Deprecated
   public void setMapTouchListener(MapTouchListener mapTouchListener) {
      this.mapTouchListener = mapTouchListener;
   }

   public void setMapTouchListener2(MapTouchListener2 mapTouchListener) {
      this.mapTouchListener2 = mapTouchListener;
   }

   // Map Activity must implement this interface
   @Deprecated
   public interface MapTouchListener {
      void onUpdateMap();
   }

   public interface MapTouchListener2 {
      void onUpdateMap(MotionEvent motionEvent);
   }
}