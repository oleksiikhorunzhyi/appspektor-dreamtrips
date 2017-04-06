package com.worldventures.dreamtrips.api.photos;

import com.google.gson.annotations.SerializedName;
import com.worldventures.dreamtrips.api.api_common.AuthorizedHttpAction;
import com.worldventures.dreamtrips.api.photos.model.PhotoTag;
import com.worldventures.dreamtrips.api.photos.model.PhotoTagParams;

import org.immutables.gson.Gson;
import org.immutables.value.Value;

import java.util.List;

import io.techery.janet.http.annotations.Body;
import io.techery.janet.http.annotations.HttpAction;
import io.techery.janet.http.annotations.Path;
import io.techery.janet.http.annotations.Response;

import static com.worldventures.dreamtrips.api.photos.ImmutableAddUserTagsToPhotoHttpAction.ActionBody.of;
import static io.techery.janet.http.annotations.HttpAction.Method.POST;

@Value.Enclosing
@Gson.TypeAdapters
@HttpAction(value = "/api/photos/{uid}/tags", method = POST)
public class AddUserTagsToPhotoHttpAction extends AuthorizedHttpAction {

    @Path("uid")
    public final String photoUid;

    @Body
    public final ActionBody params;

    @Response
    List<PhotoTag> tags;

    public AddUserTagsToPhotoHttpAction(String photoUid, List<PhotoTagParams> tagParams) {
        this.photoUid = photoUid;
        this.params = of(tagParams);
    }

    public List<PhotoTag> response() {
        return tags;
    }

    @Value.Immutable
    @Gson.TypeAdapters
    public interface ActionBody {
        @Value.Parameter
        @SerializedName("tags")
        List<PhotoTagParams> tagParams();
    }
}
