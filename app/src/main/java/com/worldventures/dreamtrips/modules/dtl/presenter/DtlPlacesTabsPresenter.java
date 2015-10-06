package com.worldventures.dreamtrips.modules.dtl.presenter;

import com.worldventures.dreamtrips.modules.common.presenter.Presenter;
import com.worldventures.dreamtrips.modules.dtl.model.DtlPlaceType;

import java.util.Arrays;
import java.util.List;

public class DtlPlacesTabsPresenter extends Presenter<DtlPlacesTabsPresenter.View> {

    private DtlPlaceType currentType;

    @Override
    public void takeView(View view) {
        super.takeView(view);
        setTabs();
        loadPlaces();
    }

    private void loadPlaces() {
        // TODO : implement places obtaining
    }

    public void onTabChange(DtlPlaceType type) {
        currentType = type;
    }

    public void setTabs() {
        view.setTypes(Arrays.asList(DtlPlaceType.PLACES, DtlPlaceType.DINING));
        view.updateSelection();
    }

    public interface View extends Presenter.View {

        void setTypes(List<DtlPlaceType> types);

        void updateSelection();
    }
}
