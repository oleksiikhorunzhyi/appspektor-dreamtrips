package com.worldventures.dreamtrips.modules.bucketlist.view.fragment;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;

import com.fourmob.datetimepicker.date.DatePickerDialog;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.techery.spares.annotations.Layout;
import com.techery.spares.annotations.MenuResource;
import com.techery.spares.utils.ui.OrientationUtil;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.navigation.Route;
import com.worldventures.dreamtrips.core.navigation.router.NavigationConfigBuilder;
import com.worldventures.dreamtrips.core.rx.RxBaseFragmentWithArgs;
import com.worldventures.dreamtrips.core.utils.DateTimeUtils;
import com.worldventures.dreamtrips.modules.bucketlist.model.BucketPhoto;
import com.worldventures.dreamtrips.modules.bucketlist.model.CategoryItem;
import com.worldventures.dreamtrips.modules.bucketlist.presenter.BucketItemEditPresenter;
import com.worldventures.dreamtrips.modules.common.model.EntityStateHolder;
import com.worldventures.dreamtrips.modules.bucketlist.view.cell.delegate.BucketPhotoUploadCellDelegate;
import com.worldventures.dreamtrips.modules.bucketlist.view.custom.BucketHorizontalPhotosView;
import com.worldventures.dreamtrips.modules.common.view.bundle.BucketBundle;
import com.worldventures.dreamtrips.modules.common.view.bundle.PickerBundle;
import com.worldventures.dreamtrips.modules.tripsimages.bundle.FullScreenImagesBundle;

import java.util.Calendar;
import java.util.List;

import butterknife.InjectView;
import butterknife.OnClick;
import butterknife.Optional;

import static com.worldventures.dreamtrips.modules.bucketlist.presenter.BucketItemEditPresenter.BUCKET_MEDIA_REQUEST_ID;

@Layout(R.layout.fragment_bucket_item_edit)
@MenuResource(R.menu.menu_bucket_quick)
public class BucketItemEditFragment extends RxBaseFragmentWithArgs<BucketItemEditPresenter, BucketBundle> implements BucketItemEditPresenter.View, DatePickerDialog.OnDateSetListener {

   @Optional @InjectView(R.id.done) ImageView imageViewDone;
   @Optional @InjectView(R.id.bucket_header) ViewGroup bucketHeader;
   @InjectView(R.id.editTextTitle) EditText editTextTitle;
   @InjectView(R.id.editTextDescription) MaterialEditText editTextDescription;
   @InjectView(R.id.editTextPeople) EditText editTextPeople;
   @InjectView(R.id.editTextTags) EditText editTextTags;
   @InjectView(R.id.editTextTime) AutoCompleteTextView autoCompleteTextViwDate;
   @InjectView(R.id.checkBoxDone) CheckBox checkBox;
   @InjectView(R.id.spinnerCategory) Spinner spinnerCategory;
   @InjectView(R.id.lv_items) BucketHorizontalPhotosView bucketPhotosView;
   @InjectView(R.id.loading_view) ViewGroup loadingView;

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
      if (getArgs().isLock()) {
         OrientationUtil.lockOrientation(getActivity());
      }
      if (bucketHeader != null) bucketHeader.getBackground().mutate().setAlpha(255);
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
         onSaveItem();
      }
      return super.onOptionsItemSelected(item);
   }

   @Optional
   @OnClick(R.id.done)
   void onDone() {
      onSaveItem();
   }

   private void onSaveItem() {
      hideMediaPicker();
      getPresenter().saveItem();
   }

   private void openDatePicker() {
      Calendar calendar = Calendar.getInstance();
      calendar.setTime(getPresenter().getDate());
      DatePickerDialog datePickerDialog = DatePickerDialog.newInstance(this, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar
            .get(Calendar.DAY_OF_MONTH), false);
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
   public void afterCreateView(android.view.View rootView) {
      super.afterCreateView(rootView);
      setupPhotoCellCallbacks();
      bucketPhotosView.init(this);
      if (imageViewDone != null) {
         setHasOptionsMenu(false);
      }
   }

   protected void setupPhotoCellCallbacks() {
      bucketPhotosView.enableAddPhotoCell(model -> {
         if (isVisibleOnScreen()) {
            showMediaPicker();
         }
      });
      bucketPhotosView.setPhotoCellDelegate(new BucketPhotoUploadCellDelegate() {
         @Override
         public void deletePhotoRequest(BucketPhoto photo) {
            getPresenter().deletePhotoRequest(photo);
         }

         @Override
         public void selectPhotoAsCover(BucketPhoto photo) {
            getPresenter().saveCover(photo);
         }

         @Override
         public void onCellClicked(EntityStateHolder<BucketPhoto> model) {
            getPresenter().onPhotoCellClicked(model);
         }
      });
   }

   @Override
   public void onDestroyView() {
      super.onDestroyView();
      OrientationUtil.unlockOrientation(getActivity());
   }

   @Override
   public void setCategoryItems(List<CategoryItem> items) {
      ArrayAdapter<CategoryItem> adapter = new ArrayAdapter<>(getActivity(), R.layout.spinner_dropdown_item, items);
      adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
      spinnerCategory.setVisibility(android.view.View.VISIBLE);
      spinnerCategory.setAdapter(adapter);
      AdapterView.OnItemSelectedListener onItemSelectedListenerCategory = new AdapterView.OnItemSelectedListener() {
         @Override
         public void onItemSelected(AdapterView<?> parent, android.view.View view, int position, long id) {
            categorySelected = true;
         }

         @Override
         public void onNothingSelected(AdapterView<?> parent) {
            //nothing to do here
         }
      };
      spinnerCategory.setOnItemSelectedListener(onItemSelectedListenerCategory);
   }

   private void initAutoCompleteDate() {
      String[] items = getResources().getStringArray(R.array.bucket_date_items);
      ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), R.layout.item_dropdown, items);
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
      loadingView.setVisibility(android.view.View.GONE);
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
   public void hideMediaPicker() {
      router.moveTo(Route.MEDIA_PICKER, NavigationConfigBuilder.forRemoval()
            .fragmentManager(getChildFragmentManager())
            .containerId(R.id.picker_container)
            .build());
   }

   @Override
   public void showMediaPicker() {
      router.moveTo(Route.MEDIA_PICKER, NavigationConfigBuilder.forFragment()
            .backStackEnabled(false)
            .fragmentManager(getChildFragmentManager())
            .containerId(R.id.picker_container)
            .data(new PickerBundle(BUCKET_MEDIA_REQUEST_ID, 5, true))
            .build());
   }

   @Override
   public void showLoading() {
      loadingView.setVisibility(android.view.View.VISIBLE);
   }

   @Override
   public void hideLoading() {
      loadingView.setVisibility(android.view.View.GONE);
   }

   @Override
   public void addItemInProgressState(EntityStateHolder<BucketPhoto> photoEntityStateHolder) {
      bucketPhotosView.addItemInProgressState(photoEntityStateHolder);
   }

   @Override
   public void changeItemState(EntityStateHolder<BucketPhoto> photoEntityStateHolder) {
      bucketPhotosView.changeItemState(photoEntityStateHolder);
   }

   @Override
   public void deleteImage(EntityStateHolder<BucketPhoto> photoStateHolder) {
      bucketPhotosView.removeItem(photoStateHolder);
   }

   @Override
   public void openFullscreen(FullScreenImagesBundle data) {
      router.moveTo(Route.FULLSCREEN_PHOTO_LIST, NavigationConfigBuilder.forActivity().data(data).build());
   }

   @Override
   public void setImages(List photos) {
      bucketPhotosView.setImages(photos);
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