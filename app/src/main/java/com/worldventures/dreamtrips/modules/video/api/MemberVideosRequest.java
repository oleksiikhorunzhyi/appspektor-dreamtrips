package com.worldventures.dreamtrips.modules.video.api;

import com.worldventures.dreamtrips.core.api.request.Query;
import com.worldventures.dreamtrips.modules.video.model.Video;

import java.util.ArrayList;

public class MemberVideosRequest extends Query<ArrayList<Video>> {

    private String type;
    private String country;
    private String locale;

    public MemberVideosRequest(String type, String country, String locale) {
        super((Class<ArrayList<Video>>) new ArrayList<Video>().getClass());
        this.type = type;
        this.country = country;
        this.locale = locale;
    }


    public MemberVideosRequest(String type) {
        this(type, null);
    }

    public MemberVideosRequest(String type, String country) {
        this(type, country, null);
    }


    @Override
    public ArrayList<Video> loadDataFromNetwork() throws Exception {
        if (country != null) return getService().getVideos(type, country, locale);
        return getService().getVideos(type);
    }
}
