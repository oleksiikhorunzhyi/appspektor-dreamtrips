package com.worldventures.dreamtrips.modules.dtl_flow.parts.reviews;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.StringRes;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ProgressBar;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.dtl_flow.DtlLayout;
import com.worldventures.dreamtrips.modules.dtl_flow.parts.reviews.model.ReviewObject;
import com.worldventures.dreamtrips.modules.dtl_flow.parts.reviews.views.OfferWithReviewView;

import java.util.ArrayList;

import butterknife.InjectView;
import cn.pedant.SweetAlert.SweetAlertDialog;
import flow.Flow;

public class DtlReviewsScreenImpl extends DtlLayout<DtlReviewsScreen, DtlReviewsPresenter, DtlReviewsPath>
      implements DtlReviewsScreen {

   @InjectView(R.id.toolbar_actionbar) Toolbar toolbar;
   @InjectView(R.id.container_comments_detail) OfferWithReviewView mContainerDetail;
   @InjectView(R.id.progress_loader) ProgressBar refreshLayout;
   @InjectView(R.id.emptyView) View emptyView;
   @InjectView(R.id.errorView) View errorView;

   SweetAlertDialog errorDialog;

   public DtlReviewsScreenImpl(Context context) {
      super(context);
   }

   public DtlReviewsScreenImpl(Context context, AttributeSet attrs) {
      super(context, attrs);
   }

   @Override
   public DtlReviewsPresenter createPresenter() {
      return new DtlReviewsPresenterImpl(getContext(), injector, getPath().getMerchant());
   }

   @Override
   protected void onPostAttachToWindowView() {
      inflateToolbarMenu(toolbar);
      toolbar.setTitle(getContext().getResources().getString(R.string.reviews_text));
      toolbar.setNavigationIcon(R.drawable.back_icon);
      toolbar.setNavigationOnClickListener(view -> {
         Flow.get(getContext()).goBack();
      });
      showMessage();
   }

   private void showMessage() {
      String message = getPath().getMessage();
      if (message != null && message.length() > 0) {
         Snackbar.make(mContainerDetail, message, Snackbar.LENGTH_LONG).show();
      }
   }

   @Override
   public void addCommentsAndReviews(float ratingMerchant, int countReview, ArrayList<ReviewObject> listReviews) {
      Bundle bundle = new Bundle();
      bundle.putParcelableArrayList(OfferWithReviewView.ARRAY, listReviews);
      bundle.putFloat(OfferWithReviewView.RATING_MERCHANT, ratingMerchant);
      bundle.putInt(OfferWithReviewView.COUNT_REVIEW, countReview);
      bundle.putString(OfferWithReviewView.MERCHANT_NAME, getPath().getMerchant().displayName());
      bundle.putBoolean(OfferWithReviewView.IS_FROM_LIST_REVIEW, true);
      mContainerDetail.addBundle(bundle);
   }

   private void hideRefreshMerchantsError() {
      errorView.setVisibility(GONE);
   }

   @Override
   public void onRefreshSuccess() {
      this.hideRefreshMerchantsError();
      this.showEmpty(false);
      this.refreshLayout.setVisibility(View.GONE);
      this.mContainerDetail.setVisibility(View.VISIBLE);
   }

   @Override
   public void onRefreshProgress() {
      this.refreshLayout.setVisibility(View.VISIBLE);
      this.mContainerDetail.setVisibility(View.GONE);
      this.hideRefreshMerchantsError();
      this.showEmpty(false);
   }

   @Override
   public void onRefreshError(String error) {
      this.refreshLayout.setVisibility(View.GONE);
      this.showEmpty(false);
   }

   @Override
   public void showEmpty(boolean isShow) {
      emptyView.setVisibility(isShow ? VISIBLE : GONE);
   }

   @Override
   public void showFrameLayoutReviews(boolean isShow) {
      mContainerDetail.setVisibility(isShow ? VISIBLE : GONE);
   }

   @Override
   public void userHasPendingReview() {
      errorDialog = new SweetAlertDialog(getActivity(), SweetAlertDialog.NORMAL_TYPE);
      errorDialog.setTitleText(getActivity().getString(R.string.app_name));
      errorDialog.setContentText(getContext().getString(R.string.text_awaiting_approval_review));
      errorDialog.setConfirmText(getActivity().getString(R.string.apptentive_ok));
      errorDialog.showCancelButton(true);
      errorDialog.setConfirmClickListener(listener -> listener.dismissWithAnimation());
      errorDialog.show();
   }

   @Override
   public String getMerchantId() {
      return getPath().getMerchant().id();
   }

   @Override
   public boolean isTabletLandscape() {
      return false;
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
