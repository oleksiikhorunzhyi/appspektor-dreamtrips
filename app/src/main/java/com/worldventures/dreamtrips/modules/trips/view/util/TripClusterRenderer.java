package com.worldventures.dreamtrips.modules.trips.view.util;

import android.content.Context;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.trips.model.MapObjectHolder;
import com.worldventures.dreamtrips.modules.trips.model.Pin;
import com.worldventures.dreamtrips.modules.trips.model.TripClusterItem;

public class TripClusterRenderer extends DefaultClusterRenderer<TripClusterItem> {

   private Context context;

   public TripClusterRenderer(Context context, GoogleMap map, ClusterManager<TripClusterItem> clusterManager) {
      super(context, map, clusterManager);
      this.context = context;
   }

   @Override
   protected void onBeforeClusterItemRendered(TripClusterItem item, MarkerOptions markerOptions) {
      markerOptions.icon(BitmapDescriptorFactory.fromBitmap(TripPinFactory.createPinBitmapFromMapObject(context, item.getMapObjectHolder())));
   }

   @Override
   protected void onBeforeClusterRendered(Cluster<TripClusterItem> cluster, MarkerOptions markerOptions) {
      int clusterTripSize = getTripsCountFromCluster(cluster);
      markerOptions.icon(BitmapDescriptorFactory.fromBitmap(TripPinFactory.createClusterBitmap(context, R.drawable.cluster_pin, String
            .valueOf(clusterTripSize > 99 ? "99+" : clusterTripSize))));
   }

   @Override
   protected boolean shouldRenderAsCluster(Cluster<TripClusterItem> cluster) {
      return cluster.getSize() > 1;
   }

   private int getTripsCountFromCluster(Cluster<TripClusterItem> cluster) {
      int tripsCount = 0;
      for (TripClusterItem item : cluster.getItems()) {
         if (item.getMapObjectHolder().getType() == MapObjectHolder.Type.PIN) {
            tripsCount += ((Pin) item.getMapObjectHolder().getItem()).getTripUids().size();
         }
      }
      return tripsCount;
   }
}
