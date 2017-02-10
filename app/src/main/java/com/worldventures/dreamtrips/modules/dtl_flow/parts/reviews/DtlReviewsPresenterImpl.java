package com.worldventures.dreamtrips.modules.dtl_flow.parts.reviews;

import android.content.Context;
import com.techery.spares.module.Injector;
import com.worldventures.dreamtrips.modules.dtl.event.ToggleMerchantSelectionAction;
import com.worldventures.dreamtrips.modules.dtl.service.MerchantsInteractor;
import com.worldventures.dreamtrips.modules.dtl.service.PresentationInteractor;
import com.worldventures.dreamtrips.modules.dtl.service.action.ReviewMerchantsAction;
import com.worldventures.dreamtrips.modules.dtl_flow.DtlPresenterImpl;
import com.worldventures.dreamtrips.modules.dtl_flow.ViewState;
import com.worldventures.dreamtrips.modules.dtl_flow.parts.reviews.model.ReviewObject;
import javax.inject.Inject;
import io.techery.janet.ActionPipe;
import io.techery.janet.helper.ActionStateSubscriber;

public class DtlReviewsPresenterImpl extends DtlPresenterImpl<DtlReviewsScreen, ViewState.EMPTY> implements DtlReviewsPresenter {

   @Inject PresentationInteractor presentationInteractor;
   @Inject MerchantsInteractor merchantInteractor;

   public DtlReviewsPresenterImpl(Context context, Injector injector) {
      super(context);
      injector.inject(this);
   }

   @Override
   public void onAttachedToWindow() {
      super.onAttachedToWindow();
      connectReviewMerchants();
   }

   @Override
   public void onBackPressed() {
      presentationInteractor.toggleSelectionPipe().send(ToggleMerchantSelectionAction.clear());
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
      reviewActionPipe.send(ReviewMerchantsAction.create(null));
   }

   private void onMerchantsLoaded(ReviewMerchantsAction action) {
      getView().onRefreshSuccess();
      getView().addCommentsAndReviews(action.getResult().getRatingAvarage().floatValue(), action.getResult().getTotal(),
            ReviewObject.getReviewList(action.getResult().getReviews()));

   }

   private void onMerchantsLoading(ReviewMerchantsAction action, Integer progress) {
      getView().onRefreshProgress();
   }

   private void onMerchantsLoadingError(ReviewMerchantsAction action, Throwable throwable) {
      getView().onRefreshError(action.getErrorMessage());
   }
}