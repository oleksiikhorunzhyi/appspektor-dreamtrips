package com.worldventures.dreamtrips.modules.dtl_flow.parts.comment;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.StringRes;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.api.error.ErrorResponse;
import com.worldventures.dreamtrips.modules.dtl_flow.DtlLayout;
import com.worldventures.dreamtrips.modules.feed.bundle.CreateEntityBundle;
import com.worldventures.dreamtrips.modules.dtl_flow.parts.comment.fragments.CreateReviewPostFragment;
import com.worldventures.dreamtrips.modules.feed.view.util.FragmentWithFeedDelegate;

import javax.inject.Inject;

import butterknife.InjectView;
import butterknife.OnClick;
import cn.pedant.SweetAlert.SweetAlertDialog;

import static com.iovation.mobile.android.DevicePrint.getBlackbox;

public class DtlCommentReviewScreenImpl extends DtlLayout<DtlCommentReviewScreen, DtlCommentReviewsPresenter, DtlCommentReviewPath>
        implements DtlCommentReviewScreen{

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

    @InjectView(R.id.tv_char_counter)
    TextView mCharCounter;
    @InjectView(R.id.tv_min_chars)
    TextView mMinChars;
    @InjectView(R.id.tv_max_chars)
    TextView mMaxChars;
    @InjectView(R.id.tvOnPost)
    TextView mTvPost;
    @InjectView(R.id.container_details_floating)
    FrameLayout mFlContainerReview;

    @Inject FragmentWithFeedDelegate fragmentWithFeedDelegate;

    private CreateReviewPostFragment fragment;

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
        refreshLayout.setColorSchemeResources(R.color.theme_main_darker);
        refreshLayout.setEnabled(false);

        initEditTextListener();
        initLengthText();
        setMaxLengthText(getPresenter().maximumCharactersAllowed());

       mFlContainerReview.setVisibility(View.VISIBLE);

       Bundle bundle = new Bundle();
       bundle.putParcelable("data", new CreateEntityBundle(true, CreateEntityBundle.Origin.FEED,
             getPresenter().minimumCharactersAllowed(),
             getPresenter().maximumCharactersAllowed(),
             getPath().getMerchant().id(),
             isFromListReview(),
             getPath().getMerchant()));

       fragment = CreateReviewPostFragment.newInstance(bundle);
       FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
       transaction.replace(R.id.container_details_floating, fragment);
       transaction.commit();
       toolbar.setNavigationOnClickListener(view -> onBackPressed());
    }

   @Override
   protected void onDetachedFromWindow() {
      fragment = null;
      super.onDetachedFromWindow();
   }

   private void onBackPressed() {
      if (null != fragment){
         if (fragment.getRatingBar() > 0 || fragment.getSizeComment() > 0) {
            showDialogMessage(getContext().getString(R.string.review_comment_discard_changes));
         } else {
            validateComingFrom("");
         }
      }
   }

   private void validateComingFrom(String message) {
      if (isFromListReview()){
         getPresenter().navigateToListReview(message);
      } else {
         getPresenter().navigateToDetail(message);
      }
   }

   private void initLengthText() {
        mMinChars.setText(String.format(getActivity().getResources().getString(R.string.review_min_char), getPresenter().minimumCharactersAllowed()));
        mMaxChars.setText(String.format(getActivity().getResources().getString(R.string.review_max_char), getPresenter().maximumCharactersAllowed()));
    }

    public void setMaxLengthText(int maxValue){
        mComment.setFilters(new InputFilter[] {new InputFilter.LengthFilter(maxValue)});
    }

    private void initEditTextListener() {
        mComment.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                getPresenter().handleStringReview(s.toString());
            }
        });
    }

    public void showErrorMaxMessage() {
        Snackbar.make(refreshLayout, R.string.input_major_limit, Snackbar.LENGTH_LONG).show();
    }

    public void setBoldStyleText(){
        mCharCounter.setTypeface(null, Typeface.BOLD);
    }

    public void setNormalStyleText(){
        mCharCounter.setTypeface(null, Typeface.NORMAL);
    }

    public void setInputChars(int charCounter){
        mCharCounter.setText(String.valueOf(charCounter));
    }

    private void refreshProgress(boolean isShow) {
        refreshLayout.setRefreshing(isShow);
    }

    private void hideRefreshMerchantsError() {
        errorView.setVisibility(GONE);
    }

    @Override
    public void sendPostReview() {
        getPresenter().sendAddReview(mComment.getText().toString(), (int) mRatingBar.getRating(), false);
    }

    @Override
    public String getFingerprintId() {
        return getBlackbox(getContext().getApplicationContext());
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
            validateComingFrom("");
        });
        errorDialog.show();
    }

    @Override
    public void showNoInternetMessage(){
        errorDialog = new SweetAlertDialog(getActivity(),
              SweetAlertDialog.ERROR_TYPE);
        errorDialog.setTitleText(getTextResource(R.string.comment_review_title_sorry));
        errorDialog.setContentText(getTextResource(R.string.comment_review_no_internet_message));
        errorDialog.setConfirmText(getTextResource(R.string.comment_review_no_internet_confirm_text));
        errorDialog.showCancelButton(true);
        errorDialog.setCancelText(getTextResource(R.string.comment_review_no_internet_cancel_text));
        errorDialog.setConfirmClickListener(listener -> {
            listener.dismissWithAnimation();
            getPresenter().onPostClick();
        });
        errorDialog.show();
    }

   private String getTextResource(int idRes) {
      return getContext().getString(idRes);
   }

   @Override
    public void showProfanityError() {
        errorDialog = new SweetAlertDialog(getActivity(),
              SweetAlertDialog.ERROR_TYPE);
        errorDialog.setTitleText(getActivity().getString(R.string.app_name));
        errorDialog.setContentText(getTextResource(R.string.comment_review_profanity_text));
        errorDialog.setConfirmText(getTextResource(R.string.comment_review_confirm_text));
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
        errorDialog.setTitleText(getTextResource(R.string.comment_review_error_unknown_title));
        errorDialog.setContentText(getTextResource(R.string.comment_review_error_unknown_text));
        errorDialog.setConfirmText(getTextResource(R.string.comment_review_error_unknown_confirm));
        errorDialog.showCancelButton(true);
        errorDialog.setCancelText(getTextResource(R.string.comment_review_error_unknown_cancel));
        errorDialog.setConfirmClickListener(listener -> {
            listener.dismissWithAnimation();
            getPresenter().onPostClick();
        });
        errorDialog.show();
    }

    @Override
    public void showErrorLimitReached() {
        errorDialog = new SweetAlertDialog(getActivity(),
              SweetAlertDialog. ERROR_TYPE);
        errorDialog.setTitleText(getActivity().getString(R.string.app_name));
        errorDialog.setContentText(getTextResource(R.string.comment_review_error_limited_reached_content));
        errorDialog.setConfirmText(getTextResource(R.string.comment_review_error_limited_reached_confirm));
        errorDialog.showCancelButton(false);
        errorDialog.setConfirmClickListener(listener -> {
            listener.dismissWithAnimation();
        });
        errorDialog.show();
    }

    @Override
    public void unrecognizedError() {
        errorDialog = new SweetAlertDialog(getActivity(),
              SweetAlertDialog.ERROR_TYPE);
        errorDialog.setTitleText(getActivity().getString(R.string.app_name));
        errorDialog.setContentText(getTextResource(R.string.comment_review_unrecognized_error_content));
        errorDialog.setConfirmText(getTextResource(R.string.comment_review_unrecognized_error_confirm));
        errorDialog.showCancelButton(false);
        errorDialog.setConfirmClickListener(listener -> {
            listener.dismissWithAnimation();
        });
        errorDialog.show();
    }

   @Override
   public void validateCodeMessage(String message) {

   }

   @Override
   public void onPostClick() {

   }

   public void enableInputs() {
        enableButtons(true);
    }

    @Override
    public void disableInputs() {
        enableButtons(false);
    }

    @Override
    public void onBackClick() {
        getActivity().onBackPressed();
    }

    private void enableButtons(boolean status) {
       toolbar.setEnabled(status);
       mTvPost.setEnabled(status);
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
       fragment.onPostClick();
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
}