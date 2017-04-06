package com.worldventures.dreamtrips.api.photos;

import com.google.gson.annotations.SerializedName;
import com.worldventures.dreamtrips.api.api_common.AuthorizedHttpAction;

import java.util.List;

import io.techery.janet.http.annotations.Body;
import io.techery.janet.http.annotations.HttpAction;
import io.techery.janet.http.annotations.Path;

import static io.techery.janet.http.annotations.HttpAction.Method.DELETE;

@HttpAction(value = "/api/photos/{uid}/tags", method = DELETE)
public class RemoveUserTagsFromPhotoHttpAction extends AuthorizedHttpAction {

    @Path("uid")
    public final String photoUid;

    @Body
    public final ActionBody params;

    public RemoveUserTagsFromPhotoHttpAction(String photoUid, List<Integer> userIds) {
        this.photoUid = photoUid;
        this.params = new ActionBody(userIds);
    }

    public static class ActionBody {
        @SerializedName("user_ids")
        public final List<Integer> userIds;

        public ActionBody(List<Integer> userIds) {
            this.userIds = userIds;
        }
    }
}
