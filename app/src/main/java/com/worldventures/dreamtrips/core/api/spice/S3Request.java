package com.worldventures.dreamtrips.core.api.spice;

import com.octo.android.robospice.request.retrofit.RetrofitSpiceRequest;
import com.worldventures.dreamtrips.core.api.S3Api;
import com.worldventures.dreamtrips.core.model.config.S3GlobalConfig;

public abstract class S3Request<T> extends RetrofitSpiceRequest<T, S3Api> {
    public S3Request(Class<T> clazz) {
        super(clazz, S3Api.class);
    }

    public static class GetConfigRequest extends S3Request<S3GlobalConfig> {

        public GetConfigRequest() {
            super(S3GlobalConfig.class);
        }

        @Override
        public S3GlobalConfig loadDataFromNetwork() throws Exception {
            return getService().getConfig();
        }
    }
}
