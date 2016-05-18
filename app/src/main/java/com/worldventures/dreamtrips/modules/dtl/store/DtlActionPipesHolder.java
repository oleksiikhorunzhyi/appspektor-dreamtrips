package com.worldventures.dreamtrips.modules.dtl.store;

import com.worldventures.dreamtrips.modules.dtl.action.DtlEarnPointsAction;
import com.worldventures.dreamtrips.modules.dtl.action.DtlEstimatePointsAction;
import com.worldventures.dreamtrips.modules.dtl.action.DtlLocationCommand;
import com.worldventures.dreamtrips.modules.dtl.action.DtlNearbyLocationAction;
import com.worldventures.dreamtrips.modules.dtl.action.DtlRateAction;
import com.worldventures.dreamtrips.modules.dtl.action.DtlSearchLocationAction;

import io.techery.janet.ActionPipe;
import io.techery.janet.Janet;
import io.techery.janet.helper.ActionStateSubscriber;
import rx.schedulers.Schedulers;

public class DtlActionPipesHolder {

    public final ActionPipe<DtlEstimatePointsAction> estimatePointsActionPipe;

    public final ActionPipe<DtlRateAction> rateActionPipe;

    public final ActionPipe<DtlEarnPointsAction> earnPointsActionPipe;

    public final ActionPipe<DtlNearbyLocationAction> nearbyLocationPipe;

    public final ActionPipe<DtlSearchLocationAction> searchLocationPipe;

    public final ActionPipe<DtlLocationCommand> locationPipe;

    public DtlActionPipesHolder(Janet janet) {
        estimatePointsActionPipe = janet.createPipe(DtlEstimatePointsAction.class, Schedulers.io());
        rateActionPipe = janet.createPipe(DtlRateAction.class, Schedulers.io());
        earnPointsActionPipe = janet.createPipe(DtlEarnPointsAction.class, Schedulers.io());
        nearbyLocationPipe = janet.createPipe(DtlNearbyLocationAction.class, Schedulers.io());
        searchLocationPipe = janet.createPipe(DtlSearchLocationAction.class, Schedulers.io());
        locationPipe = janet.createPipe(DtlLocationCommand.class, Schedulers.io());
        //
        searchLocationPipe.observe()
                .subscribe(new ActionStateSubscriber<DtlSearchLocationAction>()
                .onStart(action -> {
                    nearbyLocationPipe.cancelLatest();
                }));
    }
}
