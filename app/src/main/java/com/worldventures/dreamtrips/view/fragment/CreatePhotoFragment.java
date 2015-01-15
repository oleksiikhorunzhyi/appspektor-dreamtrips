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
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.utils.UniversalImageLoader;
import com.worldventures.dreamtrips.utils.ViewIUtils;
import com.worldventures.dreamtrips.view.activity.CreatePhotoActivity;
import com.worldventures.dreamtrips.view.custom.DTEditText;
import com.worldventures.dreamtrips.view.presentation.CreatePhotoFragmentPM;

import org.robobinding.ViewBinder;

import java.util.Calendar;
import java.util.Date;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

public class CreatePhotoFragment extends BaseFragment<CreatePhotoActivity> implements DatePickerDialog.OnDateSetListener, View.OnTouchListener, TimePickerDialog.OnTimeSetListener {

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
    private CreatePhotoFragmentPM pm;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        pm = new CreatePhotoFragmentPM(this, getAbsActivity());
        ViewBinder viewBinder = getAbsActivity().createViewBinder();
        View view = viewBinder.inflateAndBindWithoutAttachingToRoot(R.layout.fragment_create_photo, pm, container);
        ButterKnife.inject(this, view);
        getAbsActivity().inject(this);
        etDate.setOnTouchListener(this);
        etTime.setOnTouchListener(this);
        ViewGroup.LayoutParams lp = ivImage.getLayoutParams();
        lp.height = ViewIUtils.getMinSideSize(getAbsActivity());//but by material style guide 3:2

        imageLoader.loadImage(Uri.parse(getAbsActivity().getImageUri().toString()), ivImage, null);
        Date date = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);
        int hour = cal.get(Calendar.HOUR);
        int minute = cal.get(Calendar.MINUTE);

        pm.onDataSet(year, month, day);
        pm.onTimeSet(hour, minute);
        return view;
    }


    @OnClick(R.id.btn_save)
    public void onActionSave(View v) {
        pm.saveAction();
    }

    @Override
    public void onTimeSet(RadialPickerLayout radialPickerLayout, int hour, int min) {
        pm.onTimeSet(hour, min);
    }

    @Override
    public void onDateSet(DatePickerDialog datePickerDialog, int year, int month, int day) {
        pm.onDataSet(year, month, day);
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
