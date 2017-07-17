package com.worldventures.dreamtrips.modules.dtl_flow.parts.detailReview;

import android.content.Context;
import android.util.Log;
import android.view.MenuItem;

import com.techery.spares.module.Injector;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.api.dtl.merchants.requrest.ImmutableGetUrlTokenParamsSdk;
import com.worldventures.dreamtrips.api.dtl.merchants.requrest.ImmutableLocation;
import com.worldventures.dreamtrips.api.dtl.merchants.requrest.Location;
import com.worldventures.dreamtrips.core.utils.DateTimeUtils;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.thrst.ImmutableGetUrlTokenParams;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.thrst.ImmutableLocationThrst;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.thrst.LocationThrst;
import com.worldventures.dreamtrips.modules.dtl.service.MerchantsInteractor;
import com.worldventures.dreamtrips.modules.dtl.service.PresentationInteractor;
import com.worldventures.dreamtrips.modules.dtl.service.action.AddReviewAction;
import com.worldventures.dreamtrips.modules.dtl.service.action.UrlTokenAction;
import com.worldventures.dreamtrips.modules.dtl.service.action.bundle.ImmutableUrlTokenActionParams;
import com.worldventures.dreamtrips.modules.dtl_flow.DtlPresenterImpl;
import com.worldventures.dreamtrips.modules.dtl_flow.FlowUtil;
import com.worldventures.dreamtrips.modules.dtl_flow.ViewState;
import com.worldventures.dreamtrips.modules.dtl_flow.parts.details.DtlMerchantDetailsPath;
import com.worldventures.dreamtrips.modules.dtl_flow.parts.reviews.DtlReviewsPath;
import com.worldventures.dreamtrips.modules.dtl_flow.parts.reviews.model.ReviewObject;
import com.worldventures.dreamtrips.modules.dtl_flow.parts.utils.NetworkUtils;

import javax.inject.Inject;

import flow.Flow;
import flow.History;
import flow.path.Path;
import io.techery.janet.ActionPipe;
import io.techery.janet.helper.ActionStateSubscriber;

public class DtlDetailReviewPresenterImpl extends DtlPresenterImpl<DtlDetailReviewScreen, ViewState.EMPTY> implements DtlDetailReviewPresenter {

    @Inject
    PresentationInteractor presentationInteractor;
    @Inject
    MerchantsInteractor merchantInteractor;

    private final String merchantName;
    private final ReviewObject reviewObject;
    private static final String BRAND_ID = "1";

    public DtlDetailReviewPresenterImpl(Context context, Injector injector, String merchantName, ReviewObject reviewObject) {
        super(context);
        injector.inject(this);
        this.merchantName = merchantName;
        this.reviewObject = reviewObject;
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
    }

    @Override
    public void onBackPressed() {
    }

    @Override
    public boolean onToolbarMenuItemClick(MenuItem item) {
        if (item.getItemId() == R.id.action_add_flag){
            onItemClick();
        }
        return super.onToolbarMenuItemClick(item);
    }

    @Override
    public int getMenuFlag() {
        return R.menu.menu_detail_review;
    }

    @Override
    public void sendFlag() {
       /* ActionPipe<FlaggingReviewAction> reviewActionPipe = merchantInteractor.flaggingReviewHttpPipe();
        reviewActionPipe
              .observeWithReplay()
              .compose(bindViewIoToMainComposer());
        reviewActionPipe.send(FlaggingReviewAction.create(getView().getMerchantId(),
                                                            ImmutableSdkFlaggingReviewParams.builder()
                                                                .authorIpAddress(getIpAddress())
                                                                .contentType(1)
                                                                .feedbackType(1)
                                                                .build()));*/


        ActionPipe<UrlTokenAction> reviewActionPipe = merchantInteractor.urlTokenThrstHttpPipe();

        reviewActionPipe
              .observeWithReplay()
              .compose(bindViewIoToMainComposer())
              .subscribe(new ActionStateSubscriber<UrlTokenAction>()
                    .onSuccess(this::onMerchantsLoaded)
                    .onProgress(this::onMerchantsLoading)
                    .onFail(this::onMerchantsLoadingError));

        reviewActionPipe.send(UrlTokenAction.create(getView().getMerchantId(),
                                                      //ImmutableGetUrlTokenParams.builder()
                                                        ImmutableUrlTokenActionParams.builder()
                                                            .checkinTime(DateTimeUtils.currentUtcString())
                                                            //.currencyCode(merchant.asMerchantAttributes().defaultCurrency().code())
                                                            .currencyCode("USD")
                                                            .receiptPhotoUrl("https://dtappqa.s3.amazonaws.com/18B1BC60-77B7-4579-AA60-9E9E1BC181E4_1496844041254.jpg")
                                                            /*.location(ImmutableLocationThrst.builder()
                                                                        .coordinates("-34.89128014541686,-56.19534546514618")
                                                                        .build())*/
                                                              .location(ImmutableLocation.builder().coordinates("33.0638987,-96.8020342").build())
                                                            .build()));
    }

    private void onMerchantsLoaded(UrlTokenAction action) {
        getView().onRefreshSuccess();
        //String token = action.getResult().thrstInfo().token();
        //String url = action.getResult().thrstInfo().redirectUrl();

        //Log.e("dataInfoPilot", "token = " + token + " \n =============================== \n url = " + url);
    }

    private void onMerchantsLoading(UrlTokenAction action, Integer progress) {
        getView().onRefreshProgress();
    }

    private void onMerchantsLoadingError(UrlTokenAction action, Throwable throwable) {
        getView().onRefreshError(action.toString());
    }

    @Override
    public void onItemClick() {
        sendFlag();
    }

    @Override
    public void validateComingFrom() {
        if (getView().isFromListReview()){
            navigateToListReview("");
        } else {
            navigateToDetail("");
        }
    }

    @Override
    public void navigateToListReview(String message) {
        try{
            //TODO add merchant
            Path path = new DtlReviewsPath(null, message);
            History.Builder historyBuilder = Flow.get(getContext()).getHistory().buildUpon();
            historyBuilder.pop();
            historyBuilder.push(path);
            Flow.get(getContext()).setHistory(historyBuilder.build(), Flow.Direction.FORWARD);
        } catch (Exception e){
            e.printStackTrace();
        }

    }

    @Override
    public void navigateToDetail(String message) {
        try{
            Path path = new DtlMerchantDetailsPath(FlowUtil.currentMaster(getContext()), null, null, message);
            History.Builder historyBuilder = Flow.get(getContext()).getHistory().buildUpon();
            historyBuilder.pop();
            historyBuilder.push(path);
            Flow.get(getContext()).setHistory(historyBuilder.build(), Flow.Direction.BACKWARD);
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    private void onMerchantsLoaded(AddReviewAction action) {
        getView().onRefreshSuccess();
    }

    private void onMerchantsLoading(AddReviewAction action, Integer progress) {
        getView().onRefreshProgress();
    }

    private void onMerchantsLoadingError(AddReviewAction action, Throwable throwable) {
        getView().onRefreshError(throwable.getMessage());
    }

    public String getIpAddress() {
        return NetworkUtils.getIpAddress(true);
    }
}