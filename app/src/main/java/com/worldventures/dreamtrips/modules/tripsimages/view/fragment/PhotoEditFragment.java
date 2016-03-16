package com.worldventures.dreamtrips.modules.tripsimages.view.fragment;

import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;

import com.facebook.drawee.drawable.ScalingUtils;
import com.facebook.drawee.view.SimpleDraweeView;
import com.fourmob.datetimepicker.date.DatePickerDialog;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.sleepbot.datetimepicker.time.RadialPickerLayout;
import com.sleepbot.datetimepicker.time.TimePickerDialog;
import com.techery.spares.annotations.Layout;
import com.techery.spares.utils.ui.OrientationUtil;
import com.techery.spares.utils.ui.SoftInputUtil;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.utils.ViewUtils;
import com.worldventures.dreamtrips.modules.common.view.custom.DTEditText;
import com.worldventures.dreamtrips.modules.common.view.custom.tagview.viewgroup.CreationPhotoTaggableHolderViewGroup;
import com.worldventures.dreamtrips.modules.common.view.fragment.BaseFragmentWithArgs;
import com.worldventures.dreamtrips.modules.tripsimages.bundle.EditPhotoBundle;
import com.worldventures.dreamtrips.modules.tripsimages.model.Photo;
import com.worldventures.dreamtrips.modules.tripsimages.presenter.PhotoEditPresenter;

import java.util.Calendar;

import butterknife.InjectView;
import butterknife.OnClick;

@Layout(R.layout.fragment_create_photo)
public class PhotoEditFragment extends BaseFragmentWithArgs<PhotoEditPresenter, EditPhotoBundle>
        implements PhotoEditPresenter.View, DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {

    @InjectView(R.id.iv_image)
    protected SimpleDraweeView ivImage;
    @InjectView(R.id.btn_save)
    protected FloatingActionButton btnSave;
    @InjectView(R.id.et_title)
    protected DTEditText etTitle;
    @InjectView(R.id.et_location)
    protected DTEditText etLocation;
    @InjectView(R.id.et_date)
    protected DTEditText etDate;
    @InjectView(R.id.et_time)
    protected DTEditText etTime;
    @InjectView(R.id.et_tags)
    protected DTEditText etTags;
    @InjectView(R.id.taggable_holder)
    protected CreationPhotoTaggableHolderViewGroup taggableImageHolder;
    @InjectView(R.id.tag)
    protected ImageView tag;

    @InjectView(R.id.toolbar)
    protected Toolbar toolbar;

    @Override
    public void afterCreateView(View rootView) {
        super.afterCreateView(rootView);
        ViewGroup.LayoutParams lp = ivImage.getLayoutParams();
        lp.height = ViewUtils.getMinSideSize(getActivity());

        toolbar.setVisibility(View.VISIBLE);
        toolbar.setNavigationIcon(R.drawable.abc_ic_ab_back_mtrl_am_alpha);
        toolbar.setNavigationOnClickListener(view -> getActivity().onBackPressed());
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
    }

    private void showTimePickerDialog() {
        Calendar calendar = Calendar.getInstance();
        TimePickerDialog timePickerDialog = TimePickerDialog.newInstance(this,
                calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), false, false);
        timePickerDialog.show(getChildFragmentManager(), null);
    }

    private void showDatePickerDialog() {
        Calendar calendar = Calendar.getInstance();
        DatePickerDialog datePickerDialog = DatePickerDialog.newInstance(this,
                calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), false);
        datePickerDialog.setYearRange(1915, 2015);
        datePickerDialog.show(getChildFragmentManager(), null);
    }

    public void onTimeSet(RadialPickerLayout radialPickerLayout, int hour, int min) {
        getPresenter().onTimeSet(hour, min);
    }

    @Override
    public void onDateSet(DatePickerDialog datePickerDialog, int year, int month, int day) {
        getPresenter().onDataSet(year, month, day);
    }

    @OnClick(R.id.btn_save)
    public void onActionSave() {
        getPresenter().saveAction();
    }

    @OnClick(R.id.et_date)
    public void onDate() {
        showDatePickerDialog();
    }

    @OnClick(R.id.et_time)
    public void onTime() {
        showTimePickerDialog();
    }

    @OnClick(R.id.tag)
    public void onTag() {
        if (!taggableImageHolder.isSetuped()) return;
        //
        if (taggableImageHolder.isShown()) {
            hideTagViewGroup();
        } else {
            showTagViewGroup();
        }
    }

    protected void showTagViewGroup() {
        tag.setSelected(true);
        taggableImageHolder.show(ivImage);
    }

    protected void hideTagViewGroup() {
        tag.setSelected(false);
        taggableImageHolder.hide();
        ivImage.getHierarchy().setActualImageScaleType(ScalingUtils.ScaleType.CENTER_CROP);
    }

    @Override
    public void onStart() {
        super.onStart();
        taggableImageHolder.post(() -> {
            if (taggableImageHolder == null) return;
            //
            if (taggableImageHolder.isShown()) {
                showTagViewGroup();
            } else {
                hideTagViewGroup();
            }
        });
    }

    @Override
    public void setImage(Uri uri) {
        ivImage.setImageURI(uri);
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

    @Override
    public void setTitle(String title) {
        etTitle.setText(title);
    }

    @Override
    public void setLocation(String location) {
        etLocation.setText(location);
    }

    @Override
    public void setTags(String tags) {
        etTags.setText(tags);
    }

    @Override
    public void finish() {
        SoftInputUtil.hideSoftInputMethod(etTitle);
        getActivity().onBackPressed();
    }

    public void setEnabledSaveButton(boolean enabled) {
        btnSave.setEnabled(enabled);
    }

    @Override
    public void setupTaggingHolder(Photo photo) {
        taggableImageHolder.setup(this, photo);
        taggableImageHolder.setCompleteListener(this::finish);
        showTagViewGroup();
    }

    @Override
    public void pushTags() {
        taggableImageHolder.pushRequests();
    }

    @Override
    protected PhotoEditPresenter createPresenter(Bundle savedInstanceState) {
        return new PhotoEditPresenter(getArgs());
    }

    @Override
    public void onResume() {
        super.onResume();
        OrientationUtil.lockOrientation(getActivity());
    }

    @Override
    public void onPause() {
        super.onPause();
        OrientationUtil.unlockOrientation(getActivity());
    }
}

