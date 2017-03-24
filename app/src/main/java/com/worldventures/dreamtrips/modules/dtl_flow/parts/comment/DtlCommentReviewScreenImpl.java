package com.worldventures.dreamtrips.modules.dtl_flow.parts.comment;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.StringRes;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.RatingBar;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.api.error.ErrorResponse;
import com.worldventures.dreamtrips.modules.dtl_flow.DtlLayout;

import butterknife.InjectView;
import butterknife.OnClick;
import cn.pedant.SweetAlert.SweetAlertDialog;
import static com.iovation.mobile.android.DevicePrint.getBlackbox;

public class DtlCommentReviewScreenImpl extends DtlLayout<DtlCommentReviewScreen, DtlCommentReviewsPresenter, DtlCommentReviewPath>
        implements DtlCommentReviewScreen {

    @InjectView(R.id.toolbar_actionbar)
    Toolbar toolbar;
    @InjectView(R.id.swipe_container)
    SwipeRefreshLayout refreshLayout;
    @InjectView(R.id.emptyView)
    View emptyView;
    @InjectView(R.id.errorView)
    View errorView;

    @InjectView(R.id.rbRating)
    RatingBar mRatingBar;
    @InjectView(R.id.etCommentReview)
    EditText mComment;

    private SweetAlertDialog errorDialog;

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
    public void sendPostReview() {
        getPresenter().sendAddReview(mComment.getText().toString(), (int) mRatingBar.getRating());
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
        return getSizeComment() >= getPresenter().minimumCharactersAllowed();
    }

    @Override
    public boolean isMaximumCharacterWrote() {
        return getSizeComment() <= getPresenter().maximumCharactersAllowed();
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
            getActivity().onBackPressed();
        });
        errorDialog.show();
    }

    @Override
    public void showNoInternetMessage(){
        errorDialog = new SweetAlertDialog(getActivity(),
              SweetAlertDialog.ERROR_TYPE);
        errorDialog.setTitleText("We're sorry");
        errorDialog.setContentText("There was a problem communicating with the DreamTrips servers. Please try again.");
        errorDialog.setConfirmText("Try again");
        errorDialog.showCancelButton(true);
        errorDialog.setCancelText("Cancel");
        errorDialog.setConfirmClickListener(listener -> {
            listener.dismissWithAnimation();
            getPresenter().onPostClick();
        });
        errorDialog.show();
    }

    @Override
    public void showProfanityError() {
        errorDialog = new SweetAlertDialog(getActivity(),
              SweetAlertDialog.NORMAL_TYPE);
        errorDialog.setContentText("Your review contains profanity and could not be submitted. Please edit your review and try again.");
        errorDialog.setConfirmText("OK");
        errorDialog.showCancelButton(false);
        errorDialog.setConfirmClickListener(listener -> {
            listener.dismissWithAnimation();
        });
        errorDialog.show();
    }

    @Override
    public void showErrorUnknown() {
        errorDialog = new SweetAlertDialog(getActivity(),
              SweetAlertDialog.ERROR_TYPE);
        errorDialog.setTitleText("We're sorry");
        errorDialog.setContentText("There was a problem communicating with the DreamTrips servers. Please try again.");
        errorDialog.setConfirmText("Try again");
        errorDialog.showCancelButton(true);
        errorDialog.setCancelText("Cancel");
        errorDialog.setConfirmClickListener(listener -> {
            listener.dismissWithAnimation();
            getPresenter().onPostClick();
        });
        errorDialog.show();
    }

    @Override
    public void showErrorLimitReached() {
        errorDialog = new SweetAlertDialog(getActivity(),
              SweetAlertDialog. NORMAL_TYPE);
        errorDialog.setTitleText("All our server are busy right now");
        errorDialog.setContentText("Please try again in a minute");
        errorDialog.setConfirmText("OK");
        errorDialog.showCancelButton(false);
        errorDialog.setConfirmClickListener(listener -> {
            listener.dismissWithAnimation();
        });
        errorDialog.show();
    }

    @Override
    public void unrecognizedError() {
        errorDialog = new SweetAlertDialog(getActivity(),
              SweetAlertDialog.NORMAL_TYPE);
        //errorDialog.setTitleText("");
        errorDialog.setContentText("Oops, we broke something. We're working on fixing it. Please try again tomorrow.");
        errorDialog.setConfirmText("oK");
        errorDialog.showCancelButton(false);
        errorDialog.setConfirmClickListener(listener -> {
            listener.dismissWithAnimation();
        });
        errorDialog.show();
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
        getPresenter().onPostClick();
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

    @Override
    public boolean isFromListReview(){
        return getPath().isFromAddReview();
    }

    @Override
    public boolean isVerified() {
        return getPath().isVerified();
    }

    @Override
    public String getFingerprintId() {
        return getBlackbox(getContext().getApplicationContext());
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)  {
        if (keyCode == KeyEvent.KEYCODE_BACK
              && event.getRepeatCount() == 0) {
            getPresenter().onBackPressed();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}