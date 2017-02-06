package com.worldventures.dreamtrips.modules.dtl_flow.parts.reviews;

import android.content.Context;
import android.util.Log;

import com.techery.spares.module.Injector;
import com.worldventures.dreamtrips.modules.dtl.event.ToggleMerchantSelectionAction;
import com.worldventures.dreamtrips.modules.dtl.service.PresentationInteractor;
import com.worldventures.dreamtrips.modules.dtl_flow.DtlPresenterImpl;
import com.worldventures.dreamtrips.modules.dtl_flow.ViewState;
import com.worldventures.dreamtrips.modules.dtl_flow.parts.reviews.model.ReviewObject;

import java.util.ArrayList;

import javax.inject.Inject;

public class DtlReviewsPresenterImpl extends DtlPresenterImpl<DtlReviewsScreen, ViewState.EMPTY> implements DtlReviewsPresenter {

   @Inject PresentationInteractor presentationInteractor;

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
}