package com.worldventures.dreamtrips.api.api_common;

import com.worldventures.dreamtrips.api.feed.model.FeedParams;
import com.worldventures.dreamtrips.util.DateTimeUtils;
import com.worldventures.dreamtrips.util.Preconditions;

import java.util.Date;

import io.techery.janet.http.annotations.Query;
import rx.functions.Func0;

import static com.worldventures.dreamtrips.util.DateTimeUtils.DEFAULT_ISO_FORMAT_WITH_TIMEZONE;

public abstract class PaginatedFeedHttpAction extends AuthorizedHttpAction {

    @Query("per_page")
    public final int pageSize;

    @Query("before")
    public final String beforeDate;

    public PaginatedFeedHttpAction(FeedParams params) {
        this(params.before(), params.pageSize());
    }

    public PaginatedFeedHttpAction(final Date before, final int pageSize) {
        this.pageSize = pageSize;
        this.beforeDate = DateTimeUtils.convertDateToString(DEFAULT_ISO_FORMAT_WITH_TIMEZONE, before);

        Preconditions.check(new Func0<Boolean>() {
            @Override
            public Boolean call() {
                return pageSize > 0;
            }
        }, "Page params are not valid: Page should be > 0");
    }
}
