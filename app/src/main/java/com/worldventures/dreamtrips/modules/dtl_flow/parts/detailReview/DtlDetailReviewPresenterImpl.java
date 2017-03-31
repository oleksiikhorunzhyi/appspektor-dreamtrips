package com.worldventures.dreamtrips.modules.dtl_flow.parts.detailReview;

import android.content.Context;
import android.view.MenuItem;

import com.techery.spares.module.Injector;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.api.dtl.merchants.requrest.ImmutableSdkFlaggingReviewParams;
import com.worldventures.dreamtrips.modules.dtl.service.MerchantsInteractor;
import com.worldventures.dreamtrips.modules.dtl.service.PresentationInteractor;
import com.worldventures.dreamtrips.modules.dtl.service.action.FlaggingReviewAction;
import com.worldventures.dreamtrips.modules.dtl_flow.DtlPresenterImpl;
import com.worldventures.dreamtrips.modules.dtl_flow.ViewState;

import javax.inject.Inject;

import io.techery.janet.ActionPipe;

public class DtlDetailReviewPresenterImpl extends DtlPresenterImpl<DtlDetailReviewScreen, ViewState.EMPTY> implements DtlDetailReviewPresenter {

    @Inject
    PresentationInteractor presentationInteractor;
    @Inject
    MerchantsInteractor merchantInteractor;

    public DtlDetailReviewPresenterImpl(Context context, Injector injector) {
        super(context);
        injector.inject(this);
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
        ActionPipe<FlaggingReviewAction> reviewActionPipe = merchantInteractor.flaggingReviewHttpPipe();
        reviewActionPipe
              .observeWithReplay()
              .compose(bindViewIoToMainComposer());
        reviewActionPipe.send(FlaggingReviewAction.create(getView().getMerchantId(),
                                                            ImmutableSdkFlaggingReviewParams.builder()
                                                                .authorIpAddress(getIpAddress())
                                                                .contentType(1)
                                                                .feedbackType(1)
                                                                .build()));
    }

    @Override
    public void onItemClick() {
        sendFlag();
    }

    public String getIpAddress() {
        return "190.99.101.25";
    }
}