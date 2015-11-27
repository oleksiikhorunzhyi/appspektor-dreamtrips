package com.worldventures.dreamtrips.modules.common.view;

import com.worldventures.dreamtrips.core.api.error.ErrorResponse;
import com.worldventures.dreamtrips.modules.common.presenter.Presenter;

import java.util.Map;

public interface ApiErrorView extends Presenter.View {

    /**
     * Called in actual view implementation when api error occurs
     *
     * @param fieldsFailed map of which fields was failed {@link ErrorResponse}
     * @return {@code true} - if error was handled, {@code false} - if should be handled upper.
     */
    boolean onApiError(Map<String, String[]> fieldsFailed);

}
