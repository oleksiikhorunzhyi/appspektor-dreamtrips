package com.worldventures.dreamtrips.view.custom;

import android.content.Context;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.MapView;

public class ToucheableMapView extends MapView {

    private long lastTouched = 0;
    private static final long SCROLL_TIME = 200L; // 200 Milliseconds, but you can adjust that to your liking
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

    public ToucheableMapView(Context context, GoogleMapOptions options) {
        super(context, options);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (mapTouchListener != null)
            switch (ev.getAction()) {
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
        public void onUpdateMap();
    }
}