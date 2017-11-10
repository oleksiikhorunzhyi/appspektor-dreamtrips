package com.worldventures.dreamtrips.modules.dtl_flow.parts.comment.fragments;

import android.content.Context;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.worldventures.core.model.User;
import com.worldventures.core.model.session.SessionHolder;
import com.worldventures.core.ui.annotations.Layout;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.api.dtl.merchants.requrest.ImmutableRequestReviewParams;
import com.worldventures.dreamtrips.core.navigation.Route;
import com.worldventures.dreamtrips.core.navigation.router.NavigationConfigBuilder;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.Merchant;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.reviews.Errors;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.reviews.InnerErrors;
import com.worldventures.dreamtrips.modules.dtl.service.MerchantsInteractor;
import com.worldventures.dreamtrips.modules.dtl.service.action.AddReviewAction;
import com.worldventures.dreamtrips.modules.dtl_flow.parts.comment.bundle.CreateReviewEntityBundle;
import com.worldventures.dreamtrips.modules.dtl_flow.parts.details.DtlMerchantDetailsPath;
import com.worldventures.dreamtrips.modules.dtl_flow.parts.reviews.DtlReviewsPath;
import com.worldventures.dreamtrips.modules.dtl_flow.parts.reviews.storage.ReviewStorage;
import com.worldventures.dreamtrips.modules.dtl_flow.parts.utils.NetworkUtils;
import com.worldventures.dreamtrips.modules.media_picker.bundle.PickerBundle;

import java.util.List;

import javax.inject.Inject;

import butterknife.InjectView;
import butterknife.OnClick;
import cn.pedant.SweetAlert.SweetAlertDialog;
import flow.Flow;
import flow.History;
import flow.path.Path;
import io.techery.janet.ActionPipe;
import io.techery.janet.helper.ActionStateSubscriber;
import rx.android.schedulers.AndroidSchedulers;

import static com.iovation.mobile.android.DevicePrint.getBlackbox;

@Layout(R.layout.layout_review_post)
public class CreateReviewPostFragment extends CreateReviewEntityFragment implements DtlFragmentReviewScreen {

   private static final String ERROR_FORM_PROFANITY = "ERROR_FORM_PROFANITY";
   private static final String ERROR_UNKNOWN = "ERROR_UNKNOWN";
   private static final String ERROR_REQUEST_LIMIT_REACHED = "ERROR_REQUEST_LIMIT_REACHED";
   private static final String ERROR_DUPLICATED_REVIEW = "DuplicatedReview";

   @InjectView(R.id.rbRating) RatingBar mRatingBar;
   @InjectView(R.id.etCommentReview) EditText mComment;
   @InjectView(R.id.tv_char_counter) TextView mCharCounter;
   @InjectView(R.id.tv_min_chars) TextView mMinChars;
   @InjectView(R.id.tv_max_chars) TextView mMaxChars;
   @InjectView(R.id.progress_loader) ProgressBar mProgressBar;
   @InjectView(R.id.post_container) RelativeLayout mContainer;
   @InjectView(R.id.photos) RecyclerView mRecycler;

   @Inject MerchantsInteractor merchantInteractor;
   @Inject SessionHolder appSessionHolder;

   private static final String BRAND_ID = "1";
   private User user;
   private int stringReviewLength = 0;
   private Merchant merchant;
   private boolean mAvailableToPost = true;

   private SweetAlertDialog errorDialog;

   private CreateReviewEntityBundle bundle;

   public CreateReviewPostFragment() {
   }

   public static CreateReviewPostFragment newInstance(Bundle arguments) {
      CreateReviewPostFragment f = new CreateReviewPostFragment();
      if (arguments != null) {
         f.setArguments(arguments);
      }
      return f;
   }

   @Override
   public void afterCreateView(View rootView) {
      super.afterCreateView(rootView);

      Bundle args = getArguments();
      bundle = args.getParcelable("data");
      merchant = bundle.getMerchant();
      initEditTextListener();
      initLengthText();
      setMaxLengthText(maximumCharactersAllowed());
   }

   @Override
   protected Route getRoute() {
      return Route.POST_CREATE;
   }

   protected void showMediaPicker() {
      PickerBundle pickerBundle = new PickerBundle.Builder()
            .setRequestId(0)
            .setPhotoPickLimit(getPresenter().getRemainingPhotosCount())
            .build();

      router.moveTo(Route.MEDIA_PICKER, NavigationConfigBuilder.forFragment()
            .backStackEnabled(false)
            .fragmentManager(getChildFragmentManager())
            .containerId(R.id.picker_container)
            .data(pickerBundle)
            .build());
   }

   @OnClick(R.id.container_add_photos_and_videos)
   void onImage() {
      if (isAvailableToPost()) {
         if (getPresenter().getRemainingPhotosCount() > 0) {
            showMediaPicker();
         }
      }
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
            handleStringReview(s.toString());
         }
      });
   }

   private void initLengthText() {
      mMinChars.setText(String.format(getActivity().getResources()
            .getString(R.string.review_min_char), minimumCharactersAllowed()));
      mMaxChars.setText(String.format(getActivity().getResources()
            .getString(R.string.review_max_char), maximumCharactersAllowed()));
   }

   private int maximumCharactersAllowed() {
      return bundle.getMaxCharactersAllow();
   }

   private int minimumCharactersAllowed() {
      return bundle.getMinCharactersAllow();
   }

   @Override
   public void setInputChars(int charCounter) {
      mCharCounter.setText(String.valueOf(charCounter));
   }

   @Override
   public void setMaxLengthText(int maxValue) {
      mComment.setFilters(new InputFilter[]{new InputFilter.LengthFilter(maxValue)});
   }

   @Override
   public void setBoldStyleText() {
      mCharCounter.setTypeface(null, Typeface.BOLD);
   }

   @Override
   public void setNormalStyleText() {
      mCharCounter.setTypeface(null, Typeface.NORMAL);
   }

   public void handleStringReview(String stringReview) {
      if (stringReview == null)  return;
      // Character \n should not be part of the counting at any place. (beginning, middle or end)
      stringReviewLength = stringReview.replaceAll("\n", " ").trim().length();

      setInputChars(stringReviewLength);

      if (stringReviewLength >= minimumCharactersAllowed()) {
         setNormalStyleText();
      } else {
         setBoldStyleText();
      }

      if (stringReviewLength >= maximumCharactersAllowed()) {
         showErrorMaxMessage();
      }
   }

   public String getFingerprintId() {
      return getBlackbox(getContext().getApplicationContext());
   }

   @Override
   public int getSizeComment() {
      if (mComment == null) throw new IllegalStateException("Comment must exist.");
      String review = mComment.getText().toString();
      int lineJumpOccurrences = 0;
      for (int i = 0; i < review.length(); i++) {
         if (review.charAt(i) == '\n') {
            lineJumpOccurrences++;
         }
      }
      return review.trim().length() - lineJumpOccurrences;
   }

   @Override
   public int getRatingBar() {
      return mRatingBar != null ? (int) mRatingBar.getRating() : -1;
   }

   @Override
   public boolean isMinimumCharacterWrote() {
      return getSizeComment() >= minimumCharactersAllowed();
   }

   @Override
   public boolean isMaximumCharacterWrote() {
      return getSizeComment() <= maximumCharactersAllowed();
   }

   @Override
   public void finish() {
      navigateToDetail("");
   }

   @Override
   public void showSnackbarMessage(String message) {
      Snackbar.make(mContainer, message, Snackbar.LENGTH_LONG).show();
   }

   @Override
   public void showDialogMessage(String message) {
      getActivity().runOnUiThread(() -> {
         errorDialog = new SweetAlertDialog(getActivity(),
               SweetAlertDialog.WARNING_TYPE);
         errorDialog.setTitleText(getActivity().getString(R.string.app_name));
         errorDialog.setContentText(message);
         errorDialog.setConfirmText(getActivity().getString(R.string.apptentive_yes));
         errorDialog.showCancelButton(true);
         errorDialog.setCancelText(getActivity().getString(R.string.apptentive_no));
         errorDialog.setConfirmClickListener(listener -> {
            listener.dismissWithAnimation();
            navigateToDetail("");
         });
         errorDialog.show();
      });
   }

   @Override
   public void showNoInternetMessage() {
      getActivity().runOnUiThread(() -> {
         errorDialog = new SweetAlertDialog(getActivity(),
               SweetAlertDialog.ERROR_TYPE);
         errorDialog.setTitleText(getTextResource(R.string.comment_review_title_sorry));
         errorDialog.setContentText(getTextResource(R.string.comment_review_no_internet_message));
         errorDialog.setConfirmText(getTextResource(R.string.comment_review_no_internet_confirm_text));
         errorDialog.showCancelButton(true);
         errorDialog.setCancelText(getTextResource(R.string.comment_review_no_internet_cancel_text));
         errorDialog.setConfirmClickListener(listener -> {
            listener.dismissWithAnimation();
            sendPostReview();
         });
         errorDialog.show();
      });
   }

   private String getTextResource(int idRes) {
      return getContext().getString(idRes);
   }

   @Override
   public void showProfanityError() {
      getActivity().runOnUiThread(() -> {
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
      });
   }

   @Override
   public void showErrorMaxMessage() {
      Snackbar.make(mContainer, R.string.input_major_limit, Snackbar.LENGTH_LONG).show();
   }

   @Override
   public void showErrorUnknown() {
      getActivity().runOnUiThread(() -> {
         errorDialog = new SweetAlertDialog(getActivity(),
               SweetAlertDialog.ERROR_TYPE);
         errorDialog.setTitleText(getTextResource(R.string.comment_review_error_unknown_title));
         errorDialog.setContentText(getTextResource(R.string.comment_review_error_unknown_text));
         errorDialog.setConfirmText(getTextResource(R.string.comment_review_error_unknown_confirm));
         errorDialog.showCancelButton(true);
         errorDialog.setCancelText(getTextResource(R.string.comment_review_error_unknown_cancel));
         errorDialog.setConfirmClickListener(listener -> {
            listener.dismissWithAnimation();
            sendPostReview();
         });
         errorDialog.show();
      });
   }

   @Override
   public void showErrorLimitReached() {
      getActivity().runOnUiThread(() -> {
         errorDialog = new SweetAlertDialog(getActivity(),
               SweetAlertDialog.ERROR_TYPE);
         errorDialog.setTitleText(getActivity().getString(R.string.app_name));
         errorDialog.setContentText(getTextResource(R.string.comment_review_error_limited_reached_content));
         errorDialog.setConfirmText(getTextResource(R.string.comment_review_error_limited_reached_confirm));
         errorDialog.showCancelButton(false);
         errorDialog.setConfirmClickListener(listener -> {
            listener.dismissWithAnimation();
         });
         errorDialog.show();
      });
   }

   private void showReviewDuplicatedError() {
      errorDialog = new SweetAlertDialog(getActivity(),
            SweetAlertDialog.ERROR_TYPE);
      errorDialog.setTitleText(getActivity().getString(R.string.app_name));
      errorDialog.setContentText(getTextResource(R.string.comment_review_duplicated_review));
      errorDialog.setConfirmText(getTextResource(R.string.comment_review_confirm_text));
      errorDialog.showCancelButton(false);
      errorDialog.setConfirmClickListener(listener -> {
         listener.dismissWithAnimation();
      });
      errorDialog.show();
   }

   @Override
   public void unrecognizedError() {
      getActivity().runOnUiThread(() -> {
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
      });
   }

   public void enableInputs() {
      enableButtons(true);
      enableButton();
      enablePost();
      enableDisableViewGroup(mContainer, true);
   }

   @Override
   public void disableInputs() {
      enableButtons(false);
      disableButton();
      disablePost();
      enableDisableViewGroup(mContainer, false);
   }

   public static void enableDisableViewGroup(ViewGroup viewGroup, boolean enabled) {
      int childCount = viewGroup.getChildCount();
      for (int i = 0; i < childCount; i++) {
         View view = viewGroup.getChildAt(i);
         view.setEnabled(enabled);
         if (view instanceof ViewGroup) {
            enableDisableViewGroup((ViewGroup) view, enabled);
         }
      }
   }

   @Override
   public void onBackClick() {

   }

   public void enableButtons(boolean status) {
      getActivity().runOnUiThread(() -> {
         mComment.setEnabled(status);
         mRatingBar.setEnabled(status);
         mRecycler.setEnabled(status);
         mRecycler.setClickable(!status);
         mRecycler.setNestedScrollingEnabled(status);
      });
   }

   @Override
   public void onRefreshSuccess() {
      this.refreshProgress(false);
      this.showEmpty(false);
   }

   private void refreshProgress(boolean status) {
      if (status) {
         changeProgressStatus(View.VISIBLE);
      } else {
         changeProgressStatus(View.GONE);
      }
   }

   private void changeProgressStatus(int change) {
      getActivity().runOnUiThread(() -> mProgressBar.setVisibility(change));
   }

   @Override
   public void onRefreshProgress() {
      this.mProgressBar.setVisibility(View.VISIBLE);
      this.refreshProgress(true);
      this.showEmpty(false);
   }

   @Override
   public void onRefreshError(String error) {
      this.showEmpty(false);
   }

   @Override
   public void showEmpty(boolean isShow) {
   }

   @Override
   public void sendPostReview() {
      this.user = appSessionHolder.get().get().getUser();

      ActionPipe<AddReviewAction> addReviewActionActionPipe = merchantInteractor.addReviewsHttpPipe();
      addReviewActionActionPipe.createObservable(
            AddReviewAction.create(
                  ImmutableRequestReviewParams.builder()
                        .brandId(BRAND_ID)
                        .productId(getMerchantId())
                        .build(),
                  getDescription(),
                  String.valueOf(getRatingBar()),
                  isVerified(),
                  getFingerprintId(),
                  getIpAddress(), getPresenter().getSelectedImagesList()))
            .compose(bindUntilDropViewComposer())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(new ActionStateSubscriber<AddReviewAction>()
                  .onSuccess(this::onReviewPostingSuccess)
                  .onFail(this::onReviewPostingError));
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

   @Override
   public boolean isFromListReview() {
      return bundle.isFromAddReview();
   }

   @Override
   public boolean isVerified() {
      return bundle.isVerified();
   }

   public String getIpAddress() {
      return NetworkUtils.getIpAddress(true);
   }

   private void onReviewPostingSuccess(AddReviewAction action) {
      if (action.getResult().errors() != null) {
         try {
            validateCodeMessage(getReviewPostingErrorCode(action.getResult().errors()));
         } catch (Exception e) {
            showErrorUnknown();
            e.printStackTrace();
         }
      } else {
         this.user = appSessionHolder.get().get().getUser();
         try {
            ReviewStorage.saveReviewsPosted(getActivity(), String.valueOf(user.getId()), getMerchantId());
         } catch (Exception e) {
            e.printStackTrace();
         }

         handlePostNavigation();
      }
      onRefreshSuccess();
      enableInputs();
   }

   private @Nullable String getReviewPostingErrorCode(List<Errors> errors) {
      String errorCode = parseErrorCode(errors);
      if (errorCode == null) errorCode = parseErrorCodeFallback(errors);
      return errorCode;
   }

   private @Nullable String parseErrorCode(List<Errors> errors) {
      if (errors != null && !errors.isEmpty()) {
         Errors error = errors.get(0);
         if (error.innerError() != null && !error.innerError().isEmpty()) {
            InnerErrors innerErrors = error.innerError().get(0);
            return innerErrors.code();
         }
      }
      return null;
   }

   /**
    * This method should probably be deleted after clarifying API contract
    */
   private @Nullable String parseErrorCodeFallback(List<Errors> errors) {
      try {
         return errors.get(0).innerError().get(0).formErrors().fieldErrors().reviewText().code();
      } catch (Exception e) {
         return null;
      }
   }

   private void onReviewPostingError(AddReviewAction action, Throwable throwable) {
      onRefreshSuccess();
      onRefreshError(throwable.getMessage());
      enableInputs();
   }

   private void handlePostNavigation() {
      if (merchant.reviews().total().equals("") || merchant.reviews().total().equals("0")) {
         navigateToDetail(getContext().getString(R.string.snack_review_success));
      } else {
         Path path = new DtlReviewsPath(Flow.get(getContext()).getHistory().top(), merchant, getContext().getString(R.string.snack_review_success));
         History.Builder historyBuilder = Flow.get(getContext()).getHistory().buildUpon();
         historyBuilder.pop();
         historyBuilder.pop();
         historyBuilder.push(path);
         Flow.get(getContext()).setHistory(historyBuilder.build(), Flow.Direction.FORWARD);
      }

   }

   @Override
   public void validateCodeMessage(String message) {
      if (message == null) {
         unrecognizedError();
         return;
      }
      switch (message) {
         case ERROR_FORM_PROFANITY:
            showProfanityError();
            break;
         case ERROR_UNKNOWN:
            showErrorUnknown();
            break;
         case ERROR_REQUEST_LIMIT_REACHED:
            showErrorLimitReached();
            break;
         case ERROR_DUPLICATED_REVIEW:
            showReviewDuplicatedError();
            break;
         default:
            unrecognizedError();
            break;
      }
   }

   @Override
   public void onPostClick() {
      if (isAvailableToPost()) {
         if (isInternetConnection()) {
            if (validateComment()) {
               disableInputs();
               onRefreshProgress();
               sendPostReview();
            }
         } else {
            showNoInternetMessage();
         }
      }
   }

   @Override
   public boolean isAvailableToPost() {
      return mAvailableToPost;
   }

   @Override
   public void disablePost() {
      mAvailableToPost = false;
   }


   @Override
   public void enablePost() {
      mAvailableToPost = true;
   }

   public String getDescription() {
      return mComment.getText().toString().trim();
   }

   public String getMerchantId() {
      return bundle.getMerchantId();
   }

   public boolean isInternetConnection() {
      boolean isInternet = false;
      try {
         ConnectivityManager cm = (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
         NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
         isInternet = activeNetwork.isConnectedOrConnecting();
      } catch (Exception e) {
         e.printStackTrace();
      }
      return isInternet;
   }

   public boolean validateComment() {
      boolean validated = false;
      if (isMinimumCharacterWrote()) {
         if (isMaximumCharacterWrote()) {
            if (getRatingBar() > 0) {
               validated = true;
            }
         } else {
            showSnackbarMessage(String.format(getContext().getString(R.string.review_comment_major_letter), maximumCharactersAllowed()));
         }
      } else if (getSizeComment() >= 0 && getSizeComment() < minimumCharactersAllowed()) {
         showSnackbarMessage(String.format(getContext().getString(R.string.review_comment_minor_letter), minimumCharactersAllowed()));
      }
      return validated;
   }

   public void navigateToDetail(String message) {
      Path path = new DtlMerchantDetailsPath(Flow.get(getContext()).getHistory().top(), merchant, null, message);
      History.Builder historyBuilder = Flow.get(getContext()).getHistory().buildUpon();
      historyBuilder.pop();
      //delete one more element if is present, if not it deletes the merchant list
      if (Flow.get(getContext()).getHistory().size() > 2) {
         historyBuilder.pop();
      }
      historyBuilder.push(path);
      Flow.get(getContext()).setHistory(historyBuilder.build(), Flow.Direction.BACKWARD);
   }

}