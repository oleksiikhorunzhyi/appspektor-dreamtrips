package com.worldventures.dreamtrips.modules.dtl_flow.parts.comment_review;

import android.content.Context;
import android.support.annotation.StringRes;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.view.View;
import android.widget.EditText;
import android.widget.RatingBar;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.api.error.ErrorResponse;
import com.worldventures.dreamtrips.modules.dtl_flow.DtlLayout;

import butterknife.InjectView;
import butterknife.OnClick;
import cn.pedant.SweetAlert.SweetAlertDialog;

/**
 * Created by andres.rubiano on 20/02/2017.
 */

public class DtlCommentReviewScreenImpl extends DtlLayout<DtlCommentReviewScreen, DtlCommentReviewsPresenter, DtlCommentReviewPath>
      implements DtlCommentReviewScreen {

   @InjectView(R.id.toolbar_actionbar) Toolbar toolbar;
   @InjectView(R.id.swipe_container) SwipeRefreshLayout refreshLayout;
   @InjectView(R.id.emptyView) View emptyView;
   @InjectView(R.id.errorView) View errorView;

   @InjectView(R.id.rbRating) RatingBar mRatingBar;
   @InjectView(R.id.etCommentReview) EditText mComment;

   private SweetAlertDialog errorDialog;

   private static final int MINIMUM_CHARACTER = 140;
   private static final int MAJOR_CHARACTER = 2000;

   public static final int REVIEW_COMMENT = 1;

   public DtlCommentReviewScreenImpl(Context context) {
      super(context);
   }

   public DtlCommentReviewScreenImpl(Context context, AttributeSet attrs) {
      super(context, attrs);
   }

   @Override
   public DtlCommentReviewsPresenter createPresenter() {
      return new DtlCommentReviewPresenterImpl(getContext(), injector, getPath().getMerchant());
   }

   @Override
   protected void onPostAttachToWindowView() {
      inflateToolbarMenu(toolbar);
      toolbar.setNavigationIcon(R.drawable.back_icon);
      toolbar.setNavigationOnClickListener(view -> getPresenter().onBackPressed());
      refreshLayout.setColorSchemeResources(R.color.theme_main_darker);
      refreshLayout.setEnabled(true);
   }

   private void refreshProgress(boolean isShow) {
      refreshLayout.setRefreshing(isShow);
   }

   private void hideRefreshMerchantsError() {
      errorView.setVisibility(GONE);
   }

   @Override
   public void onPostClick() {
      getPresenter().validateComment();
   }

   @Override
   public int getSizeComment() {
      return mComment != null ? mComment.getText().toString().length() : -1;
   }

   @Override
   public int getRatingBar() {
      return mRatingBar != null ? (int) mRatingBar.getRating() : -1;
   }

   @Override
   public boolean isMinimumCharacterWrote() {
      return getSizeComment() >= MINIMUM_CHARACTER ? true : false;
   }

   @Override
   public boolean isMaximumCharacterWrote() {
      return getSizeComment() <= MAJOR_CHARACTER ? true : false;
   }

   @Override
   public void finish() {
      getPresenter().navigateToDetail("");
   }

   @Override
   public void showSnackbarMessage(String message) {
      Snackbar.make(refreshLayout, message, Snackbar.LENGTH_SHORT).show();
   }

   @Override
   public void showDialogMessage(String message) {
      errorDialog = new SweetAlertDialog(getActivity(),
            SweetAlertDialog.WARNING_TYPE);
      errorDialog.setTitleText(getActivity().getString(R.string.app_name));
      errorDialog.setContentText(message);
      errorDialog.setConfirmText(getActivity().getString(R.string.apptentive_yes));
      errorDialog.showCancelButton(true);
      errorDialog.setCancelText(getActivity().getString(R.string.apptentive_no));
      errorDialog.setConfirmClickListener(listener -> {
         listener.dismissWithAnimation();
         getPresenter().navigateToDetail("");
      });
      errorDialog.show();
   }

   @Override
   public void enableInputs() {
      enableButtons(true);
   }

   @Override
   public void disableInputs() {
      enableButtons(false);
   }

   private void enableButtons(boolean status) {
      mComment.setEnabled(status);
      mRatingBar.setEnabled(status);
      toolbar.setEnabled(status);
   }

   @Override
   public void onRefreshSuccess() {
      this.refreshProgress(false);
      this.hideRefreshMerchantsError();
      this.showEmpty(false);
   }

   @Override
   public void onRefreshProgress() {
      this.refreshProgress(true);
      this.hideRefreshMerchantsError();
      this.showEmpty(false);
   }

   @Override
   public void onRefreshError(String error) {
      this.refreshProgress(false);
      this.showEmpty(false);
   }

   @OnClick(R.id.tvOnPost)
   public void onClick() {
      onPostClick();
   }

   @Override
   public void showEmpty(boolean isShow) {
      emptyView.setVisibility(isShow ? VISIBLE : GONE);
   }

   @Override
   public boolean isTabletLandscape() {
      return false;
   }

   @Override
   public boolean onApiError(ErrorResponse errorResponse) {
      return false;
   }

   @Override
   public void onApiCallFailed() {

   }

   @Override
   public void informUser(@StringRes int stringId) {

   }

   @Override
   public void informUser(String message) {

   }

   @Override
   public void showBlockingProgress() {

   }

   @Override
   public void hideBlockingProgress() {

   }
}