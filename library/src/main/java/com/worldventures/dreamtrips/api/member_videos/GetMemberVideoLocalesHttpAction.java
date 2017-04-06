package com.worldventures.dreamtrips.api.member_videos;

import com.worldventures.dreamtrips.api.api_common.AuthorizedHttpAction;
import com.worldventures.dreamtrips.api.member_videos.model.VideoLocale;

import java.util.List;

import io.techery.janet.http.annotations.HttpAction;
import io.techery.janet.http.annotations.Response;

@HttpAction("/api/member_videos/locales")
public class GetMemberVideoLocalesHttpAction extends AuthorizedHttpAction {

    @Response
    List<VideoLocale> locales;

    public List<VideoLocale> response() {
        return locales;
    }
}
