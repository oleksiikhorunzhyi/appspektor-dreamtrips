package com.worldventures.dreamtrips.modules.common.api;

import com.octo.android.robospice.request.retrofit.RetrofitSpiceRequest;
import com.worldventures.dreamtrips.core.api.ConfigApi;
import com.worldventures.dreamtrips.modules.common.model.S3GlobalConfig;

public abstract class ConfigRequest<T> extends RetrofitSpiceRequest<T, ConfigApi> {

    public ConfigRequest(Class<T> clazz) {
        super(clazz, ConfigApi.class);
    }

    public static class GetConfigRequest extends ConfigRequest<S3GlobalConfig> {

        public GetConfigRequest() {
            super(S3GlobalConfig.class);
        }

        @Override
        public S3GlobalConfig loadDataFromNetwork() throws Exception {
            return getService().getConfig();
        }
    }
}
