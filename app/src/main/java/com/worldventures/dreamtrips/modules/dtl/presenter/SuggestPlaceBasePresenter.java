package com.worldventures.dreamtrips.modules.dtl.presenter;

import com.worldventures.dreamtrips.modules.common.presenter.Presenter;

public abstract class SuggestPlaceBasePresenter<T extends SuggestPlaceBasePresenter.View> extends Presenter<T> {

    public abstract void submitClicked();

    public void presentationClicked() {
        view.openPresentation("https://www.youtube.com/watch?v=FA_8TY9Z5Zg");
    }

    public void pdfClicked() {
        view.openPdf("https://www.google.com.ua/url?sa=t&rct=j&q=&esrc=s&source=web&cd=3&cad=rja&uact=8&ved=0CCgQFjACahUKEwiUsISjp_zIAhXp7nIKHTH0ACc&url=http%3A%2F%2Fwww.scrumguides.org%2Fdocs%2Fscrumguide%2Fv1%2Fscrum-guide-us.pdf&usg=AFQjCNHpo0uVXuTmZCtwkQwh_hjUsHin5A&sig2=F3hGZ0AlQo4VX-_rNWkVIg");
    }

    public interface View extends Presenter.View {

        String getContactName();

        String getPhone();

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
    }
}
