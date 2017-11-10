package com.worldventures.dreamtrips.modules.dtl_flow.parts.reviews;

import android.content.Context;
import android.view.MenuItem;

import com.worldventures.core.janet.Injector;
import com.worldventures.core.model.User;
import com.worldventures.core.model.session.SessionHolder;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.common.listener.ScrollEventListener;
import com.worldventures.dreamtrips.modules.dtl.event.ToggleMerchantSelectionAction;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.Merchant;
import com.worldventures.dreamtrips.modules.dtl.service.MerchantsInteractor;
import com.worldventures.dreamtrips.modules.dtl.service.PresentationInteractor;
import com.worldventures.dreamtrips.modules.dtl.service.action.ReviewMerchantsAction;
import com.worldventures.dreamtrips.modules.dtl.service.action.bundle.ImmutableReviewsMerchantsActionParams;
import com.worldventures.dreamtrips.modules.dtl_flow.DtlPresenterImpl;
import com.worldventures.dreamtrips.modules.dtl_flow.ViewState;
import com.worldventures.dreamtrips.modules.dtl_flow.parts.comment.DtlCommentReviewPath;
import com.worldventures.dreamtrips.modules.dtl_flow.parts.reviews.model.ReviewObject;
import com.worldventures.dreamtrips.modules.dtl_flow.parts.reviews.storage.ReviewStorage;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import flow.Flow;
import io.techery.janet.ActionPipe;
import io.techery.janet.helper.ActionStateSubscriber;

public class DtlReviewsPresenterImpl extends DtlPresenterImpl<DtlReviewsScreen, ViewState.EMPTY> implements DtlReviewsPresenter {

   @Inject PresentationInteractor presentationInteractor;
   @Inject MerchantsInteractor merchantInteractor;

   ScrollEventListener listener = new ScrollEventListener() {
      @Override
      public void onScrollBottomReached(int indexOf) {
         addMoreReviews(indexOf);
      }
   };

   @Inject
   SessionHolder appSessionHolder;

   private final Merchant merchant;
   private static final String BRAND_ID = "1";
   private User user;

   public DtlReviewsPresenterImpl(Context context, Injector injector, Merchant merchant) {
      super(context);
      injector.inject(this);
      this.merchant = merchant;
   }

   @Override
   public void onAttachedToWindow() {
      super.onAttachedToWindow();
      getView().setEventListener(listener);
      connectReviewMerchants();
      loadFirstReviews();
      getFirstPage();
   }

   @Override
   public void onDetachedFromWindow() {
      super.onDetachedFromWindow();
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
      user = appSessionHolder.get().get().getUser();
      if (ReviewStorage.exists(getContext(), String.valueOf(user.getId()), merchant.id())) {
         getView().userHasPendingReview();
      } else {
         addNewComment();
      }
   }

   @Override
   public void addNewComment() {
      Flow.get(getContext()).set(new DtlCommentReviewPath(merchant, true, false));
   }

   @Override
   public void addMoreReviews(int indexOf) {
      merchantInteractor.reviewsMerchantsHttpPipe()
            .send(ReviewMerchantsAction.create(ImmutableReviewsMerchantsActionParams
                  .builder()
                  .brandId(BRAND_ID)
                  .productId(merchant.id())
                  .limit(10)
                  .indexOf(indexOf)
                  .build()));
   }

   @Override
   public void loadFirstReviews() {
      getView().resetViewData();
      getView().onRefreshProgress();
   }

   @Override
   public void getFirstPage() {
      merchantInteractor.reviewsMerchantsHttpPipe().send(ReviewMerchantsAction
            .create(ImmutableReviewsMerchantsActionParams.builder()
                  .brandId(BRAND_ID)
                  .productId(merchant.id())
                  .limit(10)
                  .indexOf(0)
                  .build()));
   }

   private void connectReviewMerchants() {
      merchantInteractor.reviewsMerchantsHttpPipe()
            .observeWithReplay()
            .compose(bindViewIoToMainComposer())
            .subscribe(new ActionStateSubscriber<ReviewMerchantsAction>()
                  .onSuccess(this::onMerchantsLoaded)
                  .onProgress(this::onMerchantsLoading)
                  .onFail(this::onMerchantsLoadingError));
   }

   public void onMerchantsLoaded(ReviewMerchantsAction action) {
      getView().onRefreshSuccess();

      ArrayList<ReviewObject> reviewObjects = ReviewObject.getReviewList(action.getResult().reviews());
      List<ReviewObject> currentReviews = getView().getCurrentReviews();
      boolean validReceivedData = isValidReceivedData(currentReviews, reviewObjects);
      if (!validReceivedData) return;

      getView().addCommentsAndReviews(Float.parseFloat(action.getResult()
                  .ratingAverage()), Integer.parseInt(action.getResult().total()),
            reviewObjects);
   }

   private void onMerchantsLoading(ReviewMerchantsAction action, Integer progress) {
      getView().onRefreshProgress();
   }

   private void onMerchantsLoadingError(ReviewMerchantsAction action, Throwable throwable) {
      getView().onRefreshError(action.getErrorMessage());
   }

   public boolean isValidReceivedData(List<ReviewObject> currentItems, List<ReviewObject> reviewObjects) {
      if (reviewObjects.isEmpty()) return false;

      if (!currentItems.isEmpty() && !reviewObjects.isEmpty()) {
         ReviewObject lastItem = currentItems.get(currentItems.size() - 1);
         ReviewObject lastReceivedItem = reviewObjects.get(reviewObjects.size() - 1);
         if (lastItem.getReviewId().equals(lastReceivedItem.getReviewId())) return false;
      }
      return true;
   }
}