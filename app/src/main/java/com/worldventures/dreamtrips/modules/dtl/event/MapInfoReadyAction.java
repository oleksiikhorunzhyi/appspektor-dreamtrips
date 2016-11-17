package com.worldventures.dreamtrips.modules.dtl.event;

import android.support.v4.util.Pair;

import com.google.android.gms.maps.model.LatLng;
import com.worldventures.dreamtrips.core.api.action.ValueCommandAction;

import io.techery.janet.command.annotations.CommandAction;

@CommandAction
public class MapInfoReadyAction extends ValueCommandAction<Pair<LatLng, Integer>> {

   public static MapInfoReadyAction create(LatLng coordinates, int height) {
      return new MapInfoReadyAction(coordinates, height);
   }

   public MapInfoReadyAction(LatLng coordinates, int height) {
      super(new Pair<>(coordinates, height));
   }

}
