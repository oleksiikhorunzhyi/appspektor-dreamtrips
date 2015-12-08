package com.worldventures.dreamtrips.modules.dtl.api.merchant;

import com.worldventures.dreamtrips.modules.dtl.api.DtlRequest;

public class RateMerchantRequest extends DtlRequest<Void> {

    private String merchantId;
    private String transactionId;
    private int stars;

    public RateMerchantRequest(String merchantId, int stars, String transactionId) {
        super(Void.class);
        this.merchantId = merchantId;
        this.transactionId = transactionId;
        this.stars = stars;
    }

    @Override
    public Void loadDataFromNetwork() throws Exception {
        return getService().rate(merchantId, stars, transactionId);
    }
}
