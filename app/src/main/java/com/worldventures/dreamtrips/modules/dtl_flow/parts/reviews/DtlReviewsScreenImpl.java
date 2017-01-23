package com.worldventures.dreamtrips.modules.dtl_flow.parts.reviews;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.StringRes;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.api.error.ErrorResponse;
import com.worldventures.dreamtrips.modules.dtl_flow.DtlLayout;
import com.worldventures.dreamtrips.modules.dtl_flow.parts.details.DtlDetailsScreen;
import com.worldventures.dreamtrips.modules.dtl_flow.parts.details.DtlDetailsScreenImpl;
import com.worldventures.dreamtrips.modules.dtl_flow.parts.reviews.fragments.OfferWithReviewFragment;
import com.worldventures.dreamtrips.modules.dtl_flow.parts.reviews.model.ReviewObject;

import java.util.ArrayList;

import butterknife.InjectView;

/**
 * Created by yair.carreno on 2/1/2017.
 */

public class DtlReviewsScreenImpl extends DtlLayout<DtlReviewsScreen, DtlReviewsPresenter, DtlReviewsPath>
        implements DtlReviewsScreen {

    @InjectView(R.id.toolbar_actionbar) Toolbar toolbar;
    @InjectView(R.id.container_comments_detail) FrameLayout mContainerDetail;

    public DtlReviewsScreenImpl(Context context) {
        super(context);
    }

    public DtlReviewsScreenImpl(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onPostAttachToWindowView() {
        inflateToolbarMenu(toolbar);
        toolbar.setTitle("Reviews");
        toolbar.setNavigationIcon(R.drawable.back_icon);
        toolbar.setNavigationOnClickListener(view -> {
            getPresenter().onBackPressed();
            getActivity().onBackPressed();
        });
       getPresenter().addNewComments(DtlDetailsScreenImpl.RATING_MERCHANT,
                                    DtlDetailsScreenImpl.SIZE_COMMENTS,
                                    ReviewObject.getDummies(DtlDetailsScreenImpl.SIZE_COMMENTS));
    }

   private void addDummiesContent() {
      Bundle bundle = new Bundle();
      int count = DtlDetailsScreenImpl.SIZE_COMMENTS;
      bundle.putParcelableArrayList(OfferWithReviewFragment.ARRAY, ReviewObject.getDummies(count));
      bundle.putFloat(OfferWithReviewFragment.RATING_MERCHANT, 3.5f);
      bundle.putInt(OfferWithReviewFragment.COUNT_REVIEW, count);

      FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
      transaction.replace(R.id.container_comments_detail, OfferWithReviewFragment.newInstance(bundle));
      transaction.commit();
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
    public DtlReviewsPresenter createPresenter() {
        return new DtlReviewsPresenterImpl(getContext(), injector);
    }

   @Override
   public void addCommentsAndReviews(float ratingMerchant, int countReview, ArrayList<ReviewObject> listReviews) {
      Bundle bundle = new Bundle();
      bundle.putParcelableArrayList(OfferWithReviewFragment.ARRAY, listReviews);
      bundle.putFloat(OfferWithReviewFragment.RATING_MERCHANT, ratingMerchant);
      bundle.putInt(OfferWithReviewFragment.COUNT_REVIEW, countReview);

      FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
      transaction.replace(R.id.container_comments_detail, OfferWithReviewFragment.newInstance(bundle));
      transaction.commit();
   }
}
