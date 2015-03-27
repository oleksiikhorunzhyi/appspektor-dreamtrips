package com.worldventures.dreamtrips.modules.bucketlist.view.fragment;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;

import com.fourmob.datetimepicker.date.DatePickerDialog;
import com.techery.spares.annotations.Layout;
import com.techery.spares.annotations.MenuResource;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.navigation.FragmentCompass;
import com.worldventures.dreamtrips.modules.bucketlist.model.BucketItem;
import com.worldventures.dreamtrips.modules.bucketlist.model.CategoryItem;
import com.worldventures.dreamtrips.modules.bucketlist.presenter.BucketItemEditPresenter;
import com.worldventures.dreamtrips.modules.bucketlist.view.activity.BucketListPopularActivity;
import com.worldventures.dreamtrips.modules.common.view.fragment.BaseFragment;

import java.util.List;

import javax.inject.Inject;

import butterknife.InjectView;
import butterknife.OnClick;
import butterknife.Optional;

@Layout(R.layout.fragment_bucket_item_edit)
@MenuResource(R.menu.menu_bucket_quick)
public class BucketItemEditFragment extends BaseFragment<BucketItemEditPresenter> implements BucketItemEditPresenter.View,
        DatePickerDialog.OnDateSetListener {

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
    protected android.widget.CheckBox checkBox;

    @InjectView(R.id.spinnerCategory)
    protected Spinner spinnerCategory;

    @Override
    public void afterCreateView(View rootView) {
        super.afterCreateView(rootView);
        spinnerCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        if (imageViewDone != null)
            setHasOptionsMenu(false);
    }

    @Optional
    @OnClick(R.id.mainFrame)
    void onClick() {
        getPresenter().frameClicked();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_done:
                getPresenter().saveItem();
                break;

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
        getPresenter().onDataSet(year, month, day);
    }

    @Override
    protected BucketItemEditPresenter createPresenter(Bundle savedInstanceState) {
        BucketTabsFragment.Type type = (BucketTabsFragment.Type) getArguments().getSerializable(BucketListPopularActivity.EXTRA_TYPE);
        BucketItem item = (BucketItem) getArguments().getSerializable(BucketListPopularActivity.EXTRA_ITEM);
        return new BucketItemEditPresenter(this, type, item);
    }

    @Override
    public void setCategoryItems(List<CategoryItem> items) {
        ArrayAdapter<CategoryItem> adapter = new ArrayAdapter<CategoryItem>(getActivity(), android.R.layout.simple_spinner_item, items);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategory.setAdapter(adapter);
    }

    @Override
    public void setCategory(String name) {
        if (TextUtils.isEmpty(name)) {
            spinnerCategory.setPromptId(R.string.category);
        } else {
            spinnerCategory.setPrompt(name);
        }
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
        return editTextTime.getText().toString();
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
    public void setStatus(boolean isCompleted) {
        checkBox.setChecked(isCompleted);
    }

    @Override
    public boolean getStatus() {
        return checkBox.isChecked();
    }
}




