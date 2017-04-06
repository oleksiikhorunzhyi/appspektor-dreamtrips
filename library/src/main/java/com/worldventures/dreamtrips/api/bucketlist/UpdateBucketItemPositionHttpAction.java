package com.worldventures.dreamtrips.api.bucketlist;

import com.google.gson.annotations.SerializedName;
import com.worldventures.dreamtrips.api.api_common.AuthorizedHttpAction;
import com.worldventures.dreamtrips.util.Preconditions;

import io.techery.janet.http.annotations.Body;
import io.techery.janet.http.annotations.HttpAction;
import io.techery.janet.http.annotations.Path;
import rx.functions.Func0;

import static io.techery.janet.http.annotations.HttpAction.Method.PUT;

@HttpAction(value = "/api/bucket_list_items/{uid}/position", method = PUT)
public class UpdateBucketItemPositionHttpAction extends AuthorizedHttpAction {

    @Path("uid")
    public final String uid;

    @Body
    public final ActionBody body;

    public UpdateBucketItemPositionHttpAction(String uid, final int position) {
        this.uid = uid;
        this.body = new ActionBody(position);
        //
        Preconditions.check(new Func0<Boolean>() {
            @Override
            public Boolean call() {
                return position >= 0;
            }
        }, "Item position can't be < 0");
    }

    private static class ActionBody {
        @SerializedName("position")
        public final int position;

        public ActionBody(int position) {
            this.position = position;
        }
    }
}
