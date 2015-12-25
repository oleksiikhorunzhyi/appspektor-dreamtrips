package com.worldventures.dreamtrips.modules.bucketlist.view.fragment;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;

import com.afollestad.materialdialogs.MaterialDialog;
import com.fourmob.datetimepicker.date.DatePickerDialog;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.techery.spares.annotations.Layout;
import com.techery.spares.annotations.MenuResource;
import com.techery.spares.utils.ui.OrientationUtil;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.navigation.NavigationBuilder;
import com.worldventures.dreamtrips.core.navigation.Route;
import com.worldventures.dreamtrips.core.utils.DateTimeUtils;
import com.worldventures.dreamtrips.modules.bucketlist.model.BucketPhoto;
import com.worldventures.dreamtrips.modules.bucketlist.model.CategoryItem;
import com.worldventures.dreamtrips.modules.bucketlist.presenter.BucketItemEditPresenter;
import com.worldventures.dreamtrips.modules.bucketlist.presenter.BucketItemEditPresenterView;
import com.worldventures.dreamtrips.modules.bucketlist.view.custom.BucketPhotosView;
import com.worldventures.dreamtrips.modules.bucketlist.view.custom.IBucketPhotoView;
import com.worldventures.dreamtrips.modules.common.model.UploadTask;
import com.worldventures.dreamtrips.modules.common.view.bundle.BucketBundle;
import com.worldventures.dreamtrips.modules.common.view.custom.PhotoPickerLayout;
import com.worldventures.dreamtrips.modules.common.view.fragment.BaseFragmentWithArgs;
import com.worldventures.dreamtrips.modules.tripsimages.bundle.FullScreenImagesBundle;
import com.worldventures.dreamtrips.modules.tripsimages.view.custom.PickImageDelegate;

import java.util.Calendar;
import java.util.List;

import butterknife.InjectView;
import butterknife.OnClick;
import butterknife.Optional;

@Layout(R.layout.fragment_bucket_item_edit)
@MenuResource(R.menu.menu_bucket_quick)
public class BucketItemEditFragment extends BaseFragmentWithArgs<BucketItemEditPresenter, BucketBundle>
        implements BucketItemEditPresenterView, DatePickerDialog.OnDateSetListener {

    @Optional
    @InjectView(R.id.done)
    protected ImageView imageViewDone;
    @InjectView(R.id.editTextTitle)
    protected EditText editTextTitle;
    @InjectView(R.id.editTextDescription)
    protected MaterialEditText editTextDescription;
    @InjectView(R.id.editTextPeople)
    protected EditText editTextPeople;
    @InjectView(R.id.editTextTags)
    protected EditText editTextTags;
    @InjectView(R.id.editTextTime)
    protected AutoCompleteTextView autoCompleteTextViwDate;
    @InjectView(R.id.checkBoxDone)
    protected CheckBox checkBox;
    @InjectView(R.id.spinnerCategory)
    protected Spinner spinnerCategory;
    @InjectView(R.id.lv_items)
    protected BucketPhotosView bucketPhotosView;
    @InjectView(R.id.photo_picker)
    protected PhotoPickerLayout photoPickerLayout;
    @InjectView(R.id.loading_view)
    protected ViewGroup loadingView;

    private boolean categorySelected = false;

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        if (isTabletLandscape()) return;
        //
        if (menu != null) {
            menu.clear();
        }
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onResume() {
        super.onResume();
        initAutoCompleteDate();

        if (getArgs().isLock()) OrientationUtil.lockOrientation(getActivity());
    }

    @Override
    public void onPause() {
        super.onPause();
        OrientationUtil.unlockOrientation(getActivity());
    }

    @Optional
    @OnClick(R.id.mainFrame)
    void onClick() {
        done();
    }

    @OnClick(R.id.editTextTime)
    void onTimeClicked() {
        autoCompleteTextViwDate.showDropDown();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_done) {
            getPresenter().saveItem(true);
        }
        return super.onOptionsItemSelected(item);
    }

    @Optional
    @OnClick(R.id.done)
    void onDone() {
        getPresenter().saveItem(true);
    }

    private void openDatePicker() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(getPresenter().getDate());
        DatePickerDialog datePickerDialog =
                DatePickerDialog.newInstance(this, calendar.get(Calendar.YEAR),
                        calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH), false);
        if (getActivity() != null && !getActivity().isFinishing()) {
            datePickerDialog.show(getChildFragmentManager(), "default");
        }
    }

    @Override
    public void onDateSet(DatePickerDialog datePickerDialog, int year, int month, int day) {
        getPresenter().onDateSet(year, month, day);
        initAutoCompleteDate();
    }

    @Override
    protected BucketItemEditPresenter createPresenter(Bundle savedInstanceState) {
        return new BucketItemEditPresenter(getArgs());
    }

    @Override
    public void afterCreateView(View rootView) {
        super.afterCreateView(rootView);
        bucketPhotosView.init(this, BucketPhotosView.Type.EDIT);

        if (imageViewDone != null) {
            setHasOptionsMenu(false);
        }

        inject(photoPickerLayout);
        photoPickerLayout.setup(getChildFragmentManager(), true, 5);
        photoPickerLayout.hidePanel();
        photoPickerLayout.setOnDoneClickListener(chosenImages -> getPresenter().attachImages(chosenImages, PickImageDelegate.REQUEST_MULTI_SELECT));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        OrientationUtil.unlockOrientation(getActivity());
    }

    @Override
    public void setCategoryItems(List<CategoryItem> items) {
        ArrayAdapter<CategoryItem> adapter = new ArrayAdapter<>(getActivity(),
                R.layout.spinner_dropdown_item, items);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategory.setVisibility(View.VISIBLE);
        spinnerCategory.setAdapter(adapter);
        AdapterView.OnItemSelectedListener onItemSelectedListenerCategory = new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                categorySelected = true;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                //nothin to do here
            }
        };
        spinnerCategory.setOnItemSelectedListener(onItemSelectedListenerCategory);
    }

    private void initAutoCompleteDate() {
        String[] items = getResources().getStringArray(R.array.bucket_date_items);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(),
                R.layout.item_dropdown, items);
        autoCompleteTextViwDate.setAdapter(adapter);
        autoCompleteTextViwDate.setOnItemClickListener((parent, view, position, id) -> {
            if (position == 0) {
                autoCompleteTextViwDate.setText("");
                openDatePicker();
            } else if (position == parent.getCount() - 1) {
                getPresenter().onDateClear();
                initAutoCompleteDate();
            } else {
                getPresenter().setDate(DateTimeUtils.convertReferenceToDate(position));
            }
        });
    }

    @Override
    public void done() {
        loadingView.setVisibility(View.GONE);
        getActivity().onBackPressed();
        hideSoftInput(editTextDescription);
    }

    @Override
    public CategoryItem getSelectedItem() {
        if (categorySelected) {
            return (CategoryItem) spinnerCategory.getSelectedItem();
        } else {
            return null;
        }
    }

    @Override
    public void setCategory(int selection) {
        spinnerCategory.setSelection(selection);
    }

    @Override
    public void setTime(String time) {
        autoCompleteTextViwDate.setText(time);
    }

    @Override
    public String getTags() {
        return editTextTags.getText().toString();
    }

    @Override
    public void setTags(String tags) {
        editTextTags.setText(tags);
    }

    @Override
    public String getPeople() {
        return editTextPeople.getText().toString();
    }

    @Override
    public void setPeople(String people) {
        editTextPeople.setText(people);
    }

    @Override
    public String getTitle() {
        return editTextTitle.getText().toString();
    }

    @Override
    public void setTitle(String title) {
        editTextTitle.setText(title);
    }

    @Override
    public String getDescription() {
        return editTextDescription.getText().toString();
    }

    @Override
    public void setDescription(String description) {
        editTextDescription.setText(description);
    }

    @Override
    public IBucketPhotoView getBucketPhotosView() {
        return bucketPhotosView;
    }

    @Override
    public void hidePhotoPicker() {
        photoPickerLayout.hidePanel();
    }

    @Override
    public void showPhotoPicker() {
        photoPickerLayout.showPanel();
    }

    @Override
    public void showLoading() {
        loadingView.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideLoading() {
        loadingView.setVisibility(View.GONE);
    }

    @Override
    public void openFullscreen(FullScreenImagesBundle data) {
        NavigationBuilder.create().with(activityRouter).data(data).move(Route.FULLSCREEN_PHOTO_LIST);
    }

    @Override
    public void setImages(List<BucketPhoto> photos) {
        bucketPhotosView.setImages(photos);
    }

    @Override
    public void addImages(List<UploadTask> tasks) {
        bucketPhotosView.addImages(tasks);
    }

    @Override
    public void addImage(UploadTask uploadTask) {
        bucketPhotosView.addImage(uploadTask);
    }

    @Override
    public void deleteImage(UploadTask task) {
        bucketPhotosView.deleteImage(task);
    }

    @Override
    public void deleteImage(BucketPhoto bucketPhoto) {
        bucketPhotosView.deleteImage(bucketPhoto);
    }

    @Override
    public void itemChanged(UploadTask uploadTask) {
        bucketPhotosView.itemChanged(uploadTask);
    }

    @Override
    public void replace(UploadTask bucketPhotoUploadTask, BucketPhoto bucketPhoto) {
        bucketPhotosView.replace(bucketPhotoUploadTask, bucketPhoto);
    }

    @Override
    public UploadTask getBucketPhotoUploadTask(String taskId) {
        return bucketPhotosView.getBucketPhotoUploadTask(taskId);
    }

    @Override
    public boolean getStatus() {
        return checkBox.isChecked();
    }

    @Override
    public void setStatus(boolean isCompleted) {
        checkBox.setChecked(isCompleted);
    }

    @Override
    public void showError() {
        editTextDescription.validate();
    }


}




