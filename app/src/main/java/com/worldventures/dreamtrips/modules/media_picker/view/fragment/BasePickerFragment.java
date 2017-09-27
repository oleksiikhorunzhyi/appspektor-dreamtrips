package com.worldventures.dreamtrips.modules.media_picker.view.fragment;

import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;

import com.innahema.collections.query.queriables.Queryable;
import com.techery.spares.adapter.BaseDelegateAdapter;
import com.techery.spares.annotations.Layout;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.utils.ViewUtils;
import com.worldventures.dreamtrips.modules.media_picker.model.MediaPickerModel;
import com.worldventures.dreamtrips.modules.media_picker.model.VideoPickerModel;
import com.worldventures.dreamtrips.modules.media_picker.presenter.BasePickerPresenter;
import com.worldventures.dreamtrips.modules.common.view.fragment.BaseFragmentWithArgs;
import com.worldventures.dreamtrips.modules.common.view.util.PhotoPickerDelegate;
import com.worldventures.dreamtrips.social.ui.feed.view.util.GridAutofitLayoutManager;

import java.util.List;

import javax.inject.Inject;

import butterknife.InjectView;

@Layout(R.layout.fragment_dt_gallery)
public abstract class BasePickerFragment<T extends BasePickerPresenter, B extends Parcelable> extends BaseFragmentWithArgs<T, B> implements BasePickerPresenter.View {

   @InjectView(R.id.picker) protected RecyclerView picker;
   @InjectView(R.id.progress) protected ProgressBar progressBar;

   @Inject protected PhotoPickerDelegate photoPickerDelegate;

   protected BaseDelegateAdapter adapter;

   @Override
   public void afterCreateView(View rootView) {
      super.afterCreateView(rootView);
      progressBar.setVisibility(View.VISIBLE);
      adapter = new BaseDelegateAdapter<>(getContext(), this);
      registerCells();

      picker.setLayoutManager(new GridAutofitLayoutManager(getContext(), getContext().getResources()
            .getDimension(R.dimen.photo_picker_size)));
      picker.setAdapter(adapter);
   }

   @Override
   public void onResume() {
      super.onResume();
      if (photoPickerDelegate != null) {
         photoPickerDelegate.attachScrollableView(picker);
         photoPickerDelegate.setSelectedPhotosProvider(new PhotoPickerDelegate.SelectedPhotosProvider() {
            @Override
            public List<MediaPickerModel> provideSelectedPhotos() {
               return getPresenter().getSelectedPhotos();
            }

            @Override
            public VideoPickerModel provideSelectedVideo() {
               return getPresenter().getSelectedVideo();
            }

            @Override
            public int getType() {
               return getPhotosType();
            }

         });
      }
   }

   @Override
   public void onDestroyView() {
      super.onDestroyView();
      photoPickerDelegate.setSelectedPhotosProvider(null);
   }

   protected abstract void registerCells();

   protected abstract int getPhotosType();

   @Override
   public void onViewCreated(View view, Bundle savedInstanceState) {
      super.onViewCreated(view, savedInstanceState);
      getPresenter().setPhotoPickLimit(photoPickerDelegate.getPickLimit());
   }

   @Override
   public void updateItem(MediaPickerModel item) {
      adapter.updateItem(item);
   }

   @Override
   public void addItems(List<MediaPickerModel> items) {
      updatePickedItemsCount(Queryable.from(items).count(MediaPickerModel::isChecked));
      progressBar.setVisibility(View.GONE);
      adapter.addItems(items);
   }

   @Override
   public void updatePickedItemsCount(int count) {
      photoPickerDelegate.updatePickedItemsCount(count);
   }

   @Override
   public boolean isPhotosMultiPickEnabled() {
      return photoPickerDelegate.isMultiPickEnabled();
   }

   @Override
   public boolean isVisibleOnScreen() {
      return ViewUtils.isPartVisibleOnScreen(this);
   }
}
