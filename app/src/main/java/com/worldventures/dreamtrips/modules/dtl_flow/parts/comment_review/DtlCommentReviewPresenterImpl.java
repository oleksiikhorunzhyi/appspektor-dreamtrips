package com.worldventures.dreamtrips.modules.dtl_flow.parts.comment_review;

import android.content.Context;

import com.techery.spares.module.Injector;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.Merchant;
import com.worldventures.dreamtrips.modules.dtl.service.MerchantsInteractor;
import com.worldventures.dreamtrips.modules.dtl.service.PresentationInteractor;
import com.worldventures.dreamtrips.modules.dtl.service.action.ReviewMerchantsAction;
import com.worldventures.dreamtrips.modules.dtl.service.action.bundle.ImmutableReviewsMerchantsActionParams;
import com.worldventures.dreamtrips.modules.dtl_flow.DtlPresenterImpl;
import com.worldventures.dreamtrips.modules.dtl_flow.FlowUtil;
import com.worldventures.dreamtrips.modules.dtl_flow.ViewState;
import com.worldventures.dreamtrips.modules.dtl_flow.parts.details.DtlMerchantDetailsPath;

import javax.inject.Inject;

import flow.Flow;
import flow.History;
import flow.path.Path;
import io.techery.janet.ActionPipe;
import io.techery.janet.helper.ActionStateSubscriber;

/**
 * Created by andres.rubiano on 20/02/2017.
 */

public class DtlCommentReviewPresenterImpl extends DtlPresenterImpl<DtlCommentReviewScreen, ViewState.EMPTY> implements DtlCommentReviewsPresenter {

   @Inject PresentationInteractor presentationInteractor;
   @Inject MerchantsInteractor merchantInteractor;

   private final Merchant merchant;
   private static final String BRAND_ID = "1";

   public DtlCommentReviewPresenterImpl(Context context, Injector injector, Merchant merchant) {
      super(context);
      injector.inject(this);
      this.merchant = merchant;
   }

   @Override
   public void onAttachedToWindow() {
      super.onAttachedToWindow();
   }

   @Override
   public void onBackPressed() {
      if (getView().getRatingBar() > 0 || getView().getSizeComment() > 0) {
         getView().showDialogMessage(getContext().getString(R.string.review_comment_discard_changes));
      } else {
         navigateToDetail("");
      }
   }

   @Override
   public void navigateToDetail(String message) {
      Path path = new DtlMerchantDetailsPath(FlowUtil.currentMaster(getContext()), merchant, null, message);
      if (Flow.get(getContext()).getHistory().size() < 2) {
         Flow.get(getContext()).set(path);
      } else {
         History.Builder historyBuilder = Flow.get(getContext()).getHistory().buildUpon();
         historyBuilder.pop();
         historyBuilder.push(path);
         Flow.get(getContext()).setHistory(historyBuilder.build(), Flow.Direction.BACKWARD);
      }
   }

   @Override
   public void validateComment() {
      if (getView().isMinimumCharacterWrote()){
         if (getView().isMaximumCharacterWrote()){
            navigateToDetail(getContext().getString(R.string.snack_review_success));
         } else {
            getView().showSnackbarMessage(getContext().getString(R.string.review_comment_major_letter));
         }
      } else {
         getView().showSnackbarMessage(getContext().getString(R.string.review_comment_minor_letter));
      }
   }

   private void connectReviewMerchants() {
      ActionPipe<ReviewMerchantsAction> reviewActionPipe = merchantInteractor.reviewsMerchantsHttpPipe();
      reviewActionPipe
            .observeWithReplay()
            .compose(bindViewIoToMainComposer())
            .subscribe(new ActionStateSubscriber<ReviewMerchantsAction>()
                  .onSuccess(this::onMerchantsLoaded)
                  .onProgress(this::onMerchantsLoading)
                  .onFail(this::onMerchantsLoadingError));
      reviewActionPipe.send(ReviewMerchantsAction.create(ImmutableReviewsMerchantsActionParams
            .builder()
            .brandId(BRAND_ID)
            .productId(merchant.id())
            .build()));
   }

   private void onMerchantsLoaded(ReviewMerchantsAction action) {
      getView().onRefreshSuccess();
      /*getView().addCommentsAndReviews(action.getResult().getRatingAverage().floatValue(), action.getResult().getTotal(),
            ReviewObject.getReviewList(action.getResult().getReviews()));*/

   }

   private void onMerchantsLoading(ReviewMerchantsAction action, Integer progress) {
      getView().onRefreshProgress();
   }

   private void onMerchantsLoadingError(ReviewMerchantsAction action, Throwable throwable) {
      getView().onRefreshError(action.getErrorMessage());
   }
}