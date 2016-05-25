package com.worldventures.dreamtrips.modules.dtl_flow.parts.map;

import android.support.v7.app.AppCompatActivity;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.flow.path.MasterDetailPath;
import com.worldventures.dreamtrips.core.flow.path.PathAttrs;
import com.worldventures.dreamtrips.core.flow.util.Layout;
import com.worldventures.dreamtrips.modules.dtl_flow.DtlDetailPath;

@Layout(R.layout.screen_dtl_map)
public class DtlMapPath extends DtlDetailPath {

    private final boolean toolbarCollapsed;

    public DtlMapPath(MasterDetailPath master, boolean toolbarCollapsed) {
        super(master);
        this.toolbarCollapsed = toolbarCollapsed;
    }

    public DtlMapPath(MasterDetailPath master) {
        super(master);
        this.toolbarCollapsed = true;
    }

    @Override
    public PathAttrs getAttrs() {
        return PathAttrs.WITH_DRAWER;
    }

    @Override
    public void onPreDispatch(AppCompatActivity activity) {
        /*
         * We have problem with measuring new screen if old one has
         * {@link com.google.android.gms.maps.MapView MapView} or
         * {@link com.google.android.gms.maps.MapFragment MapFragment} <br />
         * Solution is to remove map prior to dispatch start as we do in this method. <br />
         * NOTE: make sure that MapFragment is added <i><b>programmatically</i></b> to container <br />
         * with tag {@link DtlMapScreenImpl#MAP_TAG}
         * @param traversal current traversal
         */
        activity.getFragmentManager()
                .beginTransaction()
                .remove(activity.getFragmentManager().findFragmentByTag(DtlMapScreenImpl.MAP_TAG))
                .commit();
    }
}
