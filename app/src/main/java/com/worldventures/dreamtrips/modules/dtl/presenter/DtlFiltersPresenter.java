package com.worldventures.dreamtrips.modules.dtl.presenter;

import com.worldventures.dreamtrips.modules.common.presenter.Presenter;
import com.worldventures.dreamtrips.modules.dtl.event.DtlFilterEvent;
import com.worldventures.dreamtrips.modules.dtl.model.DtlFilterObject;

import icepick.State;

public class DtlFiltersPresenter extends Presenter<DtlFiltersPresenter.View> {

    @State
    DtlFilterObject dtlFilterObject;

    @Override
    public void takeView(View view) {
        super.takeView(view);
        if (dtlFilterObject == null) {
            dtlFilterObject = new DtlFilterObject();
        }
    }

    public void priceChanged(int left, int right) {
        dtlFilterObject.setPrice(left, right);
    }

    public void distanceChanged(int left, int right) {
        dtlFilterObject.setDistance(left, right);
    }

    public void apply() {
        eventBus.post(new DtlFilterEvent(dtlFilterObject));
    }

    public void resetAll() {
        dtlFilterObject.reset();
        eventBus.post(new DtlFilterEvent(dtlFilterObject));

    }

    public interface View extends Presenter.View {

    }
}
