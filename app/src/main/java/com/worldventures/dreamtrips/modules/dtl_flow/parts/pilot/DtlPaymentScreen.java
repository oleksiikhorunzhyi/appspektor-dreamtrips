package com.worldventures.dreamtrips.modules.dtl_flow.parts.pilot;

import com.worldventures.dreamtrips.modules.dtl_flow.DtlScreen;

public interface DtlPaymentScreen extends DtlScreen {

    void setChargeMoney(String money);

    void thankYouSuccessfulText();

    void thankYouFailureText();

    void setSuccessPaymentText();

    void setFailurePaymentText();

    void showTotalChargedText();

    void hideTotalChargedText();

    void setSuccessResume();

    void setFailureResume();

    void setPaymentSuccessImage();

    void setPaymentFailureImage();

    void setShowScreenSuccessMessage();

    void setShowScreenFailureMessage();

    void showSubThankYouMessage();

    void hideSubThankYouMessage();

    void initToolbar();

    void goBack();
}
