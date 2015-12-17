package com.worldventures.dreamtrips.modules.dtl.api.place;

import com.worldventures.dreamtrips.modules.dtl.api.DtlRequest;

public class RatePlaceRequest extends DtlRequest<Void> {

    private String merchantId;
    private String transactionId;
    private int stars;

    public RatePlaceRequest(String merchantId, int stars, String transactionId) {
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
