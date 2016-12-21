package com.worldventures.dreamtrips.modules.feed.view.cell.delegate;

import android.content.Context;

import com.afollestad.materialdialogs.MaterialDialog;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.background_uploading.model.PostCompoundOperationModel;
import com.worldventures.dreamtrips.modules.feed.model.uploading.UploadingPostsList;
import com.worldventures.dreamtrips.modules.feed.presenter.UploadingListenerPresenter;
import com.worldventures.dreamtrips.modules.feed.view.cell.uploading.UploadingPhotoPostsSectionCell;

public class UploadingCellDelegate implements UploadingPhotoPostsSectionCell.Delegate {

   private UploadingListenerPresenter presenter;
   private Context context;

   public UploadingCellDelegate(UploadingListenerPresenter presenter, Context context) {
      this.presenter = presenter;
      this.context = context;
   }

   @Override
   public void onUploadPauseClicked(PostCompoundOperationModel model) {
      presenter.onUploadPaused(model);
   }

   @Override
   public void onUploadResumeClicked(PostCompoundOperationModel model) {
      presenter.onUploadResume(model);
   }

   @Override
   public void onUploadRetryClicked(PostCompoundOperationModel model) {
      presenter.onUploadRetry(model);
   }

   @Override
   public void onUploadCancelClicked(PostCompoundOperationModel model) {
      MaterialDialog.Builder builder = new MaterialDialog.Builder(context);
      builder.content(context.getString(R.string.uploading_post_cancel_upload_message))
            .positiveText(R.string.uploading_post_cancel_upload_positive_button)
            .negativeText(R.string.uploading_post_cancel_upload_negative_button)
            .onPositive((dialog, which) -> presenter.onUploadCancel(model))
            .show();
   }

   @Override
   public void onCellClicked(UploadingPostsList model) {

   }
}
