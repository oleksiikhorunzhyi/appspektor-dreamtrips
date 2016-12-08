package com.worldventures.dreamtrips.modules.feed.view.cell.uploading;

import android.content.Context;
import android.graphics.PorterDuff;
import android.support.annotation.ColorRes;
import android.text.format.DateUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.background_uploading.model.CompoundOperationModel;
import com.worldventures.dreamtrips.modules.background_uploading.model.PhotoAttachment;
import com.worldventures.dreamtrips.modules.background_uploading.model.PostWithAttachmentBody;
import com.worldventures.dreamtrips.modules.feed.view.cell.uploading.preview.PhotoAttachmentPreviewView;
import com.worldventures.dreamtrips.modules.feed.view.cell.uploading.preview.PhotoPreviewViewFactory;
import com.worldventures.dreamtrips.modules.feed.view.cell.uploading.util.UploadingTimeLeftFormatter;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

import static android.text.format.DateUtils.FORMAT_ABBREV_MONTH;
import static android.text.format.DateUtils.FORMAT_SHOW_DATE;
import static android.text.format.DateUtils.FORMAT_SHOW_YEAR;

public class UploadingPhotoPostCell extends FrameLayout {

   @InjectView(R.id.uploading_cell_general_upload_container) ViewGroup generalUploadContainer;
   @InjectView(R.id.uploading_cell_upload_finished_container) View uploadFinishedView;
   @InjectView(R.id.uploading_cell_attachment_container) ViewGroup previewContainer;
   @InjectView(R.id.uploading_cell_upload_title_text_view) TextView titleTextView;
   @InjectView(R.id.uploading_cell_upload_status_text_view) TextView statusTextView;
   @InjectView(R.id.uploading_cell_upload_time_left_text_view) TextView timeLeftTextView;
   @InjectView(R.id.uploading_cell_control_main_action) ImageView mainControlImageView;
   @InjectView(R.id.uploading_cell_progress_bar) ProgressBar progressBar;

   private CompoundOperationModel<PostWithAttachmentBody> compoundOperationModel;
   private UploadingPhotoPostsSectionCell.Delegate cellDelegate;
   private UploadingTimeLeftFormatter timeLeftFormatter;

   public UploadingPhotoPostCell(Context context) {
      super(context);
      init();
   }

   public UploadingPhotoPostCell(Context context, AttributeSet attrs) {
      super(context, attrs);
      init();
   }

   private void init() {
      ButterKnife.inject(this, LayoutInflater.from(getContext())
            .inflate(R.layout.adapter_uploading_item_cell, this, true));
      timeLeftFormatter = new UploadingTimeLeftFormatter(getContext());
   }

   public void update(CompoundOperationModel compoundOperationModel, UploadingPhotoPostsSectionCell.Delegate delegate) {
      this.compoundOperationModel = compoundOperationModel;
      this.cellDelegate = delegate;

      PostWithAttachmentBody postWithAttachmentBody = (PostWithAttachmentBody) compoundOperationModel.body();
      List<PhotoAttachment> attachments = postWithAttachmentBody.attachments();

      PhotoAttachmentPreviewView photoPreviewView = PhotoPreviewViewFactory.provideView(getContext(), attachments);
      photoPreviewView.attachView(previewContainer);
      photoPreviewView.showPreview(attachments);

      // TODO Change to actual time of creation
      long timestampPostCreated = System.currentTimeMillis();
      titleTextView.setText(DateUtils.formatDateTime(getContext(),
            timestampPostCreated, FORMAT_SHOW_DATE | FORMAT_SHOW_YEAR | FORMAT_ABBREV_MONTH));

      progressBar.setProgress(compoundOperationModel.progress());

      updateViewsAccordingToState(compoundOperationModel);
   }

   private void updateViewsAccordingToState(CompoundOperationModel compoundOperationModel) {
      switch (compoundOperationModel.state()) {
         case SCHEDULED:
         case STARTED:
            updateAccordingToStartedState(compoundOperationModel);
            break;
         case FAILED:
            updateAccordingToFailedState();
            break;
         case PAUSED:
         case CANCELED:
            updateAccordingToPausedState();
            break;
         case FINISHED:
            updateAccordingToFinishedState(compoundOperationModel);
            break;
      }
   }

   private void updateAccordingToStartedState(CompoundOperationModel compoundOperationModel) {
      PostWithAttachmentBody postWithAttachmentBody = (PostWithAttachmentBody) compoundOperationModel.body();
      List<PhotoAttachment> attachments = postWithAttachmentBody.attachments();

      initViewsForGeneralUploadState();

      mainControlImageView.setImageResource(R.drawable.uploading_control_pause);

      if (attachments.size() > 1) {
         statusTextView.setText(getContext().getString(R.string.uploading_post_status_progress_plural, attachments.size()));
      } else {
         statusTextView.setText(getContext().getString(R.string.uploading_post_status_progress_singular));
      }
      statusTextView.setTextColor(getContext().getColor(R.color.uploading_cell_status_label_uploading));

      timeLeftTextView.setVisibility(View.VISIBLE);
      timeLeftTextView.setText(timeLeftFormatter.format(compoundOperationModel.millisLeft()));

      setProgressBarProgressColor(R.color.uploading_cell_progress_bar_active_current,
            R.color.uploading_cell_progress_bar_active_total);
   }

   private void updateAccordingToPausedState() {
      initViewsForGeneralUploadState();

      mainControlImageView.setImageResource(R.drawable.uploading_control_resume);

      statusTextView.setText(getContext().getString(R.string.uploading_post_status_paused));
      statusTextView.setTextColor(getContext().getColor(R.color.uploading_cell_status_label_paused));
      timeLeftTextView.setVisibility(View.GONE);

      setProgressBarProgressColor(R.color.uploading_cell_progress_bar_paused_current,
            R.color.uploading_cell_progress_bar_paused_total);
   }

   private void updateAccordingToFailedState() {
      initViewsForGeneralUploadState();

      mainControlImageView.setImageResource(R.drawable.uploading_control_retry);

      statusTextView.setText(getContext().getString(R.string.uploading_post_status_failed_connection));
      statusTextView.setTextColor(getContext().getColor(R.color.uploading_cell_status_label_failure));
      timeLeftTextView.setVisibility(View.GONE);

      setProgressBarProgressColor(R.color.uploading_cell_progress_bar_paused_current,
            R.color.uploading_cell_progress_bar_paused_total);
   }

   private void initViewsForGeneralUploadState() {
      generalUploadContainer.setVisibility(View.VISIBLE);
      generalUploadContainer.setAlpha(1f);
      uploadFinishedView.setVisibility(View.GONE);
   }

   private void updateAccordingToFinishedState(CompoundOperationModel compoundOperationModel) {
      updateAccordingToStartedState(compoundOperationModel);
      generalUploadContainer.setAlpha(0.1f);
      // show finished view as overlay
      uploadFinishedView.setVisibility(View.VISIBLE);
   }

   private void setProgressBarProgressColor(@ColorRes int current, @ColorRes  int total) {
      progressBar.getProgressDrawable().setColorFilter(getContext().getColor(current), PorterDuff.Mode.SRC_IN);
      progressBar.setBackgroundColor(getContext().getColor(total));
   }

   @OnClick(R.id.uploading_cell_control_cancel)
   void onCancelClicked() {
      cellDelegate.onUploadCancelClicked(compoundOperationModel);
   }

   @OnClick(R.id.uploading_cell_control_main_action)
   void onMainControlActionClicked() {
      switch (compoundOperationModel.state()) {
         case STARTED:
         case SCHEDULED:
            cellDelegate.onUploadPauseClicked(compoundOperationModel);
            break;
         case PAUSED:
            cellDelegate.onUploadResumeClicked(compoundOperationModel);
            break;
         case FAILED:
            cellDelegate.onUploadRetryClicked(compoundOperationModel);
            break;
      }
   }

   public void setTimeLeftFormatter(UploadingTimeLeftFormatter timeLeftFormatter) {
      this.timeLeftFormatter = timeLeftFormatter;
   }
}
