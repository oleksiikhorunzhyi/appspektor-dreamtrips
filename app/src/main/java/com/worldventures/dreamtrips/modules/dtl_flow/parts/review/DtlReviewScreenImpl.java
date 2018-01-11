package com.worldventures.dreamtrips.modules.dtl_flow.parts.review;

import android.content.Context;
import android.graphics.Typeface;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.InputFilter;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.techery.spares.utils.ui.OrientationUtil;
import com.worldventures.core.modules.picker.model.PhotoPickerModel;
import com.worldventures.core.modules.picker.view.dialog.MediaPickerDialog;
import com.worldventures.core.ui.view.adapter.BaseDelegateAdapter;
import com.worldventures.core.utils.HttpErrorHandlingUtil;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.navigation.BackStackDelegate;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.reviews.Reviews;
import com.worldventures.dreamtrips.modules.dtl.service.action.bundle.ImmutablePostReviewActionParams;
import com.worldventures.dreamtrips.modules.dtl.service.action.bundle.PostReviewActionParams;
import com.worldventures.dreamtrips.modules.dtl.service.action.http.PostReviewHttpCommand;
import com.worldventures.dreamtrips.modules.dtl.service.action.http.error.exception.DuplicatePostException;
import com.worldventures.dreamtrips.modules.dtl.service.action.http.error.exception.ProfanityPostException;
import com.worldventures.dreamtrips.modules.dtl.service.action.http.error.exception.RequestLimitException;
import com.worldventures.dreamtrips.modules.dtl.service.action.http.error.exception.UnknownPostException;
import com.worldventures.dreamtrips.modules.dtl_flow.DtlLayout;
import com.worldventures.dreamtrips.modules.dtl_flow.FlowUtil;
import com.worldventures.dreamtrips.modules.dtl_flow.parts.common.ActionProgressView;
import com.worldventures.dreamtrips.modules.dtl_flow.parts.common.ActionSuccessView;
import com.worldventures.dreamtrips.modules.dtl_flow.parts.common.SimpleSweetDialogErrorView;
import com.worldventures.dreamtrips.modules.dtl_flow.parts.common.SweetDialogErrorViewProvider;
import com.worldventures.dreamtrips.modules.dtl_flow.parts.common.SweetDialogHttpErrorViewProvider;
import com.worldventures.dreamtrips.modules.dtl_flow.parts.common.SweetDialogParams;
import com.worldventures.dreamtrips.modules.dtl_flow.parts.details.DtlMerchantDetailsPath;
import com.worldventures.dreamtrips.modules.dtl_flow.parts.review.adapter.DtlReviewCell;
import com.worldventures.dreamtrips.modules.dtl_flow.parts.review.adapter.ReviewPostItemDecorator;
import com.worldventures.dreamtrips.modules.dtl_flow.parts.review.util.CommentAdapter;
import com.worldventures.dreamtrips.modules.dtl_flow.parts.review.util.CommentParams;
import com.worldventures.dreamtrips.modules.dtl_flow.parts.reviews.DtlReviewsPath;
import com.worldventures.wallet.ui.common.helper2.error.ErrorViewFactory;

import java.util.List;

import javax.inject.Inject;

import butterknife.InjectView;
import butterknife.OnClick;
import cn.pedant.SweetAlert.SweetAlertDialog;
import flow.Flow;
import flow.History;
import flow.path.Path;
import io.techery.janet.operationsubscriber.view.ComposableOperationView;
import io.techery.janet.operationsubscriber.view.OperationView;

public class DtlReviewScreenImpl extends DtlLayout<DtlReviewScreen, DtlReviewPresenter, DtlReviewPath> implements DtlReviewScreen, BackStackDelegate.BackPressedListener {

   @InjectView(R.id.toolbar_actionbar) Toolbar toolbarView;
   @InjectView(R.id.etCommentReview) EditText commentView;
   @InjectView(R.id.tv_char_counter) TextView counterView;
   @InjectView(R.id.tv_min_chars) TextView minCharsView;
   @InjectView(R.id.tv_max_chars) TextView maxCharsView;
   @InjectView(R.id.tvOnPost) TextView postView;
   @InjectView(R.id.rbRating) RatingBar ratingView;
   @InjectView(R.id.progress_loader) ProgressBar progressView;
   @InjectView(R.id.post_container) RelativeLayout postContainer;
   @InjectView(R.id.photos) RecyclerView attachmentListView;

   @Inject HttpErrorHandlingUtil httpErrorHandlingUtil;
   @Inject BackStackDelegate backStackDelegate;

   private CommentParams commentParams;
   private CommentAdapter commentAdapter;
   private BaseDelegateAdapter<PhotoPickerModel> photoAdapter;
   private SweetAlertDialog messageDialog;
   private boolean availablePost = true;

   public DtlReviewScreenImpl(Context context) {
      super(context);
   }

   public DtlReviewScreenImpl(Context context, AttributeSet attrs) {
      super(context, attrs);
   }

   @Override
   public DtlReviewPresenter createPresenter() {
      return new DtlReviewPresenterImpl(getContext(), injector);
   }

   @Override
   protected void onAttachedToWindow() {
      super.onAttachedToWindow();
      OrientationUtil.lockOrientation(getActivity()); // TODO :: discuss about this hack
   }

   @Override
   protected void onPostAttachToWindowView() {
      super.onPostAttachToWindowView();
      toolbarView.setNavigationIcon(R.drawable.back_icon);
      toolbarView.setNavigationOnClickListener(click -> onBack());
      commentParams = CommentParams.from(getPath().getMerchant());
      commentAdapter = new CommentAdapter(commentParams, commentView, this::onCommentChange, this::onOutOfLimit);

      minCharsView.setText(String.format(getResources().getString(R.string.review_min_char), commentParams.minSize()));
      maxCharsView.setText(String.format(getResources().getString(R.string.review_max_char), commentParams.maxSize()));
      commentView.setFilters(new InputFilter[]{new InputFilter.LengthFilter(commentParams.maxSize())});

      photoAdapter = new BaseDelegateAdapter<>(getContext(), injector);
      photoAdapter.registerCell(PhotoPickerModel.class, DtlReviewCell.class, item -> getPresenter().onMediaRemoved(item));

      attachmentListView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
      attachmentListView.addItemDecoration(new ReviewPostItemDecorator());
      attachmentListView.setAdapter(photoAdapter);
      backStackDelegate.addListener(this);
   }

   @Override
   protected void onDetachedFromWindow() {
      backStackDelegate.removeListener(this);
      OrientationUtil.unlockOrientation(getActivity());
      super.onDetachedFromWindow();
   }

   @Override
   public OperationView<PostReviewHttpCommand> provideReviewOperationView() {
      return new ComposableOperationView<>(new ActionProgressView<>(progressView, this::onProgress),
            new ActionSuccessView<>(ignore -> finish()),
            ErrorViewFactory.<PostReviewHttpCommand>builder()
                  .defaultErrorView(new SimpleSweetDialogErrorView<>(getContext(), SweetDialogParams.forUnrecognizedErrorView(getContext())))
                  .addProvider(new SweetDialogErrorViewProvider<>(getContext(), ProfanityPostException.class, SweetDialogParams.forProfanityErrorView(getContext())))
                  .addProvider(new SweetDialogErrorViewProvider<>(getContext(), DuplicatePostException.class, SweetDialogParams.forReviewDuplicatedErrorView(getContext())))
                  .addProvider(new SweetDialogErrorViewProvider<>(getContext(), RequestLimitException.class, SweetDialogParams.forLimitReachedErrorView(getContext())))
                  .addProvider(new SweetDialogErrorViewProvider<>(getContext(), UnknownPostException.class, SweetDialogParams.forUnknownErrorView(getContext(), ignore -> post())))
                  .addProvider(new SweetDialogHttpErrorViewProvider<>(getContext(), httpErrorHandlingUtil, ignore -> post()))
                  .build());
   }

   @Override
   public PostReviewActionParams provideReviewParams() {
      return ImmutablePostReviewActionParams.builder()
            .productId(getPath().getMerchant().id())
            .comment(commentAdapter.commentText())
            .rating(ratingView.getNumStars())
            .verified(getPath().isVerified())
            .build();
   }

   @OnClick(R.id.tv_add_photos_and_videos)
   void onImagesClick() {
      if (availablePost && getPresenter().getRemainingPhotosCount() > 0) {
         showMediaPicker();
      }
   }

   @OnClick(R.id.tvOnPost)
   void post() {
      if (commentAdapter.isCommentValid() && ratingView.getRating() > 0) {
         presenter.post();
      }
   }

   @Override
   public boolean onBackPressed() {
      onBack();
      return availablePost;
   }

   @Override
   public void attachImages(List<PhotoPickerModel> images) {
      photoAdapter.addItems(images);
   }

   @Override
   public void removeImage(PhotoPickerModel image) {
      photoAdapter.remove(image);
   }

   private void onProgress(Boolean showing) {
      toolbarView.post(() -> {
         commentView.setEnabled(!showing);
         ratingView.setEnabled(!showing);
         postView.setEnabled(!showing);
         attachmentListView.setLayoutFrozen(showing);
         availablePost = !showing;
      });
   }

   private void onOutOfLimit() {
      Snackbar.make(postContainer, R.string.input_major_limit, Snackbar.LENGTH_LONG).show();
   }

   private void onCommentChange(Integer size) {
      int typeface = size >= commentParams.minSize() ? Typeface.NORMAL : Typeface.BOLD;
      counterView.setTypeface(null, typeface);
      counterView.setText(String.valueOf(size));
   }

   private void showMediaPicker() {
      MediaPickerDialog mediaPicker = new MediaPickerDialog(getContext());
      mediaPicker.setOnDoneListener(getPresenter()::onMediaAttached);
      mediaPicker.show(getPresenter().getRemainingPhotosCount());
   }

   private boolean isMerchantHaveReviews() {
      Reviews reviews = getPath().getMerchant().reviews();
      int total = reviews != null ? !TextUtils.isEmpty(reviews.total()) ? Integer.parseInt(reviews.total()) : 0 : 0;
      return total > 0;
   }

   private void onBack() {
      if(commentAdapter.isCommentExist() || ratingView.getRating() > 0) {
         confirmBack();
      } else {
         back();
      }
   }

   private void finish() {
      String message = getContext().getString(R.string.snack_review_success);
      if (isMerchantHaveReviews()) {
         navigateToDetail(message);
      } else {
         Path path = new DtlReviewsPath(Flow.get(getContext()).getHistory().top(), getPath().getMerchant(), message);
         History.Builder historyBuilder = Flow.get(getContext()).getHistory().buildUpon();
         historyBuilder.pop();
         historyBuilder.pop();
         historyBuilder.push(path);
         Flow.get(getContext()).setHistory(historyBuilder.build(), Flow.Direction.FORWARD);
      }
   }

   public void navigateToDetail(String message) {
      Path path = new DtlMerchantDetailsPath(FlowUtil.currentMaster(getContext()), getPath().getMerchant(), null, message);
      History.Builder historyBuilder = Flow.get(getContext()).getHistory().buildUpon();
      historyBuilder.pop();
      historyBuilder.push(path);
      Flow.get(getContext()).setHistory(historyBuilder.build(), Flow.Direction.BACKWARD);
   }

   private void confirmBack() {
      if (messageDialog != null && messageDialog.isShowing()) {
         messageDialog.dismiss();
      }
      messageDialog = new SweetAlertDialog(getContext(), SweetAlertDialog.WARNING_TYPE);
      messageDialog.setTitleText(getContext().getString(R.string.app_name));
      messageDialog.setContentText(getContext().getString(R.string.review_comment_discard_changes));
      messageDialog.setConfirmText(getContext().getString(R.string.apptentive_yes));
      messageDialog.showCancelButton(true);
      messageDialog.setCancelText(getContext().getString(R.string.apptentive_no));
      messageDialog.setConfirmClickListener(listener -> {
         listener.dismissWithAnimation();
         back();
      });
      messageDialog.show();
   }

   private void back() {
      Flow.get(getContext()).goBack();
   }
}
