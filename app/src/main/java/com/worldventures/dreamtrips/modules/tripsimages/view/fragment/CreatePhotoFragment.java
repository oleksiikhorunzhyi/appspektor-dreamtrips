package com.worldventures.dreamtrips.modules.tripsimages.view.fragment;

import android.net.Uri;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.facebook.drawee.drawable.ScalingUtils;
import com.facebook.drawee.view.SimpleDraweeView;
import com.fourmob.datetimepicker.date.DatePickerDialog;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.sleepbot.datetimepicker.time.RadialPickerLayout;
import com.sleepbot.datetimepicker.time.TimePickerDialog;
import com.techery.spares.annotations.Layout;
import com.techery.spares.utils.ui.OrientationUtil;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.utils.GraphicUtils;
import com.worldventures.dreamtrips.core.utils.ViewUtils;
import com.worldventures.dreamtrips.modules.common.presenter.ComponentPresenter;
import com.worldventures.dreamtrips.modules.common.view.custom.DTEditText;
import com.worldventures.dreamtrips.modules.common.view.custom.tagview.viewgroup.CreationPhotoTaggableHolderViewGroup;
import com.worldventures.dreamtrips.modules.common.view.fragment.BaseFragment;
import com.worldventures.dreamtrips.modules.tripsimages.model.Image;
import com.worldventures.dreamtrips.modules.tripsimages.model.Photo;
import com.worldventures.dreamtrips.modules.tripsimages.model.PhotoTag;
import com.worldventures.dreamtrips.modules.tripsimages.presenter.CreatePhotoPresenter;
import com.worldventures.dreamtrips.modules.tripsimages.view.activity.CreatePhotoActivity;

import java.util.Calendar;
import java.util.List;

import butterknife.InjectView;
import butterknife.OnClick;


@Layout(R.layout.fragment_create_photo)
public class CreatePhotoFragment extends BaseFragment<CreatePhotoPresenter> implements DatePickerDialog.OnDateSetListener, View.OnTouchListener, TimePickerDialog.OnTimeSetListener, CreatePhotoPresenter.View {

    public static final String BUNDLE_IMAGE_URI = "BUNDLE_IMAGE_URI";
    public static final String BUNDLE_TYPE = "type_bundle";

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
    @InjectView(R.id.imageView)
    ImageView imageView;
    private Uri uri;

    @Override
    public void afterCreateView(View rootView) {
        super.afterCreateView(rootView);

        etDate.setOnTouchListener(this);
        etTime.setOnTouchListener(this);

        ViewGroup.LayoutParams lp = ivImage.getLayoutParams();
        lp.height = ViewUtils.getMinSideSize(getActivity());//but by material style guide 3:2

        ivImage.setController(GraphicUtils.provideFrescoResizingController(uri, ivImage.getController()));
        Photo photo = new Photo();
        Image images = new Image();
        images.setUrl(uri.toString());
        photo.setImages(images);
        taggableImageHolder.setup(this, photo);
        showTagViewGroup();
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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

    @Override
    protected CreatePhotoPresenter createPresenter(Bundle savedInstanceState) {
        uri = getArguments().getBundle(ComponentPresenter.EXTRA_DATA).getParcelable(BUNDLE_IMAGE_URI);
        String type = getArguments().getBundle(ComponentPresenter.EXTRA_DATA).getString(BUNDLE_TYPE);
        return new CreatePhotoPresenter(type);
    }

    @Override
    public void onViewStateRestored(Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
    }

    @OnClick(R.id.btn_save)
    public void onActionSave() {
        getPresenter().saveAction();
    }

    @OnClick(R.id.tag)
    public void onTag() {
        if (!taggableImageHolder.isSetuped()) return;
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
    public List<PhotoTag> getTagsToUpload() {
        return taggableImageHolder.getLocallyAddedTags();
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
