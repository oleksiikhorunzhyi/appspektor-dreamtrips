package com.worldventures.dreamtrips.modules.dtl_flow.parts.map;

import android.support.v7.app.AppCompatActivity;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.flow.path.MasterDetailPath;
import com.worldventures.dreamtrips.core.flow.path.PathAttrs;
import com.worldventures.dreamtrips.core.flow.util.Layout;
import com.worldventures.dreamtrips.modules.dtl_flow.DtlDetailPath;
import com.worldventures.dreamtrips.modules.dtl_flow.parts.master_toolbar.MasterToolbarPath;

import flow.path.Path;

@Layout(R.layout.screen_dtl_map)
public class DtlMapPath extends DtlDetailPath {

   public DtlMapPath(MasterDetailPath master) {
      super(master);
   }

   @Override
   public PathAttrs getAttrs() {
      return PathAttrs.WITH_DRAWER;
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
      if (activity.getFragmentManager().findFragmentByTag(DtlMapScreenImpl.MAP_TAG) != null) {
         activity.getFragmentManager().beginTransaction().remove(activity.getFragmentManager()
               .findFragmentByTag(DtlMapScreenImpl.MAP_TAG)).commit();
      }
   }

   @Override
   public Path getMasterToolbarPath() {
      return MasterToolbarPath.INSTANCE;
   }
}
