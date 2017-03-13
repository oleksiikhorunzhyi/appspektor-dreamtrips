package com.worldventures.dreamtrips.modules.dtl_flow.parts.detailReview;

import android.content.Context;

import com.techery.spares.module.Injector;
import com.worldventures.dreamtrips.modules.dtl.service.MerchantsInteractor;
import com.worldventures.dreamtrips.modules.dtl.service.PresentationInteractor;
import com.worldventures.dreamtrips.modules.dtl.service.action.AddReviewAction;
import com.worldventures.dreamtrips.modules.dtl_flow.DtlPresenterImpl;
import com.worldventures.dreamtrips.modules.dtl_flow.ViewState;
import com.worldventures.dreamtrips.modules.dtl_flow.parts.reviews.model.ReviewObject;

import javax.inject.Inject;

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
        /*Path path = new DtlMerchantDetailsPath(FlowUtil.currentMaster(getContext()), merchant, null, "");
        History.Builder historyBuilder = Flow.get(getContext()).getHistory().buildUpon();
        historyBuilder.pop();
        historyBuilder.pop();
        historyBuilder.push(path);
        Flow.get(getContext()).setHistory(historyBuilder.build(), Flow.Direction.BACKWARD);*/
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
}