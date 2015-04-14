package com.worldventures.dreamtrips.modules.common.api;

import com.octo.android.robospice.request.retrofit.RetrofitSpiceRequest;
import com.worldventures.dreamtrips.core.api.SharedServicesApi;
import com.worldventures.dreamtrips.modules.common.model.StaticPageConfig;

public class StaticPagesQuery extends RetrofitSpiceRequest<StaticPageConfig, SharedServicesApi> {

    public StaticPagesQuery() {
        super(StaticPageConfig.class, SharedServicesApi.class);
    }

    @Override
    public StaticPageConfig loadDataFromNetwork() throws Exception {
        return getService().getStaticConfig();
    }
}
