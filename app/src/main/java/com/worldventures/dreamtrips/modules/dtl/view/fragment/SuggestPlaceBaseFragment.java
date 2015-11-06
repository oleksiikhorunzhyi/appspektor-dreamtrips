package com.worldventures.dreamtrips.modules.dtl.view.fragment;

import android.content.Intent;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.fourmob.datetimepicker.date.DatePickerDialog;
import com.sleepbot.datetimepicker.time.RadialPickerLayout;
import com.sleepbot.datetimepicker.time.TimePickerDialog;
import com.techery.spares.annotations.Layout;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.utils.DateTimeUtils;
import com.worldventures.dreamtrips.core.utils.IntentUtils;
import com.worldventures.dreamtrips.modules.common.view.custom.DTEditText;
import com.worldventures.dreamtrips.modules.common.view.dialog.ProgressDialogFragment;
import com.worldventures.dreamtrips.modules.common.view.fragment.BaseFragmentWithArgs;
import com.worldventures.dreamtrips.modules.dtl.bundle.SuggestPlaceBundle;
import com.worldventures.dreamtrips.modules.dtl.presenter.SuggestPlaceBasePresenter;
import com.worldventures.dreamtrips.modules.dtl.validator.EmptyValidator;
import com.worldventures.dreamtrips.modules.dtl.validator.InputLengthValidator;

import java.util.Calendar;

import butterknife.InjectView;
import butterknife.OnClick;
import io.techery.properratingbar.ProperRatingBar;

@Layout(R.layout.fragment_suggest_merchant)
public abstract class SuggestPlaceBaseFragment<T extends SuggestPlaceBasePresenter>
        extends BaseFragmentWithArgs<T, SuggestPlaceBundle>
        implements SuggestPlaceBasePresenter.View, DatePickerDialog.OnDateSetListener,
        TimePickerDialog.OnTimeSetListener {

    protected final String PICKER_FROM_TAG = "PICKER_FROM";
    protected final String PICKER_TO_TAG = "PICKER_TO";

    /**
     * Workaround - since timePicker dialog supplies no tag in callback
     * to distinguish 'from' picker from 'to' one
     */
    protected String lastTimePickerTag;

    @InjectView(R.id.restaurantName)
    protected DTEditText restaurantName;
    @InjectView(R.id.city)
    protected DTEditText city;
    @InjectView(R.id.contactName)
    protected DTEditText contactName;
    @InjectView(R.id.phoneNumber)
    protected DTEditText phoneNumber;
    @InjectView(R.id.fromDate)
    protected TextView fromDate;
    @InjectView(R.id.fromTime)
    protected TextView fromTime;
    @InjectView(R.id.toDate)
    protected TextView toDate;
    @InjectView(R.id.toTime)
    protected TextView toTime;
    @InjectView(R.id.foodRatingBar)
    protected ProperRatingBar foodRatingBar;
    @InjectView(R.id.serviceRatingBar)
    protected ProperRatingBar serviceRatingBar;
    @InjectView(R.id.cleanlinessRatingBar)
    protected ProperRatingBar cleanlinessRatingBar;
    @InjectView(R.id.uniquenessRatingBar)
    protected ProperRatingBar uniquenessRatingBar;
    @InjectView(R.id.additionalInfo)
    protected DTEditText additionalInfo;

    protected ProgressDialogFragment progressDialog;

    @Override
    public void afterCreateView(View rootView) {
        super.afterCreateView(rootView);
        addValidators();
        setDateTime();
        progressDialog = ProgressDialogFragment.create();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_pdf:
                getPresenter().pdfClicked();
                break;
            case R.id.action_presentation:
                getPresenter().presentationClicked();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    protected void addValidators() {
        contactName.addValidator(new EmptyValidator(getString(R.string.dtl_field_validation_empty_input_error)));
        phoneNumber.addValidator(new EmptyValidator(getString(R.string.dtl_field_validation_empty_input_error)));
        additionalInfo.addValidator(new InputLengthValidator(120,
                getString(R.string.suggest_merchant_additional_info_length_error)));
    }

    protected void setDateTime() {
        Calendar calendar = Calendar.getInstance();

        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        fromDate.setText(formatDate(year, month, day));
        fromTime.setText(formatTime(hour, minute));
        toDate.setText(formatDate(year, month + 1, day));
        toTime.setText(formatTime(hour, minute));
    }

    @OnClick(R.id.fromDate)
    void fromDateClicked() {
        Calendar calendar = Calendar.getInstance();
        DatePickerDialog datePickerDialog = DatePickerDialog.newInstance(this,
                calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), false);
        datePickerDialog.setYearRange(calendar.get(Calendar.YEAR), calendar.get(Calendar.YEAR) + 1);
        datePickerDialog.show(getChildFragmentManager(), PICKER_FROM_TAG);
    }

    @OnClick(R.id.fromTime)
    void fromTimeClicked() {
        Calendar calendar = Calendar.getInstance();
        TimePickerDialog timePickerDialog = TimePickerDialog.newInstance(this,
                calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), false, false);
        lastTimePickerTag = PICKER_FROM_TAG;
        timePickerDialog.show(getChildFragmentManager(), PICKER_FROM_TAG);
    }

    @OnClick(R.id.toDate)
    void toDateClicked() {
        Calendar calendar = Calendar.getInstance();
        DatePickerDialog datePickerDialog = DatePickerDialog.newInstance(this,
                calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.DAY_OF_MONTH), false);
        datePickerDialog.setYearRange(calendar.get(Calendar.YEAR), calendar.get(Calendar.YEAR) + 1);
        datePickerDialog.show(getChildFragmentManager(), PICKER_TO_TAG);
    }

    @OnClick(R.id.toTime)
    void toTimeClicked() {
        Calendar calendar = Calendar.getInstance();
        TimePickerDialog timePickerDialog = TimePickerDialog.newInstance(this,
                calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), false, false);
        lastTimePickerTag = PICKER_TO_TAG;
        timePickerDialog.show(getChildFragmentManager(), PICKER_TO_TAG);
    }

    @Override
    public void onDateSet(DatePickerDialog datePickerDialog, int year, int month, int day) {
        if (datePickerDialog.getTag().equals(PICKER_FROM_TAG)) {
            fromDate.setText(formatDate(year, month, day));
        } else {
            toDate.setText(formatDate(year, month, day));
        }
    }

    @Override
    public void onTimeSet(RadialPickerLayout view, int hourOfDay, int minute) {
        if (lastTimePickerTag != null && lastTimePickerTag.equals(PICKER_FROM_TAG)) {
            fromTime.setText(formatTime(hourOfDay, minute));
        } else if (lastTimePickerTag != null && lastTimePickerTag.equals(PICKER_TO_TAG)) {
            toTime.setText(formatTime(hourOfDay, minute));
            toTime.setError(null);
        }
        lastTimePickerTag = null;
    }

    @OnClick(R.id.submit)
    void submitClicked() {
        if (validateInput() && validateDateTime()) {
            getPresenter().submitClicked();
        }
    }

    protected boolean validateInput() {
        return contactName.validate() && phoneNumber.validate() && additionalInfo.validate()
                && phoneNumber.isCharactersCountValid();
    }

    protected boolean validateDateTime() {
        long from = getFromTimestamp(), to = getToTimestamp();
        if (from > to) {
            showToDateError(getString(R.string.suggest_merchant_to_date_overlap_error));
            return false;
        }
        if (to < System.currentTimeMillis()) {
            showToDateError(getString(R.string.suggest_merchant_to_date_past_error));
            return false;
        }
        return true;
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
        // TODO : error text is not shown now due to date&time views styling. Must be re-done later
        toDate.setError(message);
    }

    @Override
    public void showProgress() {
        progressDialog.show(getFragmentManager());
    }

    @Override
    public void hideProgress() {
        progressDialog.dismiss();
    }

    protected String formatDate(int year, int month, int day) {
        return DateTimeUtils.convertDateToString(year, month, day);
    }

    protected String formatTime(int hours, int minutes) {
        return DateTimeUtils.convertTimeToString(hours, minutes);
    }

    @Override
    public void openPdf(String url) {
        Intent intent = IntentUtils.browserIntent(url);
        startActivity(intent);
    }

    @Override
    public void openPresentation(String url) {
        Intent intent = IntentUtils.browserIntent(url);
        startActivity(intent);
    }
}
