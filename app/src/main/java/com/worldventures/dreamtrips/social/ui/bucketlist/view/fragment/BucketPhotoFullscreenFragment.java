package com.worldventures.dreamtrips.social.ui.bucketlist.view.fragment;

import android.app.Dialog;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;

import com.worldventures.core.ui.annotations.Layout;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.common.view.fragment.BaseFragmentWithArgs;
import com.worldventures.dreamtrips.social.ui.bucketlist.bundle.BucketFullscreenBundle;
import com.worldventures.dreamtrips.social.ui.bucketlist.model.BucketPhoto;
import com.worldventures.dreamtrips.social.ui.bucketlist.presenter.BucketFullscreenPresenter;
import com.worldventures.dreamtrips.social.ui.tripsimages.view.custom.ImageryView;

import butterknife.InjectView;
import butterknife.OnClick;
import cn.pedant.SweetAlert.SweetAlertDialog;

@Layout(R.layout.fragment_fullscreen_bucket_photo)
public class BucketPhotoFullscreenFragment extends BaseFragmentWithArgs<BucketFullscreenPresenter, BucketFullscreenBundle> implements BucketFullscreenPresenter.View {

   @InjectView(R.id.checkBox) CheckBox checkBox;
   @InjectView(R.id.delete) ImageView delete;
   @InjectView(R.id.iv_image) protected ImageryView imageryView;

   private SweetAlertDialog progressDialog;

   @Override
   protected BucketFullscreenPresenter createPresenter(Bundle savedInstanceState) {
      return new BucketFullscreenPresenter(getArgs().getBucketPhoto(), getArgs().getBucketItem());
   }

   @Override
   public void setBucketPhoto(BucketPhoto bucketPhoto) {
      imageryView.loadImage(bucketPhoto.getImagePath());
   }

   @Override
   public void showCoverProgress() {
      progressDialog = new SweetAlertDialog(getActivity(), SweetAlertDialog.PROGRESS_TYPE);
      progressDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
      progressDialog.setTitleText(getString(R.string.uploading_photo));
      progressDialog.setCancelable(false);
      progressDialog.show();
   }

   @OnClick(R.id.delete)
   public void onDelete() {
      deletePhoto();
   }

   @Override
   public void hideCoverProgress() {
      if (progressDialog != null && progressDialog.isShowing()) progressDialog.dismissWithAnimation();
   }

   @Override
   public void hideDeleteBtn() {
      delete.setVisibility(View.GONE);
   }

   @Override
   public void showDeleteBtn() {
      delete.setVisibility(View.VISIBLE);
   }

   @Override
   public void hideCheckBox() {
      checkBox.setVisibility(View.GONE);
   }

   @Override
   public void showCheckbox(boolean currentCover) {
      checkBox.setText(currentCover ? R.string.bucket_current_cover : R.string.bucket_photo_cover);
      checkBox.setClickable(!currentCover);
      checkBox.setVisibility(View.VISIBLE);
      checkBox.setChecked(currentCover);
   }

   @Override
   public void onResume() {
      super.onResume();
      checkBox.setOnCheckedChangeListener((cb, b) -> {
         checkBox.setClickable(!b);
         getPresenter().onCheckboxPressed(b);
      });
   }

   private void deletePhoto() {
      Dialog dialog = new SweetAlertDialog(getContext(), SweetAlertDialog.WARNING_TYPE).setTitleText(getResources().getString(R.string.photo_delete))
            .setContentText(getResources().getString(R.string.photo_delete_caption))
            .setConfirmText(getResources().getString(R.string.post_delete_confirm))
            .setConfirmClickListener(sDialog -> {
               sDialog.dismissWithAnimation();
               getPresenter().onDeleteAction();
            });
      dialog.setCanceledOnTouchOutside(true);
      dialog.show();
   }

}