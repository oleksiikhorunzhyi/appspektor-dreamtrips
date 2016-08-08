package com.worldventures.dreamtrips.modules.dtl.analytics;

import com.innahema.collections.query.queriables.Queryable;
import com.worldventures.dreamtrips.core.janet.dagger.InjectableAction;
import com.worldventures.dreamtrips.core.utils.tracksystem.AnalyticsInteractor;
import com.worldventures.dreamtrips.modules.dtl.helper.DtlLocationHelper;
import com.worldventures.dreamtrips.modules.dtl.model.LocationSourceType;
import com.worldventures.dreamtrips.modules.dtl.model.location.DtlManualLocation;
import com.worldventures.dreamtrips.modules.dtl.model.location.ImmutableDtlManualLocation;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.DtlMerchant;
import com.worldventures.dreamtrips.modules.dtl.service.DtlLocationInteractor;
import com.worldventures.dreamtrips.modules.dtl.service.DtlMerchantInteractor;
import com.worldventures.dreamtrips.modules.dtl.service.action.DtlLocationCommand;
import com.worldventures.dreamtrips.modules.dtl.service.action.DtlMerchantsAction;

import javax.inject.Inject;

import io.techery.janet.Command;
import io.techery.janet.command.annotations.CommandAction;

@CommandAction
public class DtlAnalyticsCommand extends Command<Void> implements InjectableAction {

    @Inject
    protected AnalyticsInteractor analyticsInteractor;
    @Inject
    protected DtlLocationInteractor dtlLocationInteractor;
    @Inject
    protected DtlMerchantInteractor merchantInteractor;

    private final DtlAnalyticsAction action;

    public static DtlAnalyticsCommand create(DtlAnalyticsAction action) {
        return new DtlAnalyticsCommand(action);
    }

    public DtlAnalyticsCommand(DtlAnalyticsAction action) {
        this.action = action;
    }

    @Override
    protected void run(CommandCallback<Void> callback) throws Throwable {
        dtlLocationInteractor.locationPipe()
                .createObservableResult(DtlLocationCommand.last())
                .map(DtlLocationCommand::getResult)
                .map(dtlLocation -> {
                    if (dtlLocation.getLocationSourceType() == LocationSourceType.EXTERNAL) {
                        action.setAnalyticsLocation(dtlLocation);
                    } else {
                        merchantInteractor.merchantsActionPipe().observeSuccessWithReplay()
                                .map(DtlMerchantsAction::getResult)
                                .map(merchants -> {
                                    return Queryable.from(merchants)
                                            .map(merchant -> {
                                                merchant.setDistance(DtlLocationHelper.calculateDistance(
                                                        dtlLocation.getCoordinates().asLatLng(),
                                                        merchant.getCoordinates().asLatLng()));
                                                return merchant;
                                            })
                                            .sort(DtlMerchant.DISTANCE_COMPARATOR::compare)
                                            .first();
                                })
                                .map(dtlMerchant -> {
                                    return ImmutableDtlManualLocation
                                            .copyOf((DtlManualLocation) dtlLocation)
                                            .withAnalyticsName(dtlMerchant.getAnalyticsName());
                                })
                                .subscribe(dtlLocation1 ->
                                                action.setAnalyticsLocation(dtlLocation1),
                                        throwable -> {
                                        });
                    }
                    return action;
                })
                .flatMap(action -> analyticsInteractor.analyticsActionPipe()
                        .createObservableResult(action))
                .map(baseAnalyticsAction -> (Void) null)
                .subscribe(callback::onSuccess, callback::onFail);
    }
}
