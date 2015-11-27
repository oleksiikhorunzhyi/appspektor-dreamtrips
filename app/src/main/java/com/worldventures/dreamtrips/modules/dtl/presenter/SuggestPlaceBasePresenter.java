package com.worldventures.dreamtrips.modules.dtl.presenter;

import com.octo.android.robospice.persistence.exception.SpiceException;
import com.worldventures.dreamtrips.modules.common.presenter.ApiErrorPresenter;
import com.worldventures.dreamtrips.modules.common.presenter.Presenter;
import com.worldventures.dreamtrips.modules.common.view.ApiErrorView;
import com.worldventures.dreamtrips.modules.dtl.model.ContactTime;

public abstract class SuggestPlaceBasePresenter<T extends SuggestPlaceBasePresenter.View> extends Presenter<T> {

    public static final String PROMO_VIDEO = "http://assets.wvholdings.com/1/ASSETS/DTL-1500400_MerchantToMerchant_Phase2_11102015.mp4";
    public static final String PDF = "http://assets.wvholdings.com/1/ASSETS/DT_1500159_06_DTL_Merchant_Flyer_Oct_Update_LR.pdf";

    private ApiErrorPresenter apiErrorPresenter;

    public SuggestPlaceBasePresenter() {
        apiErrorPresenter = new ApiErrorPresenter();
    }

    public abstract void submitClicked();

    @Override
    public void takeView(T view) {
        super.takeView(view);
        apiErrorPresenter.setView(view);
    }

    public void presentationClicked() {
        view.openPresentation(PROMO_VIDEO);
    }

    public void pdfClicked() {
        view.openPdf(PDF);
    }

    protected ContactTime obtainContactTime() {
        if (view.intervalDate()) {
            return new ContactTime(view.getFromTimestamp(), view.getToTimestamp());
        } else {
            return new ContactTime(view.getFromTimestamp(), view.getFromTimestamp());
        }
    }

    @Override
    public void handleError(SpiceException error) {
        apiErrorPresenter.handleError(error);
    }

    public interface View extends ApiErrorView {

        String getContactName();

        String getPhone();

        boolean intervalDate();

        long getToTimestamp();

        long getFromTimestamp();

        int getFoodRating();

        int getServiceRating();

        int getCleanlinessRating();

        int getUniquenessRating();

        String getAdditionalInfo();

        void showContactTimeFieldError(String message);

        void showProgress();

        void hideProgress();

        void openPdf(String url);

        void openPresentation(String url);

        void merchantSubmitted();
    }
}
