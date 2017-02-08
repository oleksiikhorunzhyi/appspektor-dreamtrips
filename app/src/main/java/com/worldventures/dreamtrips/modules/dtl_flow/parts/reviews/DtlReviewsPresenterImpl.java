package com.worldventures.dreamtrips.modules.dtl_flow.parts.reviews;

import android.content.Context;
import android.util.Log;

import com.techery.spares.module.Injector;
import com.worldventures.dreamtrips.modules.dtl.event.ToggleMerchantSelectionAction;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.reviews.Review;
import com.worldventures.dreamtrips.modules.dtl.service.MerchantsInteractor;
import com.worldventures.dreamtrips.modules.dtl.service.PresentationInteractor;
import com.worldventures.dreamtrips.modules.dtl.service.action.ReviewMerchantsAction;
import com.worldventures.dreamtrips.modules.dtl_flow.DtlPresenterImpl;
import com.worldventures.dreamtrips.modules.dtl_flow.ViewState;
import com.worldventures.dreamtrips.modules.dtl_flow.parts.reviews.model.ReviewObject;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import io.techery.janet.helper.ActionStateSubscriber;

public class DtlReviewsPresenterImpl extends DtlPresenterImpl<DtlReviewsScreen, ViewState.EMPTY> implements DtlReviewsPresenter {

   @Inject PresentationInteractor presentationInteractor;
   @Inject MerchantsInteractor merchantInteractor;

   public DtlReviewsPresenterImpl(Context context, Injector injector) {
      super(context);
      injector.inject(this);
   }

   @Override
   public void onBackPressed() {
      presentationInteractor.toggleSelectionPipe().send(ToggleMerchantSelectionAction.clear());
   }

   @Override
   public void addNewComments(float ratingMerchant, int countReview, ArrayList<ReviewObject> listReviews) {
      getView().addCommentsAndReviews(ratingMerchant, countReview, listReviews);
   }

   @Override
   public void loadAllReviews() {
      merchantInteractor.reviewsMerchantsHttpPipe().send(ReviewMerchantsAction.create(null));
   }

   //TODO: Rx to get reviews
   /*
   private void connectReviewMerchants() {
      merchantInteractor.reviewsMerchantsHttpPipe()
            .observeSuccessWithReplay()
            .take(1)
            .compose(bindViewIoToMainComposer())
            .map(ReviewMerchantsAction::getResult)
            .subscribe(reviewsMerchant -> getView().addNewComments(0.5f, reviewsMerchant.getTotal(), createReview(reviewsMerchant.getReviews())));
   }*/

   //TODO: MAPEO DE OBJETO DE BACK Y VIEW
   private List<ReviewObject> createReview(List<Review> listReviews) {
      ArrayList<ReviewObject> lr = new ArrayList<>();
      ReviewObject r = new ReviewObject("","" , 0.5f,"","");
      lr.add(r);
      return lr;
   }
}