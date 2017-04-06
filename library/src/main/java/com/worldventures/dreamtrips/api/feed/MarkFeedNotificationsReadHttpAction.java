package com.worldventures.dreamtrips.api.feed;

import com.google.gson.annotations.SerializedName;
import com.worldventures.dreamtrips.api.api_common.AuthorizedHttpAction;

import org.immutables.gson.Gson;
import org.immutables.value.Value;
import org.jetbrains.annotations.Nullable;

import java.util.Date;

import io.techery.janet.http.annotations.HttpAction;
import io.techery.janet.http.annotations.Query;

import static com.worldventures.dreamtrips.util.DateTimeUtils.DEFAULT_ISO_FORMAT_WITH_TIMEZONE;
import static com.worldventures.dreamtrips.util.DateTimeUtils.convertDateToString;
import static io.techery.janet.http.annotations.HttpAction.Method.PUT;
import static io.techery.janet.http.annotations.HttpAction.Type.FORM_URL_ENCODED;

@Value.Enclosing
@Gson.TypeAdapters
@HttpAction(value = "api/social/notifications", method = PUT, type = FORM_URL_ENCODED)
public class MarkFeedNotificationsReadHttpAction extends AuthorizedHttpAction {

    @Query("since")
    public final String since;

    @Query("before")
    public final String before;

    public MarkFeedNotificationsReadHttpAction(Params params) {
        before = params.before() == null ? null : convertDateToString(DEFAULT_ISO_FORMAT_WITH_TIMEZONE, params.before());
        since = params.since() == null ? null : convertDateToString(DEFAULT_ISO_FORMAT_WITH_TIMEZONE, params.since());
    }

    @Gson.TypeAdapters
    @Value.Immutable
    public interface Params {
        @Nullable
        @Value.Parameter
        @SerializedName("since_date")
        Date since();
        @Nullable
        @Value.Parameter
        @SerializedName("before_date")
        Date before();
    }
}
