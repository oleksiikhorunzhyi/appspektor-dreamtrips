package com.worldventures.dreamtrips.modules.dtl.view.fragment;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.StringRes;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.SwitchCompat;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.fourmob.datetimepicker.date.DatePickerDialog;
import com.sleepbot.datetimepicker.time.RadialPickerLayout;
import com.sleepbot.datetimepicker.time.TimePickerDialog;
import com.techery.spares.annotations.Layout;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.api.error.ErrorResponse;
import com.worldventures.dreamtrips.core.utils.DateTimeUtils;
import com.worldventures.dreamtrips.core.utils.IntentUtils;
import com.worldventures.dreamtrips.modules.common.view.activity.PlayerActivity;
import com.worldventures.dreamtrips.modules.common.view.custom.DTEditText;
import com.worldventures.dreamtrips.modules.common.view.dialog.ProgressDialogFragment;
import com.worldventures.dreamtrips.modules.common.view.fragment.BaseFragmentWithArgs;
import com.worldventures.dreamtrips.modules.dtl.bundle.SuggestPlaceBundle;
import com.worldventures.dreamtrips.modules.dtl.model.DtlLead;
import com.worldventures.dreamtrips.modules.dtl.presenter.SuggestPlaceBasePresenter;
import com.worldventures.dreamtrips.modules.dtl.validator.DigitsValidator;
import com.worldventures.dreamtrips.modules.dtl.validator.EmptyValidator;
import com.worldventures.dreamtrips.modules.dtl.validator.InputLengthValidator;

import java.util.Calendar;

import butterknife.InjectView;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;
import cn.pedant.SweetAlert.SweetAlertDialog;
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
    @InjectView(R.id.betweenSwitcher)
    protected SwitchCompat betweenSwitcher;
    @InjectView(R.id.fromDateLabel)
    protected TextView fromDateLabel;
    @InjectView(R.id.toDateContainer)
    protected ViewGroup toDateContainer;

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
        phoneNumber.addValidator(new DigitsValidator(getString(R.string.dtl_invalid)));
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
        fromTime.setText(formatTime(hour, minute + 5));
        toDate.setText(formatDate(year, month + 1, day));
        toTime.setText(formatTime(hour, minute));
    }

    @OnCheckedChanged(R.id.betweenSwitcher)
    void betweenSwitcherToggled(CompoundButton buttonView, boolean isChecked){
        if (isChecked){
            toDateContainer.setVisibility(View.VISIBLE);
            fromDateLabel.setVisibility(View.VISIBLE);
        } else {
            toDateContainer.setVisibility(View.GONE);
            fromDateLabel.setVisibility(View.GONE);
        }
    }

    @OnClick(R.id.fromDate)
    void fromDateClicked() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(DateTimeUtils.mergeDateTime(fromDate.getText().toString(), fromTime.getText().toString()));
        DatePickerDialog datePickerDialog = DatePickerDialog.newInstance(this,
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH), false);
        datePickerDialog.setYearRange(calendar.get(Calendar.YEAR), calendar.get(Calendar.YEAR) + 1);
        datePickerDialog.show(getChildFragmentManager(), PICKER_FROM_TAG);
    }

    @OnClick(R.id.fromTime)
    void fromTimeClicked() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(DateTimeUtils.mergeDateTime(fromDate.getText().toString(), fromTime.getText().toString()));
        TimePickerDialog timePickerDialog = TimePickerDialog.newInstance(this,
                calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), false, false);
        lastTimePickerTag = PICKER_FROM_TAG;
        timePickerDialog.show(getChildFragmentManager(), PICKER_FROM_TAG);
    }

    @OnClick(R.id.toDate)
    void toDateClicked() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(DateTimeUtils.mergeDateTime(toDate.getText().toString(), toTime.getText().toString()));
        DatePickerDialog datePickerDialog = DatePickerDialog.newInstance(this,
                calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), false);
        datePickerDialog.setYearRange(calendar.get(Calendar.YEAR), calendar.get(Calendar.YEAR) + 1);
        datePickerDialog.show(getChildFragmentManager(), PICKER_TO_TAG);
    }

    @OnClick(R.id.toTime)
    void toTimeClicked() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(DateTimeUtils.mergeDateTime(toDate.getText().toString(), toTime.getText().toString()));
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
        if (validateInput()) {
            getPresenter().submitClicked();
        }
    }

    protected boolean validateInput() {
        return contactName.validate() && phoneNumber.validate() && phoneNumber.validateCharactersCount()
                && validateDateTime() && validateRating() && additionalInfo.validate();
    }

    protected boolean validateDateTime() {
        long from = getFromTimestamp(), to = getToTimestamp();
        if (from < System.currentTimeMillis()) {
            String errorMessage;
            if (betweenSwitcher.isChecked()) {
                errorMessage = getString(R.string.suggest_merchant_to_date_past_error);
            } else {
                errorMessage = String.format(getString(R.string.suggest_merchant_contact_time_past_error_format),
                        getString(R.string.suggest_merchant_best_time_contact_caption));
            }
            showContactTimeFieldError(errorMessage);
            return false;
        }
        if (betweenSwitcher.isChecked() && from > to) {
            showContactTimeFieldError(getString(R.string.suggest_merchant_to_date_overlap_error));
            return false;
        }
        return true;
    }

    protected boolean validateRating() {
        if (foodRatingBar.getRating() == 0) {
            showRatingBarValidationError(R.string.suggest_merchant_food_caption);
            return false;
        }
        if (serviceRatingBar.getRating() == 0) {
            showRatingBarValidationError(R.string.suggest_merchant_service_caption);
            return false;
        }
        if (cleanlinessRatingBar.getRating() == 0) {
            showRatingBarValidationError(R.string.suggest_merchant_cleanliness_caption);
            return false;
        }
        if (uniquenessRatingBar.getRating() == 0) {
            showRatingBarValidationError(R.string.suggest_merchant_uniqueness_caption);
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
    public boolean intervalDate() {
        return betweenSwitcher.isChecked();
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
    public void showContactTimeFieldError(String message) {
        if (getView() != null) {
            Snackbar.make(getView(), message, Snackbar.LENGTH_LONG).show();
        }
    }

    private void showRatingBarValidationError(@StringRes int ratingBarNameRes) {
        if (getView() != null) {
            Snackbar.make(getView(),
                    String.format(getString(R.string.suggest_merchant_rating_error_format),
                            getString(ratingBarNameRes)),
                    Snackbar.LENGTH_LONG).show();
        }
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
        Intent intent = new Intent(getActivity(), PlayerActivity.class).setData(Uri.parse(url));
        startActivity(intent);
    }

    @Override
    public void merchantSubmitted() {
        Dialog sweetAlertDialog = new SweetAlertDialog(getActivity(), SweetAlertDialog.SUCCESS_TYPE)
                .setTitleText(getString(R.string.dtl_merchant_success))
                .setContentText(getString(R.string.dtl_merchant_submitted));
        //
        sweetAlertDialog.setOnDismissListener(this::dialogCanceled);
        sweetAlertDialog.setCanceledOnTouchOutside(true);
        sweetAlertDialog.show();
    }

    @Override
    public boolean onApiError(ErrorResponse errorResponse) {
        if (errorResponse.containsField(DtlLead.NAME)) {
            restaurantName.setError(errorResponse.getMessageForField(DtlLead.NAME));
        }
        if (errorResponse.containsField(DtlLead.CONTACT)) {
            contactName.setError(errorResponse.getMessageForField(DtlLead.CONTACT));
        }
        if (errorResponse.containsField(DtlLead.CITY)) {
            city.setError(errorResponse.getMessageForField(DtlLead.CITY));
        }
        if (errorResponse.containsField(DtlLead.PHONE)) {
            phoneNumber.setError(errorResponse.getMessageForField(DtlLead.PHONE));
        }
        if (errorResponse.containsField(DtlLead.DESCRIPTION)) {
            additionalInfo.setError(errorResponse.getMessageForField(DtlLead.DESCRIPTION));
        }
        return false;
    }

    @Override
    public void onApiCallFailed() {
        hideProgress();
    }

    /**
     * Override this to react when dialog cancelled
     */
    protected void dialogCanceled(DialogInterface dialog) {
        //
    }
}
