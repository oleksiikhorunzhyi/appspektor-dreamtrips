package com.worldventures.dreamtrips.modules.dtl.presenter;

import com.worldventures.dreamtrips.modules.common.presenter.Presenter;

public abstract class SuggestPlaceBasePresenter<T extends SuggestPlaceBasePresenter.View> extends Presenter<T> {

    public abstract void submitClicked();

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
    }
}
