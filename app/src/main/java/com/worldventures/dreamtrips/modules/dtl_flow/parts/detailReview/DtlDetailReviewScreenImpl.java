package com.worldventures.dreamtrips.modules.dtl_flow.parts.detailReview;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.api.error.ErrorResponse;
import com.worldventures.dreamtrips.modules.common.view.custom.ImageryDraweeView;
import com.worldventures.dreamtrips.modules.dtl_flow.DtlLayout;
import com.worldventures.dreamtrips.modules.dtl_flow.parts.reviews.adapter.ReviewImagesAdapter;
import com.worldventures.dreamtrips.modules.dtl_flow.parts.reviews.model.CSTConverter;
import com.worldventures.dreamtrips.modules.dtl_flow.parts.reviews.model.ReviewObject;

import java.text.ParseException;

import butterknife.InjectView;
import cn.pedant.SweetAlert.SweetAlertDialog;

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
    @InjectView(R.id.tv_verified_buyer)
    TextView mTvIsVerified;
    @InjectView(R.id.toolbar_change)
    Toolbar mTlMenuOption;
    @InjectView(R.id.iv_verified_buyer)
    ImageView mIvVerifiedBuyer;
    @InjectView(R.id.photos_indicator_layout)
    LinearLayout mPhotosIndicatorLayout;
    @InjectView(R.id.pics_number_tv)
    TextView mPhotosNumberIndicator;
    @InjectView(R.id.photos)
    RecyclerView mPhotosRecyclerView;
    private LinearLayoutManager mLayoutManager;
    private ReviewImagesAdapter mImagesAdapter;
    private ReviewObject reviewObject;

    private SweetAlertDialog errorDialog;

    public DtlDetailReviewScreenImpl(Context context) {
        super(context);
    }

    public DtlDetailReviewScreenImpl(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public DtlDetailReviewPresenter createPresenter() {
        return new DtlDetailReviewPresenterImpl(getContext(), injector, getPath().getMerchant(), getPath().getReviewObject());
    }

    @Override
    protected void onPostAttachToWindowView() {
        inflateToolbarMenu(toolbar);
        toolbar.setNavigationIcon(R.drawable.back_icon);
        toolbar.setNavigationOnClickListener(view ->
                getActivity().onBackPressed());
              //getPresenter().validateComingFrom());
        toolbar.setTitle(getPath().getMerchant());

        refreshLayout.setColorSchemeResources(R.color.theme_main_darker);
        refreshLayout.setEnabled(true);

        mTlMenuOption.inflateMenu(getPresenter().getMenuFlag());
        mTlMenuOption.setOnMenuItemClickListener(getPresenter()::onToolbarMenuItemClick);
        initData();
    }

    private void initData() {
        reviewObject = getPath().getReviewObject();
        if (null != reviewObject) {
            mTvUserName.setText(reviewObject.getNameUser());
            mTvCommentWrote.setText(reviewObject.getTimeWrote());
            if (reviewObject.isVerifiedReview()) {
                mTvIsVerified.setVisibility(View.VISIBLE);
                mIvVerifiedBuyer.setVisibility(View.VISIBLE);
            } else {
                mTvIsVerified.setVisibility(View.GONE);
                mIvVerifiedBuyer.setVisibility(View.GONE);
            }
            mRatingBar.setRating(reviewObject.getRatingCommentUser());

            initTimeZone(reviewObject.getTimeWrote());
            initImageFromServer(reviewObject.getUrlImageUser());

            if(reviewObject.getUrlReviewImages().size() > 0){
                mPhotosNumberIndicator.setText(String.valueOf(reviewObject.getUrlReviewImages().size()));
            } else  {
                mPhotosIndicatorLayout.setVisibility(View.INVISIBLE);
            }
            setupPhotosList();
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

    private void setupPhotosList(){
        mLayoutManager = new LinearLayoutManager(getActivity());
        mPhotosRecyclerView.setLayoutManager(mLayoutManager);
        mPhotosRecyclerView.setHasFixedSize(true);
        mImagesAdapter = new ReviewImagesAdapter(reviewObject.getComment(), reviewObject.getUrlReviewImages());
        mPhotosRecyclerView.setAdapter(mImagesAdapter);
    }

    @Override
    public void finish() {
    }

    @Override
    public void enableInputs() {
        enableButtons(true);
    }

    @Override
    public void disableInputs() {
        enableButtons(false);
    }

    private void enableButtons(boolean status) {
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
    public String getMerchantId() {
        return getPath().getMerchantId();
    }

    @Override
    public boolean isFromListReview() {
        return getPath().isFromListReview();
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