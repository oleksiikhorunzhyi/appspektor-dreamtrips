package com.worldventures.dreamtrips.modules.dtl_flow.parts.reviews;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.StringRes;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.api.error.ErrorResponse;
import com.worldventures.dreamtrips.modules.dtl_flow.DtlLayout;
import com.worldventures.dreamtrips.modules.dtl_flow.parts.reviews.fragments.OfferWithReviewFragment;
import com.worldventures.dreamtrips.modules.dtl_flow.parts.reviews.model.ReviewObject;
import java.util.ArrayList;
import butterknife.InjectView;

public class DtlReviewsScreenImpl extends DtlLayout<DtlReviewsScreen, DtlReviewsPresenter, DtlReviewsPath>
        implements DtlReviewsScreen {

    @InjectView(R.id.toolbar_actionbar) Toolbar toolbar;
    @InjectView(R.id.container_comments_detail) FrameLayout mContainerDetail;
    @InjectView(R.id.swipe_container) SwipeRefreshLayout refreshLayout;
    @InjectView(R.id.emptyView) View emptyView;
    @InjectView(R.id.errorView) View errorView;

    public DtlReviewsScreenImpl(Context context) {
        super(context);
    }

    public DtlReviewsScreenImpl(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public DtlReviewsPresenter createPresenter() {
        return new DtlReviewsPresenterImpl(getContext(), injector, getPath().getMerchant());
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
        refreshLayout.setColorSchemeResources(R.color.theme_main_darker);
        refreshLayout.setEnabled(true);
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

    private void refreshProgress(boolean isShow) {
        refreshLayout.setRefreshing(isShow);
    }

    private void hideRefreshMerchantsError() {
        errorView.setVisibility(GONE);
    }

    @Override
    public void onRefreshSuccess() {
        this.refreshProgress(false);
        this.hideRefreshMerchantsError();
        this.showEmpty(false);
    }

    @Override
    public void onRefreshProgress() {
        this.refreshProgress(true);
        this.hideRefreshMerchantsError();
        this.showEmpty(false);
    }

    @Override
    public void onRefreshError(String error) {
        this.refreshProgress(false);
        this.showEmpty(false);
    }

    @Override
    public void showEmpty(boolean isShow) {
        emptyView.setVisibility(isShow ? VISIBLE : GONE);
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
}
