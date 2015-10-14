package com.worldventures.dreamtrips.modules.dtl.view.fragment;

import android.os.Bundle;
import android.widget.TextView;

import com.fourmob.datetimepicker.date.DatePickerDialog;
import com.sleepbot.datetimepicker.time.RadialPickerLayout;
import com.sleepbot.datetimepicker.time.TimePickerDialog;
import com.techery.spares.annotations.Layout;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.utils.DateTimeUtils;
import com.worldventures.dreamtrips.modules.common.view.custom.DTEditText;
import com.worldventures.dreamtrips.modules.common.view.fragment.BaseFragment;
import com.worldventures.dreamtrips.modules.dtl.presenter.DtlSuggestMerchantPresenter;

import java.util.Calendar;

import butterknife.InjectView;
import butterknife.OnClick;
import io.techery.properratingbar.ProperRatingBar;

@Layout(R.layout.fragment_suggest_merchant)
public class DtlSuggestMerchantFragment
        extends BaseFragment<DtlSuggestMerchantPresenter>
        implements DtlSuggestMerchantPresenter.View, DatePickerDialog.OnDateSetListener,
        TimePickerDialog.OnTimeSetListener {

    private final String PICKER_FROM_TAG = "PICKER_FROM";
    private final String PICKER_TO_TAG = "PICKER_TO";

    /**
     * Workaround - since timePicker dialog supplies no tag in callback
     * to distinguish 'from' picker from 'to' one
     */
    private String lastTimePickerTag;

    @InjectView(R.id.restaurantName)
    DTEditText restaurantName;
    @InjectView(R.id.contactName)
    DTEditText contactName;
    @InjectView(R.id.phoneNumber)
    DTEditText phoneNumber;
    @InjectView(R.id.fromDate)
    TextView fromDate;
    @InjectView(R.id.fromTime)
    TextView fromTime;
    @InjectView(R.id.toDate)
    TextView toDate;
    @InjectView(R.id.toTime)
    TextView toTime;
    @InjectView(R.id.foodRatingBar)
    ProperRatingBar foodRatingBar;
    @InjectView(R.id.serviceRatingBar)
    ProperRatingBar serviceRatingBar;
    @InjectView(R.id.cleanlinessRatingBar)
    ProperRatingBar cleanlinessRatingBar;
    @InjectView(R.id.uniquenessRatingBar)
    ProperRatingBar uniquenessRatingBar;
    @InjectView(R.id.additionalInfo)
    DTEditText additionalInfo;

    @Override
    protected DtlSuggestMerchantPresenter createPresenter(Bundle savedInstanceState) {
        return new DtlSuggestMerchantPresenter();
    }

    @Override
    public void setFromDate(String value) {
        fromDate.setText(value);
    }

    @Override
    public void setFromTime(String value) {
        fromTime.setText(value);
    }

    @Override
    public void setToDate(String value) {
        toDate.setText(value);
    }

    @Override
    public void setToTime(String value) {
        toTime.setText(value);
    }

    @Override
    public long getToTimestamp() {
        return DateTimeUtils.mergeDateTime(toDate.getText().toString(), toTime.getText().toString()).getTime();
    }

    @Override
    public long getFromTimestamp() {
        return DateTimeUtils.mergeDateTime(fromDate.getText().toString(), fromTime.getText().toString()).getTime();
    }

    @OnClick(R.id.fromDate) void fromDateClicked() {
        Calendar calendar = Calendar.getInstance();
        DatePickerDialog datePickerDialog = DatePickerDialog.newInstance(this,
                calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), false);
        datePickerDialog.setYearRange(calendar.get(Calendar.YEAR), calendar.get(Calendar.YEAR) + 1);
        datePickerDialog.show(getChildFragmentManager(), PICKER_FROM_TAG);
    }

    @OnClick(R.id.fromTime) void fromTimeClicked() {
        Calendar calendar = Calendar.getInstance();
        TimePickerDialog timePickerDialog = TimePickerDialog.newInstance(this,
                calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), false, false);
        lastTimePickerTag = PICKER_FROM_TAG;
        timePickerDialog.show(getChildFragmentManager(), PICKER_FROM_TAG);
    }

    @OnClick(R.id.toDate) void toDateClicked() {
        Calendar calendar = Calendar.getInstance();
        DatePickerDialog datePickerDialog = DatePickerDialog.newInstance(this,
                calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.DAY_OF_MONTH), false);
        datePickerDialog.setYearRange(calendar.get(Calendar.YEAR), calendar.get(Calendar.YEAR) + 1);
        datePickerDialog.show(getChildFragmentManager(), PICKER_TO_TAG);
    }

    @OnClick(R.id.toTime) void toTimeClicked() {
        Calendar calendar = Calendar.getInstance();
        TimePickerDialog timePickerDialog = TimePickerDialog.newInstance(this,
                calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), false, false);
        lastTimePickerTag = PICKER_TO_TAG;
        timePickerDialog.show(getChildFragmentManager(), PICKER_TO_TAG);
    }

    @Override
    public void onDateSet(DatePickerDialog datePickerDialog, int year, int month, int day) {
        if (datePickerDialog.getTag().equals(PICKER_FROM_TAG)) {
            getPresenter().onFromDateSet(year, month, day);
        } else {
            getPresenter().onToDateSet(year, month, day);
        }
    }

    @Override
    public void onTimeSet(RadialPickerLayout view, int hourOfDay, int minute) {
        if (lastTimePickerTag != null && lastTimePickerTag.equals(PICKER_FROM_TAG)) {
            getPresenter().onFromTimeSet(hourOfDay, minute);
        } else if (lastTimePickerTag != null && lastTimePickerTag.equals(PICKER_TO_TAG)) {
            getPresenter().onToTimeSet(hourOfDay, minute);
        }
        lastTimePickerTag = null;
    }

    @OnClick(R.id.submit) void submitClicked() {
        getPresenter().submitClicked();
    }

    @Override
    public int getFoodRating() {
        return foodRatingBar.getRating();
    }

    @Override
    public int getServiceRating() {
        return serviceRatingBar.getRating();
    }

    @Override
    public int getCleanlinessRating() {
        return cleanlinessRatingBar.getRating();
    }

    @Override
    public int getUniquenessRating() {
        return uniquenessRatingBar.getRating();
    }
}
