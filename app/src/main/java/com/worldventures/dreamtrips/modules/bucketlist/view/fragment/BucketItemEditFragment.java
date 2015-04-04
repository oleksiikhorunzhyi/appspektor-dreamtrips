package com.worldventures.dreamtrips.modules.bucketlist.view.fragment;

import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;

import com.fourmob.datetimepicker.date.DatePickerDialog;
import com.techery.spares.adapter.BaseArrayListAdapter;
import com.techery.spares.annotations.Layout;
import com.techery.spares.annotations.MenuResource;
import com.techery.spares.module.Injector;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.navigation.FragmentCompass;
import com.worldventures.dreamtrips.modules.bucketlist.model.BucketPhoto;
import com.worldventures.dreamtrips.core.utils.ViewUtils;
import com.worldventures.dreamtrips.modules.bucketlist.model.CategoryItem;
import com.worldventures.dreamtrips.modules.bucketlist.presenter.BucketItemEditPresenter;
import com.worldventures.dreamtrips.modules.bucketlist.view.cell.BucketPhotoCell;
import com.worldventures.dreamtrips.modules.bucketlist.view.cell.BucketUploadPhotoCell;
import com.worldventures.dreamtrips.modules.common.view.fragment.BaseFragment;

import java.util.List;

import javax.inject.Inject;

import butterknife.InjectView;
import butterknife.OnClick;
import butterknife.Optional;

@Layout(R.layout.fragment_bucket_item_edit)
@MenuResource(R.menu.menu_bucket_quick)
public class BucketItemEditFragment extends BaseFragment<BucketItemEditPresenter>
        implements BucketItemEditPresenter.View, DatePickerDialog.OnDateSetListener {

    @Inject
    protected FragmentCompass fragmentCompass;

    @Optional
    @InjectView(R.id.done)
    protected ImageView imageViewDone;

    @InjectView(R.id.editTextTitle)
    protected EditText editTextTitle;

    @InjectView(R.id.editTextDescription)
    protected EditText editTextDescription;

    @InjectView(R.id.editTextPeople)
    protected EditText editTextPeople;

    @InjectView(R.id.editTextTags)
    protected EditText editTextTags;

    @InjectView(R.id.editTextTime)
    protected EditText editTextTime;

    @InjectView(R.id.checkBoxDone)
    protected CheckBox checkBox;

    @InjectView(R.id.spinnerCategory)
    protected Spinner spinnerCategory;

    @InjectView(R.id.lvImages)
    protected RecyclerView lvImages;

    private BaseArrayListAdapter imagesAdapter;

    private boolean dateSelected = false;
    private boolean categorySelected = false;

    @Override
    public void onResume() {
        super.onResume();
        if (imageViewDone != null) {
            setHasOptionsMenu(false);
        }
    }

    @Optional
    @OnClick(R.id.mainFrame)
    void onClick() {
        getActivity().onBackPressed();
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

    @OnClick(R.id.editTextTime)
    void onTimeClicked() {
        fragmentCompass.showDatePickerDialog(this, getPresenter().getDate());
    }

    @Override
    public void onDateSet(DatePickerDialog datePickerDialog, int year, int month, int day) {
        dateSelected = true;
        getPresenter().onDataSet(year, month, day);
    }

    @Override
    protected BucketItemEditPresenter createPresenter(Bundle savedInstanceState) {
        return new BucketItemEditPresenter(this, getArguments());
    }

    @Override
    public void afterCreateView(View rootView) {
        super.afterCreateView(rootView);
        imagesAdapter = new BaseArrayListAdapter(getActivity(), (Injector) getActivity()) {
            @Override
            public void clear() {
                Object item = null;
                if (!items.isEmpty()) {
                    item = getItem(0); 
                }
                super.clear();
                if (item != null) {
                    addItem(item);
                }
            }
        };
        imagesAdapter.registerCell(BucketPhoto.class, BucketPhotoCell.class);
        //imagesAdapter.addItem(new BucketUploadPhotoCell());
        lvImages.setAdapter(imagesAdapter);
    }

    @Override
    public void setCategoryItems(List<CategoryItem> items) {
        ArrayAdapter<CategoryItem> adapter = new ArrayAdapter<>(getActivity(),
                R.layout.spinner_dropdown_item, items);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategory.setVisibility(View.VISIBLE);
        spinnerCategory.setAdapter(adapter);
        spinnerCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                categorySelected = true;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    @Override
    public void done() {
        getActivity().onBackPressed();
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
    public void setTags(String tags) {
        editTextTags.setText(tags);
    }

    @Override
    public void setPeople(String people) {
        editTextPeople.setText(people);
    }

    @Override
    public void setTime(String time) {
        editTextTime.setText(time);
    }

    @Override
    public void setDescription(String description) {
        editTextDescription.setText(description);
    }

    @Override
    public void setTitle(String title) {
        editTextTitle.setText(title);
    }

    @Override
    public String getTags() {
        return editTextTags.getText().toString();
    }

    @Override
    public String getPeople() {
        return editTextPeople.getText().toString();
    }

    @Override
    public String getTime() {
        if (dateSelected) {
            return editTextTime.getText().toString();
        } else {
            return null;
        }
    }

    @Override
    public String getTitle() {
        return editTextTitle.getText().toString();
    }

    @Override
    public String getDescription() {
        return editTextDescription.getText().toString();
    }

    @Override
    public void addImages(List<BucketPhoto> images) {
        imagesAdapter.clear();
        imagesAdapter.addItems(images);
    }

    @Override
    public void setStatus(boolean isCompleted) {
        checkBox.setChecked(isCompleted);
    }

    @Override
    public boolean getStatus() {
        return checkBox.isChecked();
    }

}




