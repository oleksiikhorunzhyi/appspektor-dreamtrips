package com.worldventures.dreamtrips.modules.bucketlist.view.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;

import com.badoo.mobile.util.WeakHandler;
import com.fourmob.datetimepicker.date.DatePickerDialog;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.techery.spares.annotations.Layout;
import com.techery.spares.annotations.MenuResource;
import com.techery.spares.module.Injector;
import com.techery.spares.module.qualifier.ForActivity;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.navigation.FragmentCompass;
import com.worldventures.dreamtrips.core.utils.DateTimeUtils;
import com.worldventures.dreamtrips.core.utils.events.ActivityResult;
import com.worldventures.dreamtrips.modules.bucketlist.model.CategoryItem;
import com.worldventures.dreamtrips.modules.bucketlist.presenter.BucketItemEditPresenter;
import com.worldventures.dreamtrips.modules.bucketlist.presenter.BucketItemEditPresenterView;
import com.worldventures.dreamtrips.modules.bucketlist.view.custom.BucketPhotosView;
import com.worldventures.dreamtrips.modules.bucketlist.view.custom.IBucketPhotoView;
import com.worldventures.dreamtrips.modules.common.view.fragment.BaseFragment;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Provider;

import butterknife.InjectView;
import butterknife.OnClick;
import butterknife.Optional;

@Layout(R.layout.fragment_bucket_item_edit)
@MenuResource(R.menu.menu_bucket_quick)
public class BucketItemEditFragment extends BaseFragment<BucketItemEditPresenter>
        implements BucketItemEditPresenterView, DatePickerDialog.OnDateSetListener {

    @Inject
    protected FragmentCompass fragmentCompass;
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
    @Inject
    @ForActivity
    Provider<Injector> injector;
    WeakHandler handler = new WeakHandler();
    private boolean categorySelected = false;

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        if (menu != null) {
            menu.clear();
        }
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onResume() {
        super.onResume();
        initAutoCompleteDate();
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
            getPresenter().saveItem();
        }
        return super.onOptionsItemSelected(item);
    }

    @Optional
    @OnClick(R.id.done)
    void onDone() {
        getPresenter().saveItem();
    }

    private void openDatePicker() {
        fragmentCompass.showDatePickerDialog(this, getPresenter().getDate());
    }

    @Override
    public void onDateSet(DatePickerDialog datePickerDialog, int year, int month, int day) {
        getPresenter().onDateSet(year, month, day);
        initAutoCompleteDate();
    }

    @Override
    protected BucketItemEditPresenter createPresenter(Bundle savedInstanceState) {
        return new BucketItemEditPresenter(getArguments());
    }

    @Override
    public void afterCreateView(View rootView) {
        super.afterCreateView(rootView);
        if (imageViewDone != null) {
            setHasOptionsMenu(false);
        }
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
    public void updatePhotos() {
        bucketPhotosView.init(this, injector, BucketPhotosView.Type.EDIT);
        bucketPhotosView.multiSelectAvailable(true);
        bucketPhotosView.setCaptureImageCallback(getPresenter().getPhotoChooseCallback());
        bucketPhotosView.setFbCallback(getPresenter().getFbCallback());
        bucketPhotosView.setChooseImageCallback(getPresenter().getGalleryChooseCallback());
        bucketPhotosView.setMulitImageCallback(getPresenter().getMultiSelectPickCallback());
    }

    @Override
    public IBucketPhotoView getBucketPhotosView() {
        return bucketPhotosView;
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        eventBus.postSticky(new ActivityResult(requestCode, resultCode, data));
    }


    public void onEvent(ActivityResult event) {
        eventBus.removeStickyEvent(event);
        handler.post(() -> bucketPhotosView.onActivityResult(event.requestCode, event.resultCode, event.data));
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
        editTextDescription.checkCharactersCount();
    }


}




