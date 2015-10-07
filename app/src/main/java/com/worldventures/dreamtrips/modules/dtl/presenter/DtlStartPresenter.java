package com.worldventures.dreamtrips.modules.dtl.presenter;

import com.worldventures.dreamtrips.core.repository.SnappyRepository;
import com.worldventures.dreamtrips.modules.common.presenter.Presenter;
import com.worldventures.dreamtrips.modules.dtl.bundle.PlacesBundle;
import com.worldventures.dreamtrips.modules.dtl.model.DtlLocation;

import javax.inject.Inject;

public class DtlStartPresenter extends Presenter<DtlStartPresenter.View> {

    @Inject
    SnappyRepository db;

    @Override
    public void takeView(View view) {
        super.onInjected();
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
