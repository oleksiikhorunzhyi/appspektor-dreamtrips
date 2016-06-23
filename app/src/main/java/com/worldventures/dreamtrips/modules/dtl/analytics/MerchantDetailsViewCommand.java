package com.worldventures.dreamtrips.modules.dtl.analytics;

import com.worldventures.dreamtrips.modules.dtl.service.DtlFilterMerchantInteractor;

import javax.inject.Inject;

import io.techery.janet.command.annotations.CommandAction;

@CommandAction
public class MerchantDetailsViewCommand extends DtlAnalyticsCommand {

    @Inject
    DtlFilterMerchantInteractor filterMerchantInteractor;

    private MerchantDetailsViewEvent action;

    public MerchantDetailsViewCommand(MerchantDetailsViewEvent action) {
        super(action);
        this.action = action;
    }

    @Override
    protected void run(CommandCallback<Void> callback) throws Throwable {
        filterMerchantInteractor.filterDataPipe()
                .observeSuccessWithReplay()
                .first()
                .doOnNext(filterDataAction ->
                        action.setOffersOnly(filterDataAction.getResult().isOffersOnly()))
                .subscribe(filterDataAction -> {
                    try {
                        super.run(callback);
                    } catch (Throwable throwable) {
                        callback.onFail(throwable);
                    }
                }, callback::onFail);
    }
}
