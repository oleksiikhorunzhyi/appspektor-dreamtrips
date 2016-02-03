package com.worldventures.dreamtrips.modules.dtl.presenter;

import com.worldventures.dreamtrips.modules.common.presenter.Presenter;
import com.worldventures.dreamtrips.modules.dtl.delegate.DtlFilterDelegate;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.filter.DtlFilterData;

import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

public class DtlDistancePresenter extends Presenter<DtlDistancePresenter.View> {

    @Inject
    DtlFilterDelegate dtlFilterDelegate;

    @Override
    public void takeView(View view) {
        super.takeView(view);
        view.attachDistance(Arrays.asList(DtlFilterData.DistanceType.values()));
    }

    public interface View extends Presenter.View {
        void attachDistance(List<DtlFilterData.DistanceType> distanceTypes);
    }
}
