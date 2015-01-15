package com.worldventures.dreamtrips.view.fragment;

import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.fourmob.datetimepicker.date.DatePickerDialog;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.sleepbot.datetimepicker.time.RadialPickerLayout;
import com.sleepbot.datetimepicker.time.TimePickerDialog;
import com.techery.spares.annotations.Layout;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.utils.UniversalImageLoader;
import com.worldventures.dreamtrips.utils.ViewIUtils;
import com.worldventures.dreamtrips.view.activity.CreatePhotoActivity;
import com.worldventures.dreamtrips.view.custom.DTEditText;
import com.worldventures.dreamtrips.presentation.CreatePhotoFragmentPM;

import org.robobinding.ViewBinder;

import java.util.Calendar;
import java.util.Date;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

@Layout(R.layout.fragment_create_photo)
public class CreatePhotoFragment extends BaseFragment<CreatePhotoFragmentPM> implements DatePickerDialog.OnDateSetListener, View.OnTouchListener, TimePickerDialog.OnTimeSetListener {

    @InjectView(R.id.iv_image)
    ImageView ivImage;
    @InjectView(R.id.btn_save)
    FloatingActionButton btnSave;
    @InjectView(R.id.et_title)
    DTEditText etTitle;
    @InjectView(R.id.et_location)
    DTEditText etLocation;
    @InjectView(R.id.et_date)
    DTEditText etDate;
    @InjectView(R.id.et_time)
    DTEditText etTime;
    @InjectView(R.id.et_tags)
    DTEditText etTags;
    @Inject
    UniversalImageLoader imageLoader;

    @Override
    public void afterCreateView(View rootView) {
        super.afterCreateView(rootView);

        etDate.setOnTouchListener(this);
        etTime.setOnTouchListener(this);
        ViewGroup.LayoutParams lp = ivImage.getLayoutParams();
        lp.height = ViewIUtils.getMinSideSize(getActivity());//but by material style guide 3:2

        imageLoader.loadImage(Uri.parse(getPresentationModel().getImageUri().toString()), ivImage, null);
        Date date = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);
        int hour = cal.get(Calendar.HOUR);
        int minute = cal.get(Calendar.MINUTE);

        getPresentationModel().onDataSet(year, month, day);
        getPresentationModel().onTimeSet(hour, minute);
    }

    @Override
    protected CreatePhotoFragmentPM createPresentationModel(Bundle savedInstanceState) {
        return new CreatePhotoFragmentPM(this);
    }


    @OnClick(R.id.btn_save)
    public void onActionSave(View v) {
        getPresentationModel().saveAction();
    }

    @Override
    public void onTimeSet(RadialPickerLayout radialPickerLayout, int hour, int min) {
        getPresentationModel().onTimeSet(hour, min);
    }

    @Override
    public void onDateSet(DatePickerDialog datePickerDialog, int year, int month, int day) {
        getPresentationModel().onDataSet(year, month, day);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_UP) {
            switch (v.getId()) {
                case R.id.et_date:
                    showDatePickerDialog();
                    break;
                case R.id.et_time:
                    showTimePickerDialog();
                    break;
            }
        }
        return false;
    }

    private void showTimePickerDialog() {
        Calendar calendar = Calendar.getInstance();
        TimePickerDialog timePickerDialog = TimePickerDialog.newInstance(this, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), false, false);
        timePickerDialog.setCloseOnSingleTapMinute(true);
        timePickerDialog.show(getChildFragmentManager(), null);
    }

    private void showDatePickerDialog() {
        Calendar calendar = Calendar.getInstance();
        DatePickerDialog datePickerDialog = DatePickerDialog.newInstance(this, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), false);
        datePickerDialog.setYearRange(1915, 2015);
        datePickerDialog.show(getChildFragmentManager(), null);
    }
}
