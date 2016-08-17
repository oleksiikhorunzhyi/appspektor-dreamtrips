package com.worldventures.dreamtrips.modules.trips.manager.functions;

import android.graphics.Bitmap;
import android.util.Pair;

import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.worldventures.dreamtrips.modules.trips.model.MapObject;

import java.util.List;

import rx.Observable;
import rx.functions.Func1;

public class MarkerOptionsConverter implements Func1<List<Pair<Bitmap, MapObject>>, Observable<List<MarkerOptions>>> {

   @Override
   public Observable<List<MarkerOptions>> call(List<Pair<Bitmap, MapObject>> pairs) {
      return Observable.from(pairs)
            .map(bitmapMapObjectPair -> new MarkerOptions().position(new LatLng(bitmapMapObjectPair.second.getCoordinates()
                  .getLat(), bitmapMapObjectPair.second.getCoordinates().getLng()))
                  .icon(BitmapDescriptorFactory.fromBitmap(bitmapMapObjectPair.first)))
            .toList();
   }
}
