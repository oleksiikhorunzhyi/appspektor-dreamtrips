package com.worldventures.dreamtrips.modules.reptools.presenter;

import com.worldventures.dreamtrips.modules.dtl.api.place.SuggestPlaceCommand;
import com.worldventures.dreamtrips.modules.dtl.model.RateContainer;
import com.worldventures.dreamtrips.modules.dtl.model.SuggestPlacePostData;
import com.worldventures.dreamtrips.modules.dtl.presenter.SuggestPlaceBasePresenter;

public class SuggestRestaurantPresenter extends SuggestPlaceBasePresenter<SuggestRestaurantPresenter.View> {

    @Override
    public void submitClicked() {
        view.showProgress();
        doRequest(new SuggestPlaceCommand(new SuggestPlacePostData(view.getRestaurantName(),
                        view.getCity(), view.getContactName(), view.getPhone(),
                        obtainContactTime(),
                        new RateContainer(view.getFoodRating(), view.getServiceRating(),
                                view.getCleanlinessRating(), view.getUniquenessRating()),
                        view.getAdditionalInfo())),
                aVoid -> {
                    view.merchantSubmitted();
                    view.hideProgress();
                    view.clearInput();
                },
                spiceException -> {
                    super.handleError(spiceException);
                    view.hideProgress();
                });
    }

    public interface View extends SuggestPlaceBasePresenter.View {

        String getRestaurantName();

        String getCity();

        void clearInput();
    }
}