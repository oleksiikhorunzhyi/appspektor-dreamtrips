package com.worldventures.dreamtrips.modules.dtl_flow.parts.reviews.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.RatingBar;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.dtl.view.dialog.DialogFactory;

import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.pedant.SweetAlert.SweetAlertDialog;

public class MerchantReviewActivity extends AppCompatActivity implements ReviewView {

   CoordinatorLayout containerReview;
   RatingBar mRatingBar;
   EditText mComment;
   private Toolbar mToolbar;
   private SweetAlertDialog errorDialog;

   private static final int MINIMUM_CHARACTER = 140;
   private static final int MAJOR_CHARACTER = 2000;

   public static final int REVIEW_COMMENT = 1;

   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_merchant_review);
      ButterKnife.inject(this);

      containerReview = (CoordinatorLayout) findViewById(R.id.containerReview);
      mRatingBar = (RatingBar) findViewById(R.id.rbRating);
      mComment = (EditText) findViewById(R.id.etCommentReview);
      mToolbar = (Toolbar) findViewById(R.id.toolbar);
      setSupportActionBar(mToolbar);
   }

   @Override
   public boolean onOptionsItemSelected(MenuItem item) {
      int id = item.getItemId();
      if (id == android.R.id.home) {
         onBackClick();
      }
      return false;
   }

   @Override
   public void onPostClick() {
      int commentLength = getSizeComment();
      if (commentLength >= MINIMUM_CHARACTER) {
         if (commentLength <= MAJOR_CHARACTER) {
            Intent returnIntent = new Intent();
            setResult(Activity.RESULT_OK, returnIntent);
            finish();
         } else {
            showSnackbarMessage(getString(R.string.review_comment_major_letter));
         }
      } else {
         if (commentLength != 0) {
            showSnackbarMessage(getString(R.string.review_comment_minor_letter));
         }
      }
   }

   @Override
   public void onBackClick() {
      Log.i("onBackClick", "ratingbar: " + mRatingBar.getRating());
      Log.i("onBackClick", "size comment : " + getSizeComment());
      if (mRatingBar.getRating() > 0 || getSizeComment() > 0) {
         showDialogMessage(getString(R.string.review_comment_discard_changes));
      } else {
         MerchantReviewActivity.this.finish();
      }
   }

   @Override
   public void showSnackbarMessage(String message) {
      Snackbar.make(containerReview, message, Snackbar.LENGTH_SHORT).show();
   }

   @Override
   public void showDialogMessage(String message) {
      errorDialog = new SweetAlertDialog(MerchantReviewActivity.this,
                                    SweetAlertDialog.WARNING_TYPE);
      errorDialog.setTitleText(getString(R.string.app_name));
      errorDialog.setContentText(message);
      errorDialog.setConfirmText(getString(R.string.apptentive_yes));
      errorDialog.showCancelButton(true);
      errorDialog.setCancelText(getString(R.string.apptentive_no));
      errorDialog.setConfirmClickListener(listener -> {
         listener.dismissWithAnimation();
         MerchantReviewActivity.this.finish();
      });
      errorDialog.show();
   }

   private int getSizeComment() {
      return mComment.getText().toString().length();
   }

   private int getRatingSelect() {
      return (int) mRatingBar.getRating();
   }

   @Override
   public void enableInputs() {
      enableButtons(true);
   }

   @Override
   public void disableInputs() {
      enableButtons(false);
   }

   @Override
   public void onCompleteComment() {
      MerchantReviewActivity.this.finish();
   }

   private void enableButtons(boolean status) {
      mComment.setEnabled(status);
      mRatingBar.setEnabled(status);
      mToolbar.setEnabled(status);
   }

   @Override
   public void onBackPressed() {
      onBackClick();
   }

   @OnClick(R.id.tvOnPost)
   public void onClick() {
      onPostClick();
   }

}
