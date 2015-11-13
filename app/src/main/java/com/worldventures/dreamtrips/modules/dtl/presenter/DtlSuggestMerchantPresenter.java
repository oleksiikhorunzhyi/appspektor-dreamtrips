package com.worldventures.dreamtrips.modules.dtl.presenter;

import com.worldventures.dreamtrips.modules.dtl.api.place.SuggestPlaceCommand;
import com.worldventures.dreamtrips.modules.dtl.bundle.SuggestPlaceBundle;
import com.worldventures.dreamtrips.modules.dtl.model.DtlPlace;
import com.worldventures.dreamtrips.modules.dtl.model.RateContainer;
import com.worldventures.dreamtrips.modules.dtl.model.SuggestPlacePostData;

public class DtlSuggestMerchantPresenter extends SuggestPlaceBasePresenter<DtlSuggestMerchantPresenter.View> {

    private DtlPlace place;

    public DtlSuggestMerchantPresenter(SuggestPlaceBundle data) {
        place = data.getPlace();
    }

    @Override
    public void takeView(View view) {
        super.takeView(view);
        view.syncUiWithPlace(place);
    }

    @Override
    public void submitClicked() {
        view.showProgress();
        doRequest(new SuggestPlaceCommand(new SuggestPlacePostData(place.getMerchantId(),
                        view.getContactName(), view.getPhone(),
                        obtainContactTime(),
                        new RateContainer(view.getFoodRating(), view.getServiceRating(),
                                view.getCleanlinessRating(), view.getUniquenessRating()),
                        view.getAdditionalInfo())),
                aVoid -> {
                    view.hideProgress();
                    view.merchantSubmitted();
                },
                spiceException -> {
                    super.handleError(spiceException);
                    view.hideProgress();
                });
    }

    public interface View extends SuggestPlaceBasePresenter.View {

        void syncUiWithPlace(DtlPlace place);

    }
}
