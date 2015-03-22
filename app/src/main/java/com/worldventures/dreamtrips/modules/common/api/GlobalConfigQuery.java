package com.worldventures.dreamtrips.modules.common.api;

import com.octo.android.robospice.request.retrofit.RetrofitSpiceRequest;
import com.worldventures.dreamtrips.core.api.ConfigApi;
import com.worldventures.dreamtrips.modules.common.model.AppConfig;

public abstract class GlobalConfigQuery<T> extends RetrofitSpiceRequest<T, ConfigApi> {

    public GlobalConfigQuery(Class<T> clazz) {
        super(clazz, ConfigApi.class);
    }

    public static class GetConfigRequest extends GlobalConfigQuery<AppConfig> {

        public GetConfigRequest() {
            super(AppConfig.class);
        }

        @Override
        public AppConfig loadDataFromNetwork() throws Exception {
            return getService().getConfig();
        }
    }
}
