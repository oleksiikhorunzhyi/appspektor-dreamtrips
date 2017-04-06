package com.worldventures.dreamtrips.api.bucketlist;

import com.worldventures.dreamtrips.api.api_common.AuthorizedHttpAction;
import com.worldventures.dreamtrips.api.bucketlist.model.BucketItemSimple;
import com.worldventures.dreamtrips.api.bucketlist.model.BucketStatus;
import com.worldventures.dreamtrips.api.bucketlist.model.BucketType;

import org.immutables.value.Value;
import org.jetbrains.annotations.Nullable;

import java.util.List;

import io.techery.janet.http.annotations.HttpAction;
import io.techery.janet.http.annotations.Path;
import io.techery.janet.http.annotations.Query;
import io.techery.janet.http.annotations.Response;

@Value.Enclosing
@HttpAction("/api/users/{user_id}/bucket_list_items")
public class GetBucketItemsForUserHttpAction extends AuthorizedHttpAction {

    @Path("user_id")
    public final int userId;

    @Query("type")
    public final BucketType type;

    @Query("status")
    public final BucketStatus status;

    @Response
    List<BucketItemSimple> items;

    public List<BucketItemSimple> response() {
        return items;
    }

    public GetBucketItemsForUserHttpAction(Params params) {
        this.userId = params.userId();
        this.type = params.type();
        this.status = params.status();
    }

    @Value.Immutable
    public static abstract class Params {
        @Value.Parameter
        public abstract int userId();
        @Nullable
        public abstract BucketType type();
        @Nullable
        public abstract BucketStatus status();
    }
}
