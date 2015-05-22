package com.worldventures.dreamtrips.modules.common.api;

import com.octo.android.robospice.request.retrofit.RetrofitSpiceRequest;
import com.worldventures.dreamtrips.core.api.SharedServicesApi;
import com.worldventures.dreamtrips.modules.common.model.StaticPageConfig;

public class StaticPagesQuery extends RetrofitSpiceRequest<StaticPageConfig, SharedServicesApi> {

    private String language;
    private String country;

    public StaticPagesQuery(String country, String language) {
        super(StaticPageConfig.class, SharedServicesApi.class);
        this.language = language;
        this.country = country;
    }

    @Override
    public StaticPageConfig loadDataFromNetwork() throws Exception {
        return getService().getStaticConfig("DTApp", country, language);
    }
}
