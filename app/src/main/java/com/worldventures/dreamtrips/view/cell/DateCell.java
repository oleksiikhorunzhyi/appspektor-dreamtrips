package com.worldventures.dreamtrips.view.cell;

import android.view.View;
import android.widget.TextView;

import com.fourmob.datetimepicker.date.DatePickerDialog;
import com.techery.spares.annotations.Layout;
import com.techery.spares.ui.view.cell.AbstractCell;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.model.DateFilterItem;
import com.worldventures.dreamtrips.core.navigation.FragmentCompass;
import com.worldventures.dreamtrips.utils.DateUtils;

import java.util.Calendar;

import javax.inject.Inject;

import butterknife.InjectView;
import butterknife.OnClick;

/**
 * Created by Edward on 08.02.15.
 */
@Layout(R.layout.adapter_item_dates)
public class DateCell extends AbstractCell<DateFilterItem> implements DatePickerDialog.OnDateSetListener {

    @InjectView(R.id.textViewStartDate)
    TextView textViewStart;

    @InjectView(R.id.textViewEndDate)
    TextView textViewEnd;

    @Inject
    FragmentCompass fragmentCompass;


    public DateCell(View view) {
        super(view);
    }

    @Override
    protected void syncUIStateWithModel() {
        textViewStart.setText(DateUtils.convertDateForFilters(getModelObject().getStartDate()));
        textViewEnd.setText(DateUtils.convertDateForFilters(getModelObject().getEndDate()));
    }

    @OnClick(R.id.textViewStartDate)
    void onStartClick() {
        showDatePickerDialog("start");
    }

    @OnClick(R.id.textViewEndDate)
    void onEndClick() {
        showDatePickerDialog("end");
    }

    private void showDatePickerDialog(String tag) {
        Calendar calendar = Calendar.getInstance();
        DatePickerDialog datePickerDialog = DatePickerDialog.newInstance
                (this, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), false);
        datePickerDialog.setYearRange(2015, 2020);
        fragmentCompass.show(datePickerDialog, tag);
    }

    @Override
    public void onDateSet(DatePickerDialog datePickerDialog, int year, int month, int day) {
        String tag = datePickerDialog.getTag();
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month);
        calendar.set(Calendar.DAY_OF_MONTH, day);
        if (tag.equals("end")) {
            textViewEnd.setText(DateUtils.convertDateForFilters(calendar.getTime()));
            getModelObject().setEndDate(calendar.getTime());
        } else {
            textViewStart.setText(DateUtils.convertDateForFilters(calendar.getTime()));
            getModelObject().setStartDate(calendar.getTime());
        }
    }

    @Override
    public void prepareForReuse() {

    }
}
