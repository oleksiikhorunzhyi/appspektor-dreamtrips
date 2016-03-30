package com.worldventures.dreamtrips.modules.dtl_flow.parts.locations_search;

import android.view.Menu;
import android.view.MenuItem;

import com.worldventures.dreamtrips.modules.dtl_flow.FlowPresenterImpl;

public class DtlLocationsSearchPresenterImpl extends FlowPresenterImpl<DtlLocationsSearchScreen, DtlLocationsSearchViewState>
        implements DtlLocationsSearchPresenter {

    @Override
    public int getToolbarMenuRes() {
        return 0;
    }

    @Override
    public void onToolbarMenuPrepared(Menu menu) {

    }

    @Override
    public boolean onToolbarMenuItemClick(MenuItem item) {
        return false;
    }

    @Override
    public void onNewViewState() {

    }

    @Override
    public void applyViewState() {

    }
}
