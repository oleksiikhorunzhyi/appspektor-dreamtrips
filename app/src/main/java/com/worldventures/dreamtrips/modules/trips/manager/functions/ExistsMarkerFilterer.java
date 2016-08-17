package com.worldventures.dreamtrips.modules.trips.manager.functions;

import android.graphics.Bitmap;
import android.util.Pair;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.innahema.collections.query.queriables.Queryable;
import com.worldventures.dreamtrips.modules.common.model.Coordinates;
import com.worldventures.dreamtrips.modules.trips.model.MapObject;

import java.util.List;

import rx.Observable;
import rx.functions.Func1;

public class ExistsMarkerFilterer implements Func1<List<Pair<Bitmap, MapObject>>, Observable<List<Pair<Bitmap, MapObject>>>> {

   private final List<Marker> existsMarkers;

   public ExistsMarkerFilterer(List<Marker> existsMarkers) {
      this.existsMarkers = existsMarkers;
   }

   @Override
   public Observable<List<Pair<Bitmap, MapObject>>> call(List<Pair<Bitmap, MapObject>> pairs) {
      return Observable.from(pairs).filter(bitmapMapObjectPair -> {
         Coordinates coordinates = bitmapMapObjectPair.second.getCoordinates();
         LatLng latLng = new LatLng(coordinates.getLat(), coordinates.getLng());
         return Queryable.from(existsMarkers).firstOrDefault(marker -> marker.getPosition().equals(latLng)) == null;
      }).toList();
   }
}
