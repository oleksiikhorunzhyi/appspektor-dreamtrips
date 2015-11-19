package com.worldventures.dreamtrips.modules.dtl.presenter;

import com.worldventures.dreamtrips.modules.common.presenter.Presenter;
import com.worldventures.dreamtrips.modules.dtl.model.ContactTime;

public abstract class SuggestPlaceBasePresenter<T extends SuggestPlaceBasePresenter.View> extends Presenter<T> {

    public abstract void submitClicked();

    public void presentationClicked() {
        view.openPresentation("http://assets.wvholdings.com/1/VIDEOS/wvondemand/2015/App_Video_Teaser_FINAL_10152015/App_Video_Teaser_FINAL_10152015.mp4.mp4");
    }

    public void pdfClicked() {
        view.openPdf("http://www.scrumguides.org/docs/scrumguide/v1/scrum-guide-us.pdf");
    }

    protected ContactTime obtainContactTime() {
        if (view.intervalDate()) {
            return new ContactTime(view.getFromTimestamp(), view.getToTimestamp());
        } else {
            return new ContactTime(view.getFromTimestamp(), view.getFromTimestamp());
        }
    }

    public interface View extends Presenter.View {

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

        void showToDateError(String message);

        void showProgress();

        void hideProgress();

        void openPdf(String url);

        void openPresentation(String url);

        void merchantSubmitted();
    }
}
