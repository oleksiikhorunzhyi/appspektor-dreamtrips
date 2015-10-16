package com.worldventures.dreamtrips.modules.dtl.presenter;

import com.worldventures.dreamtrips.core.repository.SnappyRepository;
import com.worldventures.dreamtrips.modules.common.presenter.Presenter;
import com.worldventures.dreamtrips.modules.dtl.bundle.PlacesBundle;
import com.worldventures.dreamtrips.modules.dtl.model.DtlLocation;

import javax.inject.Inject;

import icepick.State;

public class DtlStartPresenter extends Presenter<DtlStartPresenter.View> {

    @Inject
    SnappyRepository db;
    @State
    boolean initialized;

    @Override
    public void takeView(View view) {
        super.onInjected();
        if (initialized) return;
        initialized = true;
        //
        DtlLocation location = db.getSelectedDtlLocation();
        if (location == null) {
            view.openDtlLocationsScreen();
        } else {
            view.openDtlPlacesScreen(new PlacesBundle(location));
        }
    }

    public interface View extends Presenter.View {

        void openDtlLocationsScreen();

        void openDtlPlacesScreen(PlacesBundle bundle);
    }
}
