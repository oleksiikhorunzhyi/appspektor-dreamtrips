package com.worldventures.dreamtrips.modules.dtl.view.fragment;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.fourmob.datetimepicker.date.DatePickerDialog;
import com.sleepbot.datetimepicker.time.RadialPickerLayout;
import com.sleepbot.datetimepicker.time.TimePickerDialog;
import com.techery.spares.annotations.Layout;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.utils.DateTimeUtils;
import com.worldventures.dreamtrips.modules.common.view.custom.DTEditText;
import com.worldventures.dreamtrips.modules.common.view.fragment.BaseFragmentWithArgs;
import com.worldventures.dreamtrips.modules.dtl.bundle.SuggestMerchantBundle;
import com.worldventures.dreamtrips.modules.dtl.presenter.DtlSuggestMerchantPresenter;
import com.worldventures.dreamtrips.modules.dtl.validator.EmptyValidator;
import com.worldventures.dreamtrips.modules.dtl.validator.InputLengthValidator;

import java.util.Calendar;

import butterknife.InjectView;
import butterknife.OnClick;
import cn.pedant.SweetAlert.SweetAlertDialog;
import io.techery.properratingbar.ProperRatingBar;

@Layout(R.layout.fragment_suggest_merchant)
public class DtlSuggestMerchantFragment
        extends BaseFragmentWithArgs<DtlSuggestMerchantPresenter, SuggestMerchantBundle>
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

    private SweetAlertDialog progressDialog;

    @Override
    protected DtlSuggestMerchantPresenter createPresenter(Bundle savedInstanceState) {
        return new DtlSuggestMerchantPresenter(getArgs());
    }

    @Override
    public void afterCreateView(View rootView) {
        super.afterCreateView(rootView);
        contactName.addValidator(new EmptyValidator(getString(R.string.dtl_field_validation_empty_input_error)));
        phoneNumber.addValidator(new EmptyValidator(getString(R.string.dtl_field_validation_empty_input_error)));
        additionalInfo.addValidator(new InputLengthValidator(120,
                getString(R.string.suggest_merchant_additional_info_length_error)));
        //
        progressDialog = new SweetAlertDialog(getActivity(), SweetAlertDialog.PROGRESS_TYPE);
        progressDialog.getProgressHelper().setBarColor(getResources().getColor(R.color.theme_main));
        progressDialog.setTitleText(getString(R.string.pleasewait));
        progressDialog.setCancelable(false);
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
            fromDate.setText(DateTimeUtils.convertDateToString(year, month, day));
        } else {
            toDate.setText(DateTimeUtils.convertDateToString(year, month, day));
        }
    }

    @Override
    public void onTimeSet(RadialPickerLayout view, int hourOfDay, int minute) {
        if (lastTimePickerTag != null && lastTimePickerTag.equals(PICKER_FROM_TAG)) {
            fromTime.setText(DateTimeUtils.convertTimeToString(hourOfDay, minute));
        } else if (lastTimePickerTag != null && lastTimePickerTag.equals(PICKER_TO_TAG)) {
            toTime.setText(DateTimeUtils.convertTimeToString(hourOfDay, minute));
            toTime.setError(null);
        }
        lastTimePickerTag = null;
    }

    @OnClick(R.id.submit) void submitClicked() {
        if (contactName.validate() && phoneNumber.validate() && additionalInfo.validate()) {
            getPresenter().submitClicked();
        }
    }

    @Override
    public void setPlaceName(String placeName) {
        restaurantName.setText(placeName);
    }

    @Override
    public String getContactName() {
        return contactName.getText().toString().trim();
    }

    @Override
    public String getPhone() {
        return phoneNumber.getText().toString().trim();
    }

    @Override
    public void setFromDate(int year, int month, int day) {
        fromDate.setText(formatDate(year, month, day));
    }

    @Override
    public void setFromTime(int hours, int minutes) {
        fromTime.setText(formatTime(hours, minutes));
    }

    @Override
    public void setToDate(int year, int month, int day) {
        toDate.setText(formatDate(year, month, day));
    }

    @Override
    public void setToTime(int hours, int minutes) {
        toTime.setText(formatTime(hours, minutes));
    }

    @Override
    public long getToTimestamp() {
        return DateTimeUtils.mergeDateTime(toDate.getText().toString(), toTime.getText().toString()).getTime();
    }

    @Override
    public long getFromTimestamp() {
        return DateTimeUtils.mergeDateTime(fromDate.getText().toString(), fromTime.getText().toString()).getTime();
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

    @Override
    public String getAdditionalInfo() {
        return additionalInfo.getText().toString().trim();
    }

    @Override
    public void showToDateError(String message) {
        toDate.setError(message);
    }

    @Override
    public void showProgress() {
        progressDialog.show();
    }

    @Override
    public void hideProgress() {
        progressDialog.dismissWithAnimation();
    }

    @Override
    public void dismiss() {
        getActivity().onBackPressed();
    }

    private String formatDate(int year, int month, int day) {
        return DateTimeUtils.convertDateToString(year, month, day);
    }

    private String formatTime(int hours, int minutes) {
        return DateTimeUtils.convertTimeToString(hours, minutes);
    }
}
