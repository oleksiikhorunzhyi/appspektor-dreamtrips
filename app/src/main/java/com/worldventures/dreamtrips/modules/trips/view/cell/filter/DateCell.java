package com.worldventures.dreamtrips.modules.trips.view.cell.filter;

import android.view.View;
import android.widget.TextView;

import com.fourmob.datetimepicker.date.DatePickerDialog;
import com.techery.spares.annotations.Layout;
import com.techery.spares.ui.view.cell.AbstractCell;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.navigation.FragmentCompass;
import com.worldventures.dreamtrips.core.utils.DateTimeUtils;
import com.worldventures.dreamtrips.modules.trips.model.DateFilterItem;

import java.util.Calendar;

import javax.inject.Inject;

import butterknife.InjectView;
import butterknife.OnClick;

@Layout(R.layout.adapter_item_dates)
public class DateCell extends AbstractCell<DateFilterItem> implements DatePickerDialog.OnDateSetListener {
    public static final String START = "start";
    public static final String END = "end";

    @InjectView(R.id.textViewStartDate)
    protected TextView textViewStart;

    @InjectView(R.id.textViewEndDate)
    protected TextView textViewEnd;

    @Inject
    protected FragmentCompass fragmentCompass;

    public DateCell(View view) {
        super(view);
    }

    @Override
    protected void syncUIStateWithModel() {
        textViewStart.setText(DateTimeUtils.convertDateForFilters(getModelObject().getStartDate()));
        textViewEnd.setText(DateTimeUtils.convertDateForFilters(getModelObject().getEndDate()));
    }

    @OnClick(R.id.textViewStartDate)
    void onStartClick() {
        showDatePickerDialog(START);
    }

    @OnClick(R.id.textViewEndDate)
    void onEndClick() {
        showDatePickerDialog(END);
    }

    private void showDatePickerDialog(String tag) {
        Calendar calendar = Calendar.getInstance();

        if (tag.equals(END)) {
            calendar.setTime(getModelObject().getEndDate());
        } else {
            calendar.setTime(getModelObject().getStartDate());
        }

        fragmentCompass.showDatePickerDialog(this, calendar, 2015, 2020, tag);
    }

    @Override
    public void onDateSet(DatePickerDialog datePickerDialog, int year, int month, int day) {
        String tag = datePickerDialog.getTag();
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month);
        calendar.set(Calendar.DAY_OF_MONTH, day);

        if (tag.equals(END)) {
            processEndDate(calendar);
        } else {
            processStartDate(calendar);
        }
    }

    private void processEndDate(Calendar calendar) {
        if (validateEndDate(calendar)) {
            textViewEnd.setText(DateTimeUtils.convertDateForFilters(calendar.getTime()));
            getModelObject().setEndDate(calendar.getTime());
        } else {
            textViewEnd.setText(DateTimeUtils.convertDateForFilters(getModelObject().getStartDate()));
            getModelObject().setEndDate(getModelObject().getStartDate());
        }
    }

    private void processStartDate(Calendar calendar) {
        if (validateStartDate(calendar)) {
            textViewStart.setText(DateTimeUtils.convertDateForFilters(calendar.getTime()));
            getModelObject().setStartDate(calendar.getTime());
        } else {
            textViewStart.setText(DateTimeUtils.convertDateForFilters(getModelObject().getEndDate()));
            getModelObject().setStartDate(getModelObject().getEndDate());
        }
    }

    private boolean validateEndDate(Calendar selectedEndDate) {
        return selectedEndDate.getTimeInMillis() > getModelObject().getStartDate().getTime();
    }

    private boolean validateStartDate(Calendar selectedStartDate) {
        return selectedStartDate.getTimeInMillis() < getModelObject().getEndDate().getTime();
    }
    @Override
    public void prepareForReuse() {
        //nothing to do here
    }
}