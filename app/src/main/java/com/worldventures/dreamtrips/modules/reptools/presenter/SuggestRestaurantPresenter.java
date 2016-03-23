package com.worldventures.dreamtrips.modules.reptools.presenter;

import com.worldventures.dreamtrips.core.utils.tracksystem.TrackingHelper;
import com.worldventures.dreamtrips.modules.dtl.model.leads.DtlLead;
import com.worldventures.dreamtrips.modules.dtl.presenter.SuggestRestaurantBasePresenter;

public class SuggestRestaurantPresenter extends SuggestRestaurantBasePresenter<SuggestRestaurantPresenter.View> {

    @Override
    public void submitClicked() {
        DtlLead.Builder leadBuilder = new DtlLead.Builder()
                .merchant(new DtlLead.Merchant(null, view.getRestaurantName(), view.getCity()))
                .contact(new DtlLead.Contact(view.getContactName(), view.getPhone(),
                        view.getContactEmail(), obtainContactTime()))
                .rating(DtlLead.Rating.FOOD, view.getFoodRating())
                .rating(DtlLead.Rating.SERVICE, view.getServiceRating())
                .rating(DtlLead.Rating.CLEANLINESS, view.getCleanlinessRating())
                .rating(DtlLead.Rating.UNIQUENESS, view.getUniquenessRating())
                .comment(view.getAdditionalInfo());
        //
        jobManager.suggestLeadExecutor.createJobWith(leadBuilder.build()).subscribe();
    }

    @Override
    public void onLeadSuggested() {
        TrackingHelper.dtlSuggestMerchant(null);
        view.merchantSubmitted();
        view.hideProgress();
        view.clearInput();
    }

    public interface View extends SuggestRestaurantBasePresenter.View {

        String getRestaurantName();

        String getCity();

        void clearInput();
    }
}
