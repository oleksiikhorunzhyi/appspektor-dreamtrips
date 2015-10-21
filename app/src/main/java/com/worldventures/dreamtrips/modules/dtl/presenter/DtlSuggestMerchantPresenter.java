package com.worldventures.dreamtrips.modules.dtl.presenter;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.common.presenter.Presenter;
import com.worldventures.dreamtrips.modules.dtl.api.SuggestPlaceCommand;
import com.worldventures.dreamtrips.modules.dtl.bundle.SuggestMerchantBundle;
import com.worldventures.dreamtrips.modules.dtl.model.ContactTime;
import com.worldventures.dreamtrips.modules.dtl.model.DtlPlace;
import com.worldventures.dreamtrips.modules.dtl.model.RateContainer;
import com.worldventures.dreamtrips.modules.dtl.model.SuggestPlacePostData;

import java.util.Calendar;

public class DtlSuggestMerchantPresenter extends Presenter<DtlSuggestMerchantPresenter.View> {

    private DtlPlace place;

    public DtlSuggestMerchantPresenter(SuggestMerchantBundle data) {
        place = data.getPlace();
    }

    @Override
    public void takeView(View view) {
        super.takeView(view);
        syncUi();
    }

    public void submitClicked() {
        if (validate()) {
            view.showProgress();
            doRequest(new SuggestPlaceCommand(new SuggestPlacePostData(place.getId(),
                            view.getContactName(), view.getPhone(),
                            new ContactTime(view.getFromTimestamp(), view.getToTimestamp()),
                            new RateContainer(view.getFoodRating(), view.getServiceRating(),
                                    view.getCleanlinessRating(), view.getUniquenessRating()),
                            view.getAdditionalInfo())),
                    aVoid -> {
                        view.hideProgress();
                        view.dismiss();
                    },
                    spiceException -> {
                        super.handleError(spiceException);
                        view.hideProgress();
                    });
        }
    }

    private void syncUi() {
        Calendar calendar = Calendar.getInstance();

        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        view.setFromDate(year, month, day);
        view.setFromTime(hour, minute);
        view.setToDate(year, month + 1, day);
        view.setToTime(hour, minute);

        view.setPlaceName(place.getName());
    }

    private boolean validate() {
        long from = view.getFromTimestamp(), to = view.getToTimestamp();
        if (from > to) {
            view.showToDateError(context.getString(R.string.suggest_merchant_to_date_overlap_error));
            return false;
        }
        if (to < System.currentTimeMillis()) {
            view.showToDateError(context.getString(R.string.suggest_merchant_to_date_past_error));
            return false;
        }
        return true;
    }

    public interface View extends Presenter.View {

        void setFromDate(int year, int month, int day);

        void setFromTime(int hours, int minutes);

        void setToDate(int year, int month, int day);

        void setToTime(int hours, int minutes);

        long getToTimestamp();

        long getFromTimestamp();

        int getFoodRating();

        int getServiceRating();

        int getCleanlinessRating();

        int getUniquenessRating();

        String getContactName();

        String getPhone();

        String getAdditionalInfo();

        void setPlaceName(String placeName);

        void showToDateError(String message);

        void showProgress();

        void hideProgress();

        void dismiss();
    }
}
