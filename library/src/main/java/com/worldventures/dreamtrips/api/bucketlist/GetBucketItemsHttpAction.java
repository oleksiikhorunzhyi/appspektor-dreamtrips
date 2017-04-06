package com.worldventures.dreamtrips.api.bucketlist;

import com.worldventures.dreamtrips.api.api_common.AuthorizedHttpAction;
import com.worldventures.dreamtrips.api.bucketlist.model.BucketItemSimple;
import com.worldventures.dreamtrips.api.bucketlist.model.BucketStatus;
import com.worldventures.dreamtrips.api.bucketlist.model.BucketType;

import org.immutables.value.Value;
import org.jetbrains.annotations.Nullable;

import java.util.List;

import io.techery.janet.http.annotations.HttpAction;
import io.techery.janet.http.annotations.Query;
import io.techery.janet.http.annotations.Response;

import static com.worldventures.dreamtrips.api.bucketlist.ImmutableGetBucketItemsHttpAction.Params.builder;

@Value.Enclosing
@HttpAction("/api/bucket_list_items")
public class GetBucketItemsHttpAction extends AuthorizedHttpAction {

    @Query("type")
    public final BucketType type;

    @Query("status")
    public final BucketStatus status;

    @Response
    List<BucketItemSimple> items;

    public List<BucketItemSimple> response() {
        return items;
    }

    public GetBucketItemsHttpAction() {
        this(builder().build());
    }

    public GetBucketItemsHttpAction(Params params) {
        this.type = params.type();
        this.status = params.status();
    }

    @Value.Immutable
    public interface Params {
        @Nullable
        BucketType type();
        @Nullable
        BucketStatus status();
    }
}
