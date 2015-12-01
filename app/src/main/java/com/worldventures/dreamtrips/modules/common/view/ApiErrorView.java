package com.worldventures.dreamtrips.modules.common.view;

import com.worldventures.dreamtrips.core.api.error.ErrorResponse;
import com.worldventures.dreamtrips.modules.common.presenter.Presenter;

public interface ApiErrorView extends Presenter.View {

    /**
     * Called in actual view implementation when api error occurs
     *
     * @param errorResponse list of which fields was failed {@link ErrorResponse}
     * @return {@code true} - if error was handled, {@code false} - if should be handled upper.
     */
    boolean onApiError(ErrorResponse errorResponse);

    /**
     * Called if api call failed for some reason, here you can close progress dialog etc.
     */
    void onApiCallFailed();

}
