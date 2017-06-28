package com.worldventures.dreamtrips.modules.feed.view.cell.uploading;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.PorterDuff;
import android.support.annotation.ColorRes;
import android.support.v4.content.ContextCompat;
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
import com.worldventures.dreamtrips.modules.background_uploading.model.CompoundOperationState;
import com.worldventures.dreamtrips.modules.background_uploading.model.PhotoAttachment;
import com.worldventures.dreamtrips.modules.background_uploading.model.PostCompoundOperationModel;
import com.worldventures.dreamtrips.modules.background_uploading.model.PostWithPhotoAttachmentBody;
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

   private static final int ANIMATION_DURATION_DELAY = 2000;
   private static final int ANIMATION_DURATION_FADE_OUT = 500;
   private static final int ANIMATION_DURATION_SLIDE_UP = 500;

   @InjectView(R.id.uploading_cell_general_upload_container) ViewGroup generalUploadContainer;
   @InjectView(R.id.uploading_cell_upload_finished_container) View uploadFinishedView;
   @InjectView(R.id.uploading_cell_attachment_container) ViewGroup previewContainer;
   @InjectView(R.id.uploading_cell_upload_title_text_view) TextView titleTextView;
   @InjectView(R.id.uploading_cell_upload_status_text_view) TextView statusTextView;
   @InjectView(R.id.uploading_cell_upload_time_left_text_view) TextView timeLeftTextView;
   @InjectView(R.id.uploading_cell_control_main_action) ImageView mainControlImageView;
   @InjectView(R.id.uploading_cell_progress_bar) ProgressBar progressBar;
   private PhotoAttachmentPreviewView photoPreviewView;

   private UploadingPhotoPostsSectionCell.Delegate cellDelegate;
   private UploadingTimeLeftFormatter timeLeftFormatter;

   private PostCompoundOperationModel compoundOperationModel;

   private AnimatorSet removeCellAnimationSet;

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

   public void update(PostCompoundOperationModel compoundOperationModel, UploadingPhotoPostsSectionCell.Delegate delegate) {
      this.compoundOperationModel = compoundOperationModel;
      this.cellDelegate = delegate;

      PostWithPhotoAttachmentBody postWithAttachmentBody = (PostWithPhotoAttachmentBody) compoundOperationModel.body();
      List<PhotoAttachment> attachments = postWithAttachmentBody.attachments();

      refreshPhotoPreviewView(compoundOperationModel.state(), attachments);

      titleTextView.setText(DateUtils.formatDateTime(getContext(),
            compoundOperationModel.creationDate()
                  .getTime(), FORMAT_SHOW_DATE | FORMAT_SHOW_YEAR | FORMAT_ABBREV_MONTH));
      progressBar.setProgress(compoundOperationModel.progress());

      updateViewsAccordingToState(compoundOperationModel);
   }

   /*
    * Reuse photo preview view is suitable, otherwise attach new one
    */
   private void refreshPhotoPreviewView(CompoundOperationState state, List<PhotoAttachment> attachments) {
      PhotoAttachmentPreviewView newPhotoPreviewView = PhotoPreviewViewFactory.provideView(getContext(), attachments);
      if (photoPreviewView == null || !photoPreviewView.getClass().equals(newPhotoPreviewView.getClass())) {
         previewContainer.removeAllViews();
         newPhotoPreviewView.attachView(previewContainer);
         photoPreviewView = newPhotoPreviewView;
      }
      photoPreviewView.showPreview(attachments, state == CompoundOperationState.STARTED);
   }

   private void updateViewsAccordingToState(PostCompoundOperationModel compoundOperationModel) {
      if (compoundOperationModel.state() != CompoundOperationState.FINISHED) {
         resetAnimationsForFinishedState();
      }
      switch (compoundOperationModel.state()) {
         case SCHEDULED:
            updateAccordingToScheduledState();
            break;
         case STARTED:
            updateAccordingToStartedState(compoundOperationModel);
            break;
         case FAILED:
            updateAccordingToFailedState();
            break;
         case PAUSED:
            updateAccordingToPausedState();
            break;
         case FINISHED:
            updateAccordingToFinishedState(compoundOperationModel);
            break;
      }
   }

   private void updateAccordingToStartedState(PostCompoundOperationModel compoundOperationModel) {
      PostWithPhotoAttachmentBody postWithAttachmentBody = (PostWithPhotoAttachmentBody) compoundOperationModel.body();
      List<PhotoAttachment> attachments = postWithAttachmentBody.attachments();

      initViewsForGeneralUploadState();

      mainControlImageView.setImageResource(R.drawable.uploading_control_pause);
      mainControlImageView.setVisibility(VISIBLE);

      if (attachments.size() > 1) {
         statusTextView.setText(getContext().getString(R.string.uploading_post_status_progress_plural, attachments.size()));
      } else {
         statusTextView.setText(getContext().getString(R.string.uploading_post_status_progress_singular));
      }
      statusTextView.setTextColor(getColor(R.color.uploading_cell_status_label_uploading));

      timeLeftTextView.setVisibility(View.VISIBLE);
      timeLeftTextView.setText(timeLeftFormatter.format(compoundOperationModel.millisLeft()));

      setProgressBarProgressColor(R.color.uploading_cell_progress_bar_active_current,
            R.color.uploading_cell_progress_bar_active_total);
   }

   private void updateAccordingToPausedState() {
      initViewsForGeneralUploadState();

      mainControlImageView.setImageResource(R.drawable.uploading_control_resume);
      mainControlImageView.setVisibility(VISIBLE);

      statusTextView.setText(getContext().getString(R.string.uploading_post_status_paused));
      statusTextView.setTextColor(getColor(R.color.uploading_cell_status_label_paused));
      timeLeftTextView.setVisibility(View.GONE);

      setProgressBarProgressColor(R.color.uploading_cell_progress_bar_paused_current,
            R.color.uploading_cell_progress_bar_paused_total);
   }

   private void updateAccordingToScheduledState() {
      initViewsForGeneralUploadState();

      mainControlImageView.setVisibility(GONE);

      statusTextView.setText(getContext().getString(R.string.uploading_post_status_waiting));
      statusTextView.setTextColor(getColor(R.color.uploading_cell_status_label_paused));
      timeLeftTextView.setVisibility(View.GONE);

      setProgressBarProgressColor(R.color.uploading_cell_progress_bar_paused_current,
            R.color.uploading_cell_progress_bar_paused_total);
   }

   private void updateAccordingToFailedState() {
      initViewsForGeneralUploadState();

      mainControlImageView.setImageResource(R.drawable.uploading_control_retry);
      mainControlImageView.setVisibility(VISIBLE);

      statusTextView.setText(getContext().getString(R.string.uploading_post_status_failed_connection));
      statusTextView.setTextColor(getColor(R.color.uploading_cell_status_label_failure));
      timeLeftTextView.setVisibility(View.GONE);

      setProgressBarProgressColor(R.color.uploading_cell_progress_bar_paused_current,
            R.color.uploading_cell_progress_bar_paused_total);
   }

   private void initViewsForGeneralUploadState() {
      generalUploadContainer.setVisibility(View.VISIBLE);
      generalUploadContainer.setAlpha(1f);
      uploadFinishedView.setVisibility(View.GONE);
   }

   private void updateAccordingToFinishedState(PostCompoundOperationModel compoundOperationModel) {
      updateAccordingToStartedState(compoundOperationModel);
      generalUploadContainer.setAlpha(0.1f);
      // show finished view as overlay
      uploadFinishedView.setVisibility(View.VISIBLE);
      if (!isAnimatingUploadFinishedState()) {
         startAnimatingUploadFinishedState();
      }
   }

   private boolean isAnimatingUploadFinishedState() {
      return removeCellAnimationSet != null;
   }

   private void startAnimatingUploadFinishedState() {
      ObjectAnimator fadeOut = ObjectAnimator.ofFloat(this, "alpha", 1f, 0f);
      fadeOut.setDuration(ANIMATION_DURATION_FADE_OUT);
      ValueAnimator slideUp = ValueAnimator.ofInt(0, -getHeight());
      slideUp.addUpdateListener(animation -> {
         ((MarginLayoutParams) getLayoutParams()).topMargin = (int) animation.getAnimatedValue();
         requestLayout();
      });
      slideUp.setDuration(ANIMATION_DURATION_SLIDE_UP);
      removeCellAnimationSet = new AnimatorSet();
      removeCellAnimationSet.setStartDelay(ANIMATION_DURATION_DELAY);
      removeCellAnimationSet.playSequentially(fadeOut, slideUp);
      removeCellAnimationSet.start();
   }

   private void resetAnimationsForFinishedState() {
      if (isAnimatingUploadFinishedState()) {
         removeCellAnimationSet.removeAllListeners();
         removeCellAnimationSet.cancel();
      }
      removeCellAnimationSet = null;
      setAlpha(1f);
      ((MarginLayoutParams) getLayoutParams()).topMargin = 0;
   }

   private void setProgressBarProgressColor(@ColorRes int current, @ColorRes int total) {
      progressBar.getProgressDrawable().setColorFilter(getColor(current), PorterDuff.Mode.SRC_IN);
      progressBar.setBackgroundColor(getColor(total));
   }

   private int getColor(@ColorRes int color) {
      return ContextCompat.getColor(getContext(), color);
   }

   @OnClick(R.id.uploading_cell_control_cancel)
   void onCancelClicked() {
      cellDelegate.onUploadCancelClicked(compoundOperationModel);
   }

   @OnClick(R.id.uploading_cell_control_main_action)
   void onMainControlActionClicked() {
      switch (compoundOperationModel.state()) {
         case SCHEDULED:
            break;
         case STARTED:
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
}
