package com.worldventures.dreamtrips.modules.reptools.presenter;

import com.worldventures.dreamtrips.core.utils.tracksystem.TrackingHelper;
import com.worldventures.dreamtrips.modules.dtl.api.place.SuggestPlaceCommand;
import com.worldventures.dreamtrips.modules.dtl.model.leads.DtlLead;
import com.worldventures.dreamtrips.modules.dtl.presenter.SuggestPlaceBasePresenter;

public class SuggestRestaurantPresenter extends SuggestPlaceBasePresenter<SuggestRestaurantPresenter.View> {

    @Override
    public void submitClicked() {
        view.showProgress();
        DtlLead.Builder leadBuilder = new DtlLead.Builder()
                .merchant(new DtlLead.Merchant(null, view.getRestaurantName(), view.getCity()))
                .contact(new DtlLead.Contact(view.getContactName(), view.getPhone(), obtainContactTime()))
                .rating(DtlLead.Rating.FOOD, view.getFoodRating())
                .rating(DtlLead.Rating.SERVICE, view.getServiceRating())
                .rating(DtlLead.Rating.CLEANLINESS, view.getCleanlinessRating())
                .rating(DtlLead.Rating.UNIQUENESS, view.getUniquenessRating())
                .comment(view.getAdditionalInfo());

        doRequest(new SuggestPlaceCommand(leadBuilder.build()),
                aVoid -> {
                    TrackingHelper.dtlSuggestMerchant(null);
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
