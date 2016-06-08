package com.worldventures.dreamtrips.modules.common.api.janet;

import com.worldventures.dreamtrips.modules.common.model.StaticPageConfig;

import io.techery.janet.http.annotations.HttpAction;
import io.techery.janet.http.annotations.Query;
import io.techery.janet.http.annotations.Response;
import io.techery.janet.http.annotations.Url;

@HttpAction
public class GetStaticPagesHttpAction {

    @Url
    final String url;

    @Response
    StaticPageConfig staticPageConfig;

    @Query("dt")
    final String dt = "DTApp";
    @Query("cn")
    final String country;
    @Query("lc")
    final String language;

    public GetStaticPagesHttpAction(String url, String country, String language) {
        this.country = country;
        this.language = language;
        this.url = url;
    }

    public StaticPageConfig getStaticPageConfig() {
        return staticPageConfig;
    }
}