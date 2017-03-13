package com.worldventures.dreamtrips.modules.dtl_flow.parts.reviews;

import android.content.Context;
import android.view.MenuItem;

import com.techery.spares.module.Injector;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.api.dtl.merchants.requrest.ImmutableReviewParams;
import com.worldventures.dreamtrips.modules.dtl.event.ToggleMerchantSelectionAction;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.Merchant;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.reviews.ImmutableReview;
import com.worldventures.dreamtrips.modules.dtl.service.MerchantsInteractor;
import com.worldventures.dreamtrips.modules.dtl.service.PresentationInteractor;
import com.worldventures.dreamtrips.modules.dtl.service.action.AddReviewAction;
import com.worldventures.dreamtrips.modules.dtl.service.action.ReviewMerchantsAction;
import com.worldventures.dreamtrips.modules.dtl.service.action.bundle.ImmutableAddReviewsActionParams;
import com.worldventures.dreamtrips.modules.dtl.service.action.bundle.ImmutableReviewsMerchantsActionParams;
import com.worldventures.dreamtrips.modules.dtl_flow.DtlPresenterImpl;
import com.worldventures.dreamtrips.modules.dtl_flow.ViewState;
import com.worldventures.dreamtrips.modules.dtl_flow.parts.comment.DtlCommentReviewPath;
import com.worldventures.dreamtrips.modules.dtl_flow.parts.reviews.model.ReviewObject;
import javax.inject.Inject;

import flow.Flow;
import io.techery.janet.ActionPipe;
import io.techery.janet.helper.ActionStateSubscriber;

public class DtlReviewsPresenterImpl extends DtlPresenterImpl<DtlReviewsScreen, ViewState.EMPTY> implements DtlReviewsPresenter {

   @Inject PresentationInteractor presentationInteractor;
   @Inject MerchantsInteractor merchantInteractor;

   private final Merchant merchant;
   private static final String BRAND_ID = "1";

   public DtlReviewsPresenterImpl(Context context, Injector injector, Merchant merchant) {
      super(context);
      injector.inject(this);
      this.merchant = merchant;
   }

   @Override
   public void onAttachedToWindow() {
      super.onAttachedToWindow();
      connectReviewMerchants();
   }

   @Override
   public int getToolbarMenuRes() {
      return R.menu.menu_review_merchant;
   }

   @Override
   public boolean onToolbarMenuItemClick(MenuItem item) {
      if (item.getItemId() == R.id.action_add_review) onAddClick();
      return super.onToolbarMenuItemClick(item);
   }

   @Override
   public void onBackPressed() {
      presentationInteractor.toggleSelectionPipe().send(ToggleMerchantSelectionAction.clear());
   }

   @Override
   public void onAddClick() {
      Flow.get(getContext()).set(new DtlCommentReviewPath(merchant, true));
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
      getView().addCommentsAndReviews(Float.parseFloat(action.getResult().ratingAverage()), Integer.parseInt(action.getResult().total()),
            ReviewObject.getReviewList(action.getResult().reviews()));

   }

   private void onMerchantsLoading(ReviewMerchantsAction action, Integer progress) {
      getView().onRefreshProgress();
   }

   private void onMerchantsLoadingError(ReviewMerchantsAction action, Throwable throwable) {
      getView().onRefreshError(action.getErrorMessage());
   }
}