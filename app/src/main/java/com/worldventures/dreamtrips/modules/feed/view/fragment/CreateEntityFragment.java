package com.worldventures.dreamtrips.modules.feed.view.fragment;

import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.navigation.AnimationConfig;
import com.worldventures.dreamtrips.core.navigation.Route;
import com.worldventures.dreamtrips.core.navigation.router.NavigationConfigBuilder;
import com.worldventures.dreamtrips.modules.common.model.MediaAttachment;
import com.worldventures.dreamtrips.modules.feed.bundle.CreateEntityBundle;
import com.worldventures.dreamtrips.modules.feed.bundle.DescriptionBundle;
import com.worldventures.dreamtrips.modules.feed.model.ImmutableVideoCreationModel;
import com.worldventures.dreamtrips.modules.feed.model.PhotoCreationItem;
import com.worldventures.dreamtrips.modules.feed.model.PostDescription;
import com.worldventures.dreamtrips.modules.feed.model.VideoCreationModel;
import com.worldventures.dreamtrips.modules.feed.presenter.CreateEntityPresenter;
import com.worldventures.dreamtrips.modules.feed.view.cell.delegate.VideoCreationCellDelegate;
import com.worldventures.dreamtrips.modules.feed.view.custom.PhotoStripView;
import com.worldventures.dreamtrips.modules.picker.view.dialog.MediaPickerDialog;

import butterknife.InjectView;
import butterknife.OnClick;
import icepick.State;

public abstract class CreateEntityFragment extends ActionEntityFragment<CreateEntityPresenter, CreateEntityBundle>
      implements CreateEntityPresenter.View {

   @State boolean pickerDisabled;
   @State boolean imageFromArgsAlreadyAttached;

   @InjectView(R.id.photo_strip) PhotoStripView photoStripView;

   @Override
   public void afterCreateView(View rootView) {
      super.afterCreateView(rootView);

      RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) photosList.getLayoutParams();
      params.bottomMargin = (int) getResources().getDimension(R.dimen.photo_strip_item_size);
      photosList.setLayoutParams(params);

      adapter.registerDelegate(ImmutableVideoCreationModel.class, new VideoCreationCellDelegate() {
         @Override
         public void onRemoveClicked(VideoCreationModel model) {
            getPresenter().removeVideo(model);
         }

         @Override
         public void onCellClicked(VideoCreationModel model) { }
      });
   }

   @Override
   public void onViewCreated(View view, Bundle savedInstanceState) {
      super.onViewCreated(view, savedInstanceState);
      attachImages();
   }

   @Override
   protected CreateEntityPresenter createPresenter(Bundle savedInstanceState) {
      return new CreateEntityPresenter(getArgs().getOrigin());
   }

   @Override
   protected void openPhotoCreationDescriptionDialog(PostDescription model) {
      router.moveTo(Route.PHOTO_CREATION_DESC, NavigationConfigBuilder.forActivity()
            .data(new DescriptionBundle(model.getDescription()))
            .transparentBackground(true)
            .animationConfig(new AnimationConfig(R.anim.fade_in, R.anim.fade_out))
            .build());
   }

   @Override
   protected int getPostButtonText() {
      return R.string.post;
   }

   @Override
   public void onRemoveClicked(PhotoCreationItem uploadTask) {
      super.onRemoveClicked(uploadTask);
      getPresenter().removeImage(uploadTask);
   }

   @Override
   public void removeImage(PhotoCreationItem uploadTask) {
      adapter.remove(uploadTask);
   }

   @Override
   public void setEnabledImagePicker(boolean enabled) {
      pickerDisabled = !enabled;
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
      getPresenter().showMediaPicker();
   }

   protected void updatePickerState() {
      image.setEnabled(!pickerDisabled);
   }

   @Override
   public void showMediaPicker(int photoPickLimit, int videoPickLimit, int maxVideoDuration) {
      final MediaPickerDialog mediaPickerDialog = new MediaPickerDialog(getContext());
      mediaPickerDialog.setOnDoneListener(getPresenter()::attachMedia);
      mediaPickerDialog.show(photoPickLimit);
   }

   @Override
   public PhotoStripView getPhotoStrip() {
      return photoStripView;
   }

   protected void hideMediaPicker() {
      router.moveTo(Route.MEDIA_PICKER, NavigationConfigBuilder.forRemoval()
            .fragmentManager(getChildFragmentManager())
            .containerId(R.id.picker_container)
            .build());
   }

   protected void attachImages() {
      if (!imageFromArgsAlreadyAttached && getMediaAttachment() != null) {
         getPresenter().attachMedia(getMediaAttachment());
         imageFromArgsAlreadyAttached = true;
      }
   }

   private MediaAttachment getMediaAttachment() {
      return getArgs() != null && getArgs().getMediaAttachment() != null ? getArgs().getMediaAttachment() : null;
   }
}
