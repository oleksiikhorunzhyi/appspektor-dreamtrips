package com.worldventures.dreamtrips.modules.feed.view.fragment;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.navigation.Route;
import com.worldventures.dreamtrips.core.navigation.router.NavigationConfigBuilder;
import com.worldventures.dreamtrips.modules.common.model.MediaAttachment;
import com.worldventures.dreamtrips.modules.common.view.bundle.PickerBundle;
import com.worldventures.dreamtrips.modules.feed.bundle.CreateEntityBundle;
import com.worldventures.dreamtrips.modules.feed.model.PhotoCreationItem;
import com.worldventures.dreamtrips.modules.feed.presenter.CreateEntityPresenter;

import butterknife.InjectView;
import butterknife.OnClick;
import icepick.State;

public abstract class CreateEntityFragment extends ActionEntityFragment<CreateEntityPresenter, CreateEntityBundle>
      implements CreateEntityPresenter.View {

   @State boolean pickerDisabled;
   @State boolean imageFromArgsAlreadyAttached;

   @InjectView(R.id.picker_container) ViewGroup pickerContainer;

   @Override
   public void onViewCreated(View view, Bundle savedInstanceState) {
      super.onViewCreated(view, savedInstanceState);
      pickerContainer.setOnHierarchyChangeListener(new ViewGroup.OnHierarchyChangeListener() {
         @Override
         public void onChildViewAdded(View parent, View child) {

         }

         @Override
         public void onChildViewRemoved(View parent, View child) {
            if (isResumed()) backStackDelegate.setListener(() -> onBack());
         }
      });
      //
      attachImages();
   }

   @Override
   protected CreateEntityPresenter createPresenter(Bundle savedInstanceState) {
      return new CreateEntityPresenter(getArgs().getOrigin());
   }

   @Override
   public void cancel() {
      pickerContainer.setOnHierarchyChangeListener(null);
      super.cancel();
   }

   @Override
   protected int getPostButtonText() {
      return R.string.post;
   }

   @Override
   public void onRemoveClicked(PhotoCreationItem uploadTask) {
      super.onRemoveClicked(uploadTask);
      boolean removed = getPresenter().removeImage(uploadTask);
      if (removed) {
         adapter.remove(uploadTask);
         adapter.notifyDataSetChanged();
      }
   }

   @Override
   public void enableImagePicker() {
      pickerDisabled = false;
      updatePickerState();
   }

   @Override
   public void disableImagePicker() {
      pickerDisabled = true;
      updatePickerState();
   }

   @Override
   protected void onTitleFocusChanged(boolean hasFocus) {
      super.onTitleFocusChanged(hasFocus);
      if (hasFocus) hideMediaPicker();
      else name.requestFocus();
   }

   @OnClick(R.id.image)
   void onImage() {
      showMediaPicker();
   }

   protected void updatePickerState() {
      image.setEnabled(!pickerDisabled);
   }

   protected void showMediaPicker() {
      router.moveTo(Route.MEDIA_PICKER, NavigationConfigBuilder.forFragment()
            .backStackEnabled(false)
            .fragmentManager(getChildFragmentManager())
            .containerId(R.id.picker_container)
            .data(new PickerBundle(0, getPresenter().getRemainingPhotosCount()))
            .build());
   }

   protected void hideMediaPicker() {
      router.moveTo(Route.MEDIA_PICKER, NavigationConfigBuilder.forRemoval()
            .fragmentManager(getChildFragmentManager())
            .containerId(R.id.picker_container)
            .build());
   }

   protected void attachImages() {
      if (!imageFromArgsAlreadyAttached && getMediaAttachment() != null) {
         getPresenter().attachImages(getMediaAttachment());
         imageFromArgsAlreadyAttached = true;
      }
   }

   private MediaAttachment getMediaAttachment() {
      return getArgs() != null && getArgs().getMediaAttachment() != null ? getArgs().getMediaAttachment() : null;
   }
}
