package com.worldventures.dreamtrips.modules.dtl.presenter;

import com.worldventures.dreamtrips.core.utils.tracksystem.TrackingHelper;
import com.worldventures.dreamtrips.modules.dtl.api.merchant.SuggestRestaurantCommand;
import com.worldventures.dreamtrips.modules.dtl.bundle.SuggestMerchantBundle;
import com.worldventures.dreamtrips.modules.dtl.model.leads.DtlLead;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.DtlMerchant;

public class DtlSuggestMerchantPresenter extends SuggestRestaurantBasePresenter<DtlSuggestMerchantPresenter.View> {

    private DtlMerchant merchant;

    public DtlSuggestMerchantPresenter(SuggestMerchantBundle data) {
        merchant = data.getMerchant();
    }

    @Override
    public void takeView(View view) {
        super.takeView(view);
        view.syncUiWithMerchant(merchant);
        TrackingHelper.dtlSuggestMerchantView();
    }

    @Override
    public void submitClicked() {
        view.showProgress();
        DtlLead.Builder leadBuilder = new DtlLead.Builder()
                .merchant(new DtlLead.Merchant(merchant.getId(), merchant.getDisplayName(), merchant.getCity()))
                .contact(new DtlLead.Contact(view.getContactName(), view.getPhone(), obtainContactTime()))
                .rating(DtlLead.Rating.FOOD, view.getFoodRating())
                .rating(DtlLead.Rating.SERVICE, view.getServiceRating())
                .rating(DtlLead.Rating.CLEANLINESS, view.getCleanlinessRating())
                .rating(DtlLead.Rating.UNIQUENESS, view.getUniquenessRating())
                .comment(view.getAdditionalInfo());

        doRequest(new SuggestRestaurantCommand(leadBuilder.build()),
                aVoid -> {
                    TrackingHelper.dtlSuggestMerchant(merchant);
                    view.hideProgress();
                    view.merchantSubmitted();
                },
                spiceException -> {
                    super.handleError(spiceException);
                    view.hideProgress();
                });
    }

    public interface View extends SuggestRestaurantBasePresenter.View {

        void syncUiWithMerchant(DtlMerchant merchant);

    }
}
