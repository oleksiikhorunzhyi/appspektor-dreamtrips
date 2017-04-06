package com.worldventures.dreamtrips.api.friends;

import com.google.gson.annotations.SerializedName;
import com.worldventures.dreamtrips.api.api_common.AuthorizedHttpAction;
import com.worldventures.dreamtrips.api.friends.model.FriendRequestParams;
import com.worldventures.dreamtrips.api.friends.model.FriendRequestResponse;

import org.immutables.gson.Gson;
import org.immutables.value.Value;

import java.util.Collections;
import java.util.List;

import io.techery.janet.http.annotations.Body;
import io.techery.janet.http.annotations.HttpAction;
import io.techery.janet.http.annotations.Response;

import static com.worldventures.dreamtrips.api.friends.ImmutableAnswerFriendRequestsHttpAction.ActionBody.of;
import static io.techery.janet.http.annotations.HttpAction.Method.PATCH;

@Value.Enclosing
@Gson.TypeAdapters
@HttpAction(value = "/api/social/friends/request_responses", method = PATCH)
public class AnswerFriendRequestsHttpAction extends AuthorizedHttpAction {

    @Body
    public final ActionBody body;

    @Response
    List<FriendRequestResponse> responses;

    public AnswerFriendRequestsHttpAction(FriendRequestParams params) {
        this(Collections.singletonList(params));
    }

    public AnswerFriendRequestsHttpAction(List<FriendRequestParams> params) {
        this.body = of(params);
    }

    public List<FriendRequestResponse> response() {
        return responses;
    }

    @Value.Immutable
    @Gson.TypeAdapters
    public interface ActionBody {
        @Value.Parameter
        @SerializedName("responses")
        List<FriendRequestParams> responses();
    }
}
