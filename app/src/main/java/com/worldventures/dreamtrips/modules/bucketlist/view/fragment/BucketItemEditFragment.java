package com.worldventures.dreamtrips.modules.bucketlist.view.fragment;

import android.os.Bundle;
import android.text.TextUtils;
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
import com.worldventures.dreamtrips.modules.bucketlist.model.BucketItem;
import com.worldventures.dreamtrips.modules.bucketlist.model.BucketPhoto;
import com.worldventures.dreamtrips.modules.bucketlist.model.CategoryItem;
import com.worldventures.dreamtrips.modules.bucketlist.presenter.BucketItemEditPresenter;
import com.worldventures.dreamtrips.modules.bucketlist.view.cell.delegate.BucketPhotoUploadCellDelegate;
import com.worldventures.dreamtrips.modules.bucketlist.view.custom.BucketHorizontalPhotosView;
import com.worldventures.dreamtrips.modules.common.model.EntityStateHolder;
import com.worldventures.dreamtrips.modules.common.view.bundle.BucketBundle;
import com.worldventures.dreamtrips.modules.common.view.bundle.PickerBundle;
import com.worldventures.dreamtrips.modules.tripsimages.bundle.FullScreenImagesBundle;

import java.util.Calendar;
import java.util.List;

import butterknife.InjectView;
import butterknife.OnClick;
import butterknife.Optional;
import icepick.State;

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
   @InjectView(R.id.editTextTime) AutoCompleteTextView dateTextView;
   @InjectView(R.id.checkBoxDone) CheckBox checkBox;
   @InjectView(R.id.spinnerCategory) Spinner spinnerCategory;
   @InjectView(R.id.lv_items) BucketHorizontalPhotosView bucketPhotosView;
   @InjectView(R.id.loading_view) ViewGroup loadingView;

   @State Integer categorySelectedPosition;
   @State boolean isDoneCheckboxStatusWasChanged;

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
      dateTextView.showDropDown();
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
      checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> isDoneCheckboxStatusWasChanged = true);
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
   public void setCategoryItems(List<CategoryItem> items, CategoryItem selectedItem) {
      ArrayAdapter<CategoryItem> adapter = new ArrayAdapter<>(getActivity(), R.layout.spinner_dropdown_item, items);
      adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
      spinnerCategory.setVisibility(android.view.View.VISIBLE);
      spinnerCategory.setAdapter(adapter);
      AdapterView.OnItemSelectedListener onItemSelectedListenerCategory = new AdapterView.OnItemSelectedListener() {
         @Override
         public void onItemSelected(AdapterView<?> parent, android.view.View view, int position, long id) {
            categorySelectedPosition = position;
         }

         @Override
         public void onNothingSelected(AdapterView<?> parent) {
            //nothing to do here
         }
      };
      spinnerCategory.setOnItemSelectedListener(onItemSelectedListenerCategory);
      int categoryPosition = categorySelectedPosition == null ? items.indexOf(selectedItem) : categorySelectedPosition;
      spinnerCategory.setSelection(categoryPosition);
   }

   private void initAutoCompleteDate() {
      String[] items = getResources().getStringArray(R.array.bucket_date_items);
      ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), R.layout.item_dropdown, items);
      dateTextView.setAdapter(adapter);
      dateTextView.setOnItemClickListener((parent, view, position, id) -> {
         if (position == 0) {
            dateTextView.setText("");
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
      if (categorySelectedPosition != null) {
         return (CategoryItem) spinnerCategory.getSelectedItem();
      } else {
         return null;
      }
   }

   @Override
   public void setTime(String time) {
      if (TextUtils.isEmpty(dateTextView.getText())) {
         dateTextView.setText(time);
      }
   }

   @Override
   public String getTags() {
      return editTextTags.getText().toString();
   }

   @Override
   public void setTags(String tags) {
      if (TextUtils.isEmpty(editTextTags.getText())) {
         editTextTags.setText(tags);
      }
   }

   @Override
   public String getPeople() {
      return editTextPeople.getText().toString();
   }

   @Override
   public void setPeople(String people) {
      if (TextUtils.isEmpty(editTextPeople.getText())) {
         editTextPeople.setText(people);
      }
   }

   @Override
   public String getTitle() {
      return editTextTitle.getText().toString();
   }

   @Override
   public void setBucketItem(BucketItem bucketItem) {
      if (TextUtils.isEmpty(editTextTitle.getText())) {
         editTextTitle.setText(bucketItem.getName());
      }
      if (TextUtils.isEmpty(editTextDescription.getText())) {
         editTextDescription.setText(bucketItem.getDescription());
      }
   }

   @Override
   public String getDescription() {
      return editTextDescription.getText().toString();
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
   public void setImages(List<EntityStateHolder<BucketPhoto>> photos) {
      bucketPhotosView.setImages(photos);
   }

   @Override
   public boolean getStatus() {
      return checkBox.isChecked();
   }

   @Override
   public void setStatus(boolean isCompleted) {
      if (!isDoneCheckboxStatusWasChanged) {
         checkBox.setChecked(isCompleted);
      }
   }

   @Override
   public void showError() {
      editTextDescription.validate();
   }
}