package com.worldventures.dreamtrips.modules.dtl.presenter;

import com.worldventures.dreamtrips.core.utils.tracksystem.TrackingHelper;
import com.worldventures.dreamtrips.modules.dtl.api.merchant.SuggestRestaurantCommand;
import com.worldventures.dreamtrips.modules.dtl.bundle.MerchantIdBundle;
import com.worldventures.dreamtrips.modules.dtl.model.leads.DtlLead;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.DtlMerchant;
import com.worldventures.dreamtrips.modules.dtl.store.DtlMerchantRepository;

import javax.inject.Inject;

public class DtlSuggestMerchantPresenter extends SuggestRestaurantBasePresenter<DtlSuggestMerchantPresenter.View> {

    private String merchantId;
    private DtlMerchant dtlMerchant;

    @Inject
    DtlMerchantRepository dtlMerchantRepository;

    public DtlSuggestMerchantPresenter(MerchantIdBundle data) {
        merchantId = data.getMerchantId();
    }

    @Override
    public void onInjected() {
        super.onInjected();
        dtlMerchant = dtlMerchantRepository.getMerchantById(merchantId);
    }

    @Override
    public void takeView(View view) {
        super.takeView(view);
        view.syncUiWithMerchant(dtlMerchant);
        TrackingHelper.dtlSuggestMerchantView();
    }

    @Override
    public void submitClicked() {
        view.showProgress();
        DtlLead.Builder leadBuilder = new DtlLead.Builder()
                .merchant(new DtlLead.Merchant(dtlMerchant.getId(), dtlMerchant.getDisplayName(), dtlMerchant.getCity()))
                .contact(new DtlLead.Contact(view.getContactName(), view.getPhone(),
                        view.getContactEmail(), obtainContactTime()))
                .rating(DtlLead.Rating.FOOD, view.getFoodRating())
                .rating(DtlLead.Rating.SERVICE, view.getServiceRating())
                .rating(DtlLead.Rating.CLEANLINESS, view.getCleanlinessRating())
                .rating(DtlLead.Rating.UNIQUENESS, view.getUniquenessRating())
                .comment(view.getAdditionalInfo());

        doRequest(new SuggestRestaurantCommand(leadBuilder.build()),
                aVoid -> {
                    TrackingHelper.dtlSuggestMerchant(dtlMerchant);
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
