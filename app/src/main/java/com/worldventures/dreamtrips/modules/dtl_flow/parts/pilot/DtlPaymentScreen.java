package com.worldventures.dreamtrips.modules.dtl_flow.parts.pilot;

import com.worldventures.dreamtrips.modules.dtl_flow.DtlScreen;

public interface DtlPaymentScreen extends DtlScreen {

    void hideThankYouText();

    void showThankYouText();

    void setSuccessPaymentText();

    void setFailurePaymentText();

    void setPaymentValue(String value);

    void showTotalChargedText();

    void hideTotalChargedText();

    void setSuccessResume(String points);

    void setFailureResume();

    void setPaymentSuccessImage();

    void setPaymentFailureImage();
}
