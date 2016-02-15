package com.worldventures.dreamtrips.modules.dtl.presenter;

import com.worldventures.dreamtrips.core.rx.RxView;
import com.worldventures.dreamtrips.modules.common.presenter.JobPresenter;
import com.worldventures.dreamtrips.modules.common.view.ApiErrorView;
import com.worldventures.dreamtrips.modules.dtl.model.leads.DtlLeadContactTime;
import com.worldventures.dreamtrips.modules.dtl.store.DtlJobManager;

import javax.inject.Inject;

public abstract class SuggestRestaurantBasePresenter<T extends SuggestRestaurantBasePresenter.View> extends JobPresenter<T> {

    public static final String PROMO_VIDEO = "http://assets.wvholdings.com/1/ASSETS/DTL-1500400_MerchantToMerchant_Phase2_11102015.mp4";
    public static final String PDF = "http://assets.wvholdings.com/1/ASSETS/DT_1500159_06_DTL_Merchant_Flyer_Oct_Update_LR.pdf";

    @Inject
    protected DtlJobManager jobManager;

    @Override
    public void takeView(T view) {
        super.takeView(view);
        apiErrorPresenter.setView(view);
        bindApiJob();
    }

    protected void bindApiJob() {
        bindJobCached(jobManager.suggestLeadExecutor)
                .onProgress(view::showProgress)
                .onSuccess(aVoid -> onLeadSuggested())
                .onError(throwable -> apiErrorPresenter.handleError(throwable));
    }

    public abstract void submitClicked();

    public abstract void onLeadSuggested();

    public void presentationClicked() {
        view.openPresentation(PROMO_VIDEO);
    }

    public void pdfClicked() {
        view.openPdf(PDF);
    }

    protected DtlLeadContactTime obtainContactTime() {
        if (view.intervalDate()) {
            return new DtlLeadContactTime(view.getFromTimestamp(), view.getToTimestamp());
        } else {
            return new DtlLeadContactTime(view.getFromTimestamp(), view.getFromTimestamp());
        }
    }

    public interface View extends RxView, ApiErrorView {

        String getContactName();

        String getPhone();

        String getContactEmail();

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
