package com.worldventures.dreamtrips.modules.dtl.presenter;

import android.support.v4.app.FragmentManager;

import com.worldventures.dreamtrips.core.navigation.NavigationBuilder;
import com.worldventures.dreamtrips.core.navigation.Route;
import com.worldventures.dreamtrips.modules.common.presenter.Presenter;
import com.worldventures.dreamtrips.modules.dtl.bundle.PointsEstimationDialogBundle;
import com.worldventures.dreamtrips.modules.dtl.model.DtlPlace;

public class DtlPlaceDetailsPresenter extends Presenter<DtlPlaceDetailsPresenter.View> {

    private final DtlPlace place;

    public DtlPlaceDetailsPresenter(DtlPlace place) {
        this.place = place;
    }

    @Override
    public void takeView(View view) {
        super.takeView(view);
        view.setPlace(place);
    }

    public void onEstimationClick(FragmentManager fm) {
        NavigationBuilder.create()
                .forDialog(fm)
                .data(new PointsEstimationDialogBundle(place.getId()))
                .move(Route.DTL_POINTS_ESTIMATION);
    }

    public interface View extends Presenter.View {
        void setPlace(DtlPlace place);
    }
}
