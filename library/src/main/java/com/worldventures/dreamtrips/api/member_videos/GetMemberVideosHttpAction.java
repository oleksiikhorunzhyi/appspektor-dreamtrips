package com.worldventures.dreamtrips.api.member_videos;

import com.worldventures.dreamtrips.api.api_common.AuthorizedHttpAction;
import com.worldventures.dreamtrips.api.member_videos.model.VideoCategory;
import com.worldventures.dreamtrips.api.member_videos.model.VideoLanguage;
import com.worldventures.dreamtrips.api.member_videos.model.VideoType;

import java.util.List;

import io.techery.janet.http.annotations.HttpAction;
import io.techery.janet.http.annotations.Query;
import io.techery.janet.http.annotations.Response;

@HttpAction("/api/member_videos")
public class GetMemberVideosHttpAction extends AuthorizedHttpAction {

    @Query("type")
    public final VideoType type;

    @Query("locale")
    public String locale;

    @Response
    List<VideoCategory> categories;

    public GetMemberVideosHttpAction(VideoType type) {
        this.type = type;
    }

    public GetMemberVideosHttpAction(VideoType type, VideoLanguage language) {
        this.type = type;
        this.locale = language.localeName();
    }

    public List<VideoCategory> response() {
        return categories;
    }
}
