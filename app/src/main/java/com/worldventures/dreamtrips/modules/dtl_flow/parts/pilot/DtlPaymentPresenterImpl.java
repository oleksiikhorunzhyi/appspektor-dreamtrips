package com.worldventures.dreamtrips.modules.dtl_flow.parts.pilot;

import android.content.Context;

import com.worldventures.dreamtrips.modules.dtl_flow.DtlPresenterImpl;
import com.worldventures.dreamtrips.modules.dtl_flow.ViewState;

public class DtlPaymentPresenterImpl extends DtlPresenterImpl<DtlPaymentScreen, ViewState.EMPTY> implements DtlPaymentPresenter {

    private boolean isPaid;

    public DtlPaymentPresenterImpl(Context context, boolean isPaid) {
        super(context);
        this.isPaid = isPaid;
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        init();
    }

    private void init() {
        if (isPaid) {
            getView().showThankYouText();
            getView().setSuccessPaymentText();
            getView().setPaymentSuccessImage();
            getView().setPaymentValue("15.59");
            getView().showTotalChargedText();
            getView().setSuccessResume("31");
        } else {
            getView().hideThankYouText();
            getView().setFailurePaymentText();
            getView().setPaymentFailureImage();
            getView().setPaymentValue("15.59");
            getView().hideTotalChargedText();
            getView().setFailureResume();
        }
    }

    @Override
    public void onBackPressed() {
    }
}