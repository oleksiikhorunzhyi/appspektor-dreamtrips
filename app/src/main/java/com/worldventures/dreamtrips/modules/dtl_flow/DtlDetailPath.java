package com.worldventures.dreamtrips.modules.dtl_flow;

import android.support.v7.app.AppCompatActivity;

import com.worldventures.dreamtrips.core.flow.path.MasterDetailPath;
import com.worldventures.dreamtrips.modules.dtl_flow.parts.details.DtlDetailsScreenImpl;

public abstract class DtlDetailPath extends DtlPath {

   private MasterDetailPath master;

   public DtlDetailPath(MasterDetailPath master) {
      this.master = master;
   }

   @Override
   public MasterDetailPath getMaster() {
      return master;
   }

   @Override
   public void onPreDispatch(AppCompatActivity activity) {
      /**
       * We have problem with measuring new screen if old one has
       * {@link com.google.android.gms.maps.MapView MapView} or
       * {@link com.google.android.gms.maps.MapFragment MapFragment} <br />
       * Solution is to remove map prior to dispatch start as we do in this method. <br />
       * NOTE: make sure that MapFragment is added <i><b>programmatically</i></b> to container. <br />
       * @param traversal current traversal
       */
      if (activity.getFragmentManager().findFragmentByTag(DtlDetailsScreenImpl.MAP_TAG) != null) {
         activity.getFragmentManager().beginTransaction().remove(activity.getFragmentManager()
               .findFragmentByTag(DtlDetailsScreenImpl.MAP_TAG)).commit();
      }
   }
}
