package com.worldventures.dreamtrips.modules.dtl.presenter;

import com.worldventures.dreamtrips.core.utils.tracksystem.TrackingHelper;
import com.worldventures.dreamtrips.modules.dtl.api.place.SuggestPlaceCommand;
import com.worldventures.dreamtrips.modules.dtl.bundle.SuggestPlaceBundle;
import com.worldventures.dreamtrips.modules.dtl.model.leads.DtlLead;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.DtlMerchant;

public class DtlSuggestMerchantPresenter extends SuggestPlaceBasePresenter<DtlSuggestMerchantPresenter.View> {

    private DtlMerchant place;

    public DtlSuggestMerchantPresenter(SuggestPlaceBundle data) {
        place = data.getPlace();
    }

    @Override
    public void takeView(View view) {
        super.takeView(view);
        view.syncUiWithPlace(place);
        TrackingHelper.dtlSuggestMerchantView();
    }

    @Override
    public void submitClicked() {
        view.showProgress();
        DtlLead.Builder leadBuilder = new DtlLead.Builder()
                .merchant(new DtlLead.Merchant(place.getId(), place.getDisplayName(), place.getCity()))
                .contact(new DtlLead.Contact(view.getContactName(), view.getPhone(), obtainContactTime()))
                .rating(DtlLead.Rating.FOOD, view.getFoodRating())
                .rating(DtlLead.Rating.SERVICE, view.getServiceRating())
                .rating(DtlLead.Rating.CLEANLINESS, view.getCleanlinessRating())
                .rating(DtlLead.Rating.UNIQUENESS, view.getUniquenessRating())
                .comment(view.getAdditionalInfo());

        doRequest(new SuggestPlaceCommand(leadBuilder.build()),
                aVoid -> {
                    TrackingHelper.dtlSuggestMerchant(place);
                    view.hideProgress();
                    view.merchantSubmitted();
                },
                spiceException -> {
                    super.handleError(spiceException);
                    view.hideProgress();
                });
    }

    public interface View extends SuggestPlaceBasePresenter.View {

        void syncUiWithPlace(DtlMerchant place);

    }
}
