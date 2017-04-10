package com.worldventures.dreamtrips.modules.feed.view.fragment;

import android.graphics.Typeface;
import android.support.annotation.StringRes;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;

import com.techery.spares.annotations.Layout;
import com.techery.spares.session.SessionHolder;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.api.dtl.merchants.requrest.ImmutableRequestReviewParams;
import com.worldventures.dreamtrips.core.api.error.ErrorResponse;
import com.worldventures.dreamtrips.core.navigation.Route;
import com.worldventures.dreamtrips.core.navigation.router.NavigationConfigBuilder;
import com.worldventures.dreamtrips.core.session.UserSession;
import com.worldventures.dreamtrips.modules.common.model.User;
import com.worldventures.dreamtrips.modules.common.view.bundle.PickerBundle;
import com.worldventures.dreamtrips.modules.dtl.service.MerchantsInteractor;
import com.worldventures.dreamtrips.modules.dtl.service.action.AddReviewAction;
import com.worldventures.dreamtrips.modules.dtl_flow.parts.comment.DtlCommentReviewScreen;
import com.worldventures.dreamtrips.modules.dtl_flow.parts.reviews.DtlReviewsPath;
import com.worldventures.dreamtrips.modules.dtl_flow.parts.reviews.storage.ReviewStorage;
import com.worldventures.dreamtrips.modules.trips.model.Schedule;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
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

import static com.iovation.mobile.android.DevicePrint.getBlackbox;

@Layout(R.layout.layout_review_post)
public class CreateReviewPostFragment extends CreateEntityFragment implements DtlCommentReviewScreen {

   //@InjectView(R.id.swipe_container) SwipeRefreshLayout refreshLayout;
   @InjectView(R.id.rbRating) RatingBar mRatingBar;
   @InjectView(R.id.etCommentReview) EditText mComment;
   @InjectView(R.id.tv_char_counter) TextView mCharCounter;
   @InjectView(R.id.tv_min_chars) TextView mMinChars;
   @InjectView(R.id.tv_max_chars) TextView mMaxChars;
   @InjectView(R.id.toolbar_actionbar) Toolbar toolbar;
   @InjectView(R.id.progress_loader) ProgressBar mProgressBar;
   @Inject MerchantsInteractor merchantInteractor;
   @Inject SessionHolder<UserSession> appSessionHolder;
   private TextView tvPostButton;

   private static final String BRAND_ID = "1";
   private User user;
   private String stringReview;
   private int stringReviewLength = 0;
   private boolean isStringReviewValid = false;
   private static final String ERROR_FORM_PROFANITY = "ERROR_FORM_PROFANITY";
   private static final String ERROR_UNKNOWN = "ERROR_UNKNOWN";
   private static final String ERROR_REQUEST_LIMIT_REACHED = "ERROR_REQUEST_LIMIT_REACHED";

   private SweetAlertDialog errorDialog;

   @Override
   public void afterCreateView(View rootView) {
      super.afterCreateView(rootView);
      toolbar.setNavigationIcon(R.drawable.back_icon);
      /*AppCompatActivity activity = (AppCompatActivity) getActivity();
      activity.setSupportActionBar(toolbar);
      activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);*/
      //toolbar.setNavigationOnClickListener(view -> super.onBackPressed());

      initEditTextListener();
      initLengthText();
      setMaxLengthText(maximumCharactersAllowed());

      tvPostButton = (TextView) toolbar.findViewById(R.id.tvOnPost);

      tvPostButton.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
            onRefreshProgress();
            disableInputs();
            sendPostReview();
         }
      });
   }

   @Override
   protected Route getRoute() {
      return Route.POST_CREATE;
   }

   protected void showMediaPicker() {
      router.moveTo(Route.MEDIA_PICKER, NavigationConfigBuilder.forFragment()
            .backStackEnabled(false)
            .fragmentManager(getChildFragmentManager())
            .containerId(R.id.picker_container)
            .data(new PickerBundle(0, 5))
            .build());
   }

   @OnClick(R.id.container_add_photos_and_videos)
   void onImage() {
      showMediaPicker();
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
      mMinChars.setText(String.format(getActivity().getResources().getString(R.string.review_min_char), minimumCharactersAllowed()));
      mMaxChars.setText(String.format(getActivity().getResources().getString(R.string.review_max_char), maximumCharactersAllowed()));
   }

   private int maximumCharactersAllowed() {
      return 2000;
   }

   private int minimumCharactersAllowed() {
      return 50;
   }

   @Override
   public void setInputChars(int charCounter) {
      mCharCounter.setText(String.valueOf(charCounter));
   }

   @Override
   public void setMaxLengthText(int maxValue){
      mComment.setFilters(new InputFilter[] {new InputFilter.LengthFilter(maxValue)});
   }

   @Override
   public void setBoldStyleText(){
      mCharCounter.setTypeface(null, Typeface.BOLD);
   }

   @Override
   public void setNormalStyleText(){
      mCharCounter.setTypeface(null, Typeface.NORMAL);
   }

   public void handleStringReview(String stringReview) {
      this.stringReview = stringReview;

      int lineJumpOccurrences = 0;
      for (int i = 0; i < stringReview.length(); i++) {
         if (stringReview.charAt(i) == '\n') {
            lineJumpOccurrences++;
         }
      }
      stringReviewLength = stringReview.length() - lineJumpOccurrences;

      setInputChars(stringReviewLength);

      if (stringReviewLength >= minimumCharactersAllowed()) {
         isStringReviewValid = true;
         setNormalStyleText();
      } else {
         setBoldStyleText();
      }
      setMaxLengthText(maximumCharactersAllowed() + lineJumpOccurrences);
      if (stringReviewLength >= maximumCharactersAllowed()){
         showErrorMaxMessage();
      }
   }


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
      return getSizeComment() >= minimumCharactersAllowed();
   }

   @Override
   public boolean isMaximumCharacterWrote() {
      return getSizeComment() <= maximumCharactersAllowed();
   }

   @Override
   public void finish() {
      //navigateToDetail("");
   }

   @Override
   public void showSnackbarMessage(String message) {
      //Snackbar.make(refreshLayout, message, Snackbar.LENGTH_SHORT).show();
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
      errorDialog.setTitleText(getTextResource(R.string.comment_review_title_sorry));
      errorDialog.setContentText(getTextResource(R.string.comment_review_no_internet_message));
      errorDialog.setConfirmText(getTextResource(R.string.comment_review_no_internet_confirm_text));
      errorDialog.showCancelButton(true);
      errorDialog.setCancelText(getTextResource(R.string.comment_review_no_internet_cancel_text));
      errorDialog.setConfirmClickListener(listener -> {
         listener.dismissWithAnimation();
         //onPostClick();
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
   public void showErrorMaxMessage() {

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
         //onPostClick();
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

   public void enableButtons(boolean status) {
      mComment.setEnabled(status);
      mRatingBar.setEnabled(status);
      tvPostButton.setEnabled(status);
   }

   @Override
   public void onRefreshSuccess() {
      this.refreshProgress(false);
      //this.hideRefreshMerchantsError();
      this.showEmpty(false);
   }

   private void refreshProgress(boolean status) {
      if (status) {
         changeProgressStatus(View.VISIBLE);
      } else {
         changeProgressStatus(View.GONE);
      }
   }

   private void changeProgressStatus(int change){
      getActivity().runOnUiThread(new Runnable() {
         @Override
         public void run() {
            mProgressBar.setVisibility(change);
         }
      });
   }

   @Override
   public void onRefreshProgress() {
      this.mProgressBar.setVisibility(View.VISIBLE);
      this.refreshProgress(true);
      //this.hideRefreshMerchantsError();
      this.showEmpty(false);
   }

   @Override
   public void onRefreshError(String error) {
      //this.refreshProgress(false);
      this.showEmpty(false);
   }

   @Override
   public void showEmpty(boolean isShow) {
      //emptyView.setVisibility(isShow ? VISIBLE : GONE);
   }

   @Override
   public void sendPostReview() {
      this.user = appSessionHolder.get().get().getUser();
      ActionPipe<AddReviewAction> addReviewActionActionPipe = merchantInteractor.addReviewsHttpPipe();
      addReviewActionActionPipe
            .observe()
            //.compose(bindViewIoToMainComposer())
            .subscribe(new ActionStateSubscriber<AddReviewAction>()
                  .onSuccess(this::onMerchantsLoaded)
                  .onProgress(this::onMerchantsLoading)
                  .onFail(this::onMerchantsLoadingError));
      addReviewActionActionPipe.send(AddReviewAction.create(
            ImmutableRequestReviewParams.builder()
                                          .brandId(BRAND_ID)
                                          .productId(getMerchantId())
                                          .build(), user.getEmail(),
                                                                    user.getFullName(),
                                                                           getDescription(),
                                                                           String.valueOf(getRatingBar()),
                                                                           isVerified(),
                                                                           String.valueOf(user.getId()),
                                                                           getFingerprintId(),
                                                                           getIpAddress()));
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
      //return getPath().isFromAddReview();
      return false;
   }

   @Override
   public boolean isVerified() {
      //return getPath().isVerified();
      return false;
   }

   public String getIpAddress() {
      return "10.20.20.122";
   }

   private void onMerchantsLoaded(AddReviewAction action) {
      if (action.getResult().errors() != null){
         try {
            validateCodeMessage(action.getResult().errors().get(0).innerError().get(0).formErrors().fieldErrors().reviewText().code());
         } catch (Exception e){
            e.printStackTrace();
         }
      } else {
         this.user = appSessionHolder.get().get().getUser();
         ReviewStorage.saveReviewsPosted(getContext(), String.valueOf(user.getId()), getMerchantId());
         handlePostNavigation();
      }
      onRefreshSuccess();
      enableInputs();
   }

   private void onMerchantsLoading(AddReviewAction action, Integer progress) {
   }

   private void onMerchantsLoadingError(AddReviewAction action, Throwable throwable) {
      onRefreshSuccess();
      onRefreshError(throwable.getMessage());
      enableInputs();
   }

   private void handlePostNavigation(){
      //if (merchant.reviews().total().equals("")){
      //   navigateToDetail(getContext().getString(R.string.snack_review_success));
      //} else {
         //Send merchant TODO
         Path path = new DtlReviewsPath(null, getContext().getString(R.string.snack_review_success));
         History.Builder historyBuilder = Flow.get(getContext()).getHistory().buildUpon();
         historyBuilder.pop();
         if (isFromListReview()){
            historyBuilder.pop();
         }
         historyBuilder.push(path);
         Flow.get(getContext()).setHistory(historyBuilder.build(), Flow.Direction.FORWARD);
      //}
   }

   private void validateCodeMessage(String message){
      switch (message){
         case ERROR_FORM_PROFANITY:
            showProfanityError();
            break;

         case ERROR_UNKNOWN:
            showErrorUnknown();
            break;

         case ERROR_REQUEST_LIMIT_REACHED:
            showErrorLimitReached();
            break;

         default:
            unrecognizedError();
            break;
      }
   }

   public String getDescription() {
      return mComment.getText().toString();
   }

   public String getMerchantId() {
      return "25433dae-73ea-4d99-99a2-00a6d1325df4";
   }

   public List<File> getListImage() {
      List<File> array = new ArrayList<>();
      try {
         File image = File.createTempFile("/data/user/0/com.worldventures.dreamtrips.dev.dtl.preprod.debug/cache/dreamtrips_cache_images/IMG_20170405_100106",
                                          ".jpg");
         array.add(image);
      } catch (IOException e) {
         e.printStackTrace();
      }
      return array;
   }
}
