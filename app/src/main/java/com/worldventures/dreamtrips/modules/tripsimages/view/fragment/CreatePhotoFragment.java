package com.worldventures.dreamtrips.modules.tripsimages.view.fragment;

import android.net.Uri;
import android.os.Bundle;
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
import com.worldventures.dreamtrips.modules.tripsimages.presenter.CreatePhotoFragmentPM;
import com.worldventures.dreamtrips.core.utils.UniversalImageLoader;
import com.worldventures.dreamtrips.core.utils.ViewUtils;
import com.worldventures.dreamtrips.modules.tripsimages.view.activity.CreatePhotoActivity;
import com.worldventures.dreamtrips.modules.common.view.custom.DTEditText;
import com.worldventures.dreamtrips.modules.common.view.fragment.BaseFragment;

import java.util.Calendar;
import java.util.Date;

import javax.inject.Inject;

import butterknife.InjectView;
import butterknife.OnClick;

@Layout(R.layout.fragment_create_photo)
public class CreatePhotoFragment extends BaseFragment<CreatePhotoFragmentPM> implements DatePickerDialog.OnDateSetListener, View.OnTouchListener, TimePickerDialog.OnTimeSetListener, CreatePhotoFragmentPM.View {

    public static final String BUNDLE_IMAGE_URI = "BUNDLE_IMAGE_URI";
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
    private Uri uri;

    @Override
    public void afterCreateView(View rootView) {
        super.afterCreateView(rootView);

        etDate.setOnTouchListener(this);
        etTime.setOnTouchListener(this);

        ViewGroup.LayoutParams lp = ivImage.getLayoutParams();
        lp.height = ViewUtils.getMinSideSize(getActivity());//but by material style guide 3:2

        uri = getArguments().getParcelable(BUNDLE_IMAGE_URI);
        imageLoader.loadImage(uri, ivImage, null);

        Date date = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);

        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);
        int hour = cal.get(Calendar.HOUR);
        int minute = cal.get(Calendar.MINUTE);

        getPresenter().onDataSet(year, month, day);
        getPresenter().onTimeSet(hour, minute);
    }

    @Override
    protected CreatePhotoFragmentPM createPresenter(Bundle savedInstanceState) {
        return new CreatePhotoFragmentPM(this);
    }


    @OnClick(R.id.btn_save)
    public void onActionSave(View v) {
        getPresenter().saveAction();
    }

    @Override
    public void onTimeSet(RadialPickerLayout radialPickerLayout, int hour, int min) {
        getPresenter().onTimeSet(hour, min);
    }

    @Override
    public void onDateSet(DatePickerDialog datePickerDialog, int year, int month, int day) {
        getPresenter().onDataSet(year, month, day);
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

    @Override
    public void end() {
        ((CreatePhotoActivity) getActivity()).preFinishProcess();
    }

    @Override
    public Uri getImageUri() {
        return uri;
    }

    @Override
    public String getLocation() {
        return etLocation.getText().toString();
    }

    @Override
    public String getTags() {
        return etTags.getText().toString();
    }

    @Override
    public String getTitle() {
        return etTitle.getText().toString();
    }

    @Override
    public String getDate() {
        return etDate.getText().toString();
    }

    @Override
    public void setDate(String format) {
        etDate.setText(format);
    }

    @Override
    public String getTime() {
        return etTime.getText().toString();
    }

    @Override
    public void setTime(String format) {
        etTime.setText(format);
    }

}