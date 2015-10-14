package com.worldventures.dreamtrips.modules.dtl.presenter;

import android.widget.Toast;

import com.worldventures.dreamtrips.core.utils.DateTimeUtils;
import com.worldventures.dreamtrips.modules.common.presenter.Presenter;

import java.util.Calendar;

import timber.log.Timber;

public class DtlSuggestMerchantPresenter extends Presenter<DtlSuggestMerchantPresenter.View> {

    @Override
    public void takeView(View view) {
        super.takeView(view);
        syncUi();
    }

    public void submitClicked() {
        String temp = "From Timestamp: " + view.getFromTimestamp() + "\nTo Timestamp: "
                + view.getToTimestamp() + "\nFood rating: " + view.getFoodRating()
                + "\nService rating: " + view.getServiceRating()
                + "\nCleanliness rating: " + view.getCleanlinessRating()
                + "\nUniqueness rating: " + view.getUniquenessRating();
        Toast.makeText(context, temp, Toast.LENGTH_LONG).show();
        Timber.i(temp);
    }

    private void syncUi() {
        Calendar calendar = Calendar.getInstance();

        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        onFromDateSet(year, month, day);
        onFromTimeSet(hour, minute);
        onToDateSet(year, month + 1, day);
        onToTimeSet(hour, minute);
    }

    public void onFromDateSet(int year, int month, int day) {
        String format = DateTimeUtils.convertDateToString(year, month, day);
        view.setFromDate(format);
    }

    public void onFromTimeSet(int hours, int minutes) {
        String format = DateTimeUtils.convertTimeToString(hours, minutes);
        view.setFromTime(format);
    }

    public void onToDateSet(int year, int month, int day) {
        String format = DateTimeUtils.convertDateToString(year, month, day);
        view.setToDate(format);
    }

    public void onToTimeSet(int hours, int minutes) {
        String format = DateTimeUtils.convertTimeToString(hours, minutes);
        view.setToTime(format);
    }

    private boolean validate() {
        // TODO
        return true;
    }

    public interface View extends Presenter.View {

        void setFromDate(String value);
        void setFromTime(String value);
        void setToDate(String value);
        void setToTime(String value);
        long getToTimestamp();
        long getFromTimestamp();
        int getFoodRating();
        int getServiceRating();
        int getCleanlinessRating();
        int getUniquenessRating();
    }
}
