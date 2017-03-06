package com.worldventures.dreamtrips.modules.dtl_flow.parts.detailReview;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RatingBar;
import android.widget.TextView;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.api.error.ErrorResponse;
import com.worldventures.dreamtrips.modules.common.view.custom.ImageryDraweeView;
import com.worldventures.dreamtrips.modules.dtl_flow.DtlLayout;
import com.worldventures.dreamtrips.modules.dtl_flow.parts.reviews.model.CSTConverter;
import com.worldventures.dreamtrips.modules.dtl_flow.parts.reviews.model.ReviewObject;

import java.text.ParseException;

import butterknife.InjectView;

public class DtlDetailReviewScreenImpl extends DtlLayout<DtlDetailReviewScreen, DtlDetailReviewPresenter, DtlDetailReviewPath>
        implements DtlDetailReviewScreen {

    @InjectView(R.id.toolbar_actionbar)
    Toolbar toolbar;
    @InjectView(R.id.swipe_container)
    SwipeRefreshLayout refreshLayout;
    @InjectView(R.id.emptyView)
    View emptyView;
    @InjectView(R.id.errorView)
    View errorView;

    @InjectView(R.id.ivItemReview)
    ImageryDraweeView mIvAvatar;
    @InjectView(R.id.tvUserName)
    TextView mTvUserName;
    @InjectView(R.id.rbRating)
    RatingBar mRatingBar;
    @InjectView(R.id.tvCommentWrote)
    TextView mTvCommentWrote;
    @InjectView(R.id.tvVerifiedBuyer)
    TextView mTvIsVerified;
    @InjectView(R.id.tvComment)
    TextView mTvComment;

    public DtlDetailReviewScreenImpl(Context context) {
        super(context);
    }

    public DtlDetailReviewScreenImpl(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public DtlDetailReviewPresenter createPresenter() {
        return new DtlDetailReviewPresenterImpl(getContext(), injector);
    }

    @Override
    protected void onPostAttachToWindowView() {
        inflateToolbarMenu(toolbar);
        toolbar.setNavigationIcon(R.drawable.back_icon);
        toolbar.setNavigationOnClickListener(view ->
                getActivity().onBackPressed());
        toolbar.setTitle(getPath().getMerchant());

        refreshLayout.setColorSchemeResources(R.color.theme_main_darker);
        refreshLayout.setEnabled(true);

        initData();
    }

    private void initData() {
        ReviewObject reviewObject = getPath().getReviewObject();
        if (null != reviewObject) {
            mTvUserName.setText(reviewObject.getNameUser());
            mTvCommentWrote.setText(reviewObject.getTimeWrote());
            if (reviewObject.isVerifiedReview()) {
                mTvIsVerified.setVisibility(View.VISIBLE);
            } else {
                mTvIsVerified.setVisibility(View.GONE);
            }
            mTvComment.setText(reviewObject.getComment());
            mRatingBar.setRating(reviewObject.getRatingCommentUser());

            initTimeZone(reviewObject.getTimeWrote());
            initImageFromServer(reviewObject.getUrlImageUser());
        }
    }

    private void initTimeZone(@NonNull String timeWrote) {
        try {
            CSTConverter converter = new CSTConverter();
            mTvCommentWrote.setText(converter.getCorrectTimeWrote(getContext(),
                    timeWrote));
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    private void initImageFromServer(@NonNull String urlImage) {
        if (!urlImage.equalsIgnoreCase("null")) {
            mIvAvatar.setImageURI(Uri.parse(urlImage));
        }
    }

    private void refreshProgress(boolean isShow) {
        refreshLayout.setRefreshing(isShow);
    }

    private void hideRefreshMerchantsError() {
        errorView.setVisibility(GONE);
    }

    @Override
    public void finish() {
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