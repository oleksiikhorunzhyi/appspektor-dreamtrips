package com.worldventures.dreamtrips.modules.dtl_flow.parts.pilot;

import android.content.Context;

import com.worldventures.dreamtrips.modules.dtl.service.PresentationInteractor;
import com.worldventures.dreamtrips.modules.dtl_flow.DtlPresenterImpl;
import com.worldventures.dreamtrips.modules.dtl_flow.ViewState;

import javax.inject.Inject;

public class DtlPaymentPresenterImpl extends DtlPresenterImpl<DtlPaymentScreen, ViewState.EMPTY> implements DtlPaymentPresenter {

    private boolean isPaid;
    private String totalAmount;

    @Inject
    PresentationInteractor presentationInteractor;

    public DtlPaymentPresenterImpl(Context context, boolean isPaid, String totalAmount) {
        super(context);
        this.isPaid = isPaid;
        this.totalAmount = totalAmount;
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        getView().initToolbar();
        init();
    }

    private void init() {
        if (isPaid) {
            getView().thankYouSuccessfulText();
            getView().setSuccessPaymentText();
            getView().setPaymentSuccessImage();
            getView().setChargeMoney(totalAmount);
            getView().showTotalChargedText();
            getView().setSuccessResume();
            getView().setShowScreenSuccessMessage();
            getView().hideSubThankYouMessage();
        } else {
            getView().thankYouFailureText();
            getView().setFailurePaymentText();
            getView().setPaymentFailureImage();
            getView().setChargeMoney(totalAmount);
            getView().hideTotalChargedText();
            getView().setFailureResume();
            getView().setShowScreenFailureMessage();
            getView().showSubThankYouMessage();
        }
    }

    @Override
    public void onBackPressed() {
        getView().goBack();
    }
}