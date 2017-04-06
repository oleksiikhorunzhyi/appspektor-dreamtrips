package com.worldventures.dreamtrips.api.api_common;

import com.worldventures.dreamtrips.api.api_common.model.PaginatedParams;
import com.worldventures.dreamtrips.util.Preconditions;

import io.techery.janet.http.annotations.Query;
import rx.functions.Func0;

public abstract class PaginatedHttpAction extends AuthorizedHttpAction {

    @Query("page")
    public final int page;

    @Query("per_page")
    public final int perPage;

    public PaginatedHttpAction(PaginatedParams params) {
        this(params.page(), params.perPage());
    }

    public PaginatedHttpAction(final int page, final int perPage) {
        this.page = page;
        this.perPage = perPage;

        Preconditions.check(new Func0<Boolean>() {
            @Override
            public Boolean call() {
                return page > 0 && perPage > 0;
            }
        }, "Page params are not valid: Page should be > 0, Per Page > 0");
    }

}
