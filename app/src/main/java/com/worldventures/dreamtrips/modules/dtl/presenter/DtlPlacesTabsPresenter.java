package com.worldventures.dreamtrips.modules.dtl.presenter;

import com.worldventures.dreamtrips.modules.common.presenter.Presenter;
import com.worldventures.dreamtrips.modules.dtl.model.DtlPlace;

import java.util.Arrays;
import java.util.List;

public class DtlPlacesTabsPresenter extends Presenter<DtlPlacesTabsPresenter.View> {

    private DtlPlace.PlaceType currentType;

    @Override
    public void takeView(View view) {
        super.takeView(view);
        setTabs();
        loadPlaces();
    }

    private void loadPlaces() {
        // TODO : implement places obtaining
    }

    public void onTabChange(DtlPlace.PlaceType type) {
        currentType = type;
    }

    public void setTabs() {
        view.setTypes(Arrays.asList(DtlPlace.PlaceType.PLACES, DtlPlace.PlaceType.DINING));
        view.updateSelection();
    }

    public interface View extends Presenter.View {

        void setTypes(List<DtlPlace.PlaceType> types);

        void updateSelection();
    }
}
